package com.dcm.backend.services.impl;

import com.dcm.backend.annotations.LogEvent;
import com.dcm.backend.config.ApplicationProperties;
import com.dcm.backend.dto.FileFilterDTO;
import com.dcm.backend.dto.FileHeaderDTO;
import com.dcm.backend.dto.FilenameDTO;
import com.dcm.backend.entities.FileHeader;
import com.dcm.backend.entities.FileHeaderElastic;
import com.dcm.backend.entities.Keyword;
import com.dcm.backend.enumeration.Folders;
import com.dcm.backend.enumeration.Status;
import com.dcm.backend.enumeration.Subfolders;
import com.dcm.backend.exceptions.FileAlreadyPresentException;
import com.dcm.backend.exceptions.FileNotFoundException;
import com.dcm.backend.exceptions.IncoherentStateException;
import com.dcm.backend.exceptions.NoThumbnailException;
import com.dcm.backend.repositories.FileElasticRepository;
import com.dcm.backend.repositories.FileRepository;
import com.dcm.backend.repositories.specifications.FileFilterSpecification;
import com.dcm.backend.services.FileService;
import com.dcm.backend.services.KeywordService;
import com.dcm.backend.services.MinioService;
import com.dcm.backend.services.ThumbnailService;
import com.dcm.backend.utils.mappers.FileHeaderMapper;
import io.minio.errors.*;
import jakarta.persistence.EntityManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private ApplicationProperties ap;

    @Autowired
    MinioService minioService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private FileElasticRepository fileElasticRepository;

    @Autowired
    private KeywordService keywordService;

    @Autowired
    private ThumbnailService thumbnailService;

    @Autowired
    private FileHeaderMapper fileHeaderMapper;

    @LogEvent
    @Override
    public void upload(InputStream is, FileHeaderDTO metadata) throws MinioException,
            IOException, NoSuchAlgorithmException, InvalidKeyException,
            FileAlreadyPresentException {

        if (fileRepository.findByFolderAndFilename(metadata.getFolder(),
                metadata.getFilename()).isPresent()) {
            throw new FileAlreadyPresentException(
                    "upload : " + metadata.getFilename() + " already exists in " + metadata.getFolder());
        }

        // Clone the InputStream to be able to read it twice
        byte[] bytes = is.readAllBytes();
        is.close();
        ByteArrayInputStream is1 = new ByteArrayInputStream(bytes);
        ByteArrayInputStream is2 = new ByteArrayInputStream(bytes);

        Collection<Keyword> keywordCollection = getKeywords(metadata);
        BufferedImage thumbnail = generateThumbnail(is1, metadata.getType());
        uploadFileToMinio(is2, metadata, keywordCollection);
        uploadThumbnailToMinio(thumbnail, metadata);
        saveFileMetadata(metadata, thumbnail, keywordCollection);

        is1.close();
        is2.close();
    }

    @Override
    public long count(FileFilterDTO filter) {
        if (ap.isUseElasticsearch()) {
            return fileElasticRepository.countByFilter(filter).block();
        } else {
            FileFilterSpecification spec =
                    new FileFilterSpecification(filter.getFolder(), filter.getFilename(),
                            filter.getKeywords().stream().map(Keyword::new).toList(),
                            filter.getStatus(), filter.getVersion(), filter.getType(),
                            filter.getDateFrom(), filter.getDateTo());

            return fileRepository.count(spec);
        }

    }

    @Override
    public FileHeaderDTO getFileHeader(int id) throws FileNotFoundException {
        return fileHeaderMapper.toDto(fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException(
                        "getFileHeader : " + id + " not found")));
    }

    @Override
    public List<FileHeaderDTO> search(String query) {
        Pageable pageRequest = PageRequest.of(0, 10);
        if (ap.isUseElasticsearch()) {
            Flux<SearchHit<FileHeaderElastic>> flux =
                    fileElasticRepository.searchByQuery(query, pageRequest);
            return flux.collectList()
                    .block()
                    .stream()
                    .map(SearchHit::getContent)
                    .map(fileHeaderMapper::toDto)
                    .toList();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public Collection<Long> getStatusStats() {
        Collection<Long> result = new ArrayList<>();
        for (Status status : Status.values()) {
            result.add(fileRepository.countByStatus(status));
        }
        return result;
    }

    @Override
    public Collection<Long> getNewStats(LocalDate dateFrom) {
        Collection<Long> result = new ArrayList<>();
        for (String subfolder : Arrays.stream(Subfolders.values())
                .map(Enum::name)
                .toList()) {
            long i = 0;
            for (String folder : Arrays.stream(Folders.values())
                    .map(Enum::name)
                    .toList()) {
                i += fileRepository.countByDateAfterAndFolder(dateFrom,
                        folder + "/" + subfolder);
            }
            result.add(i);
        }
        return result;
    }

    @Override
    public List<FileHeaderDTO> list(FileFilterDTO filter) {
        Pageable pageRequest = PageRequest.of(filter.getPage(), filter.getSize());
        if (ap.isUseElasticsearch()) {

            Flux<SearchHit<FileHeaderElastic>> flux =
                    fileElasticRepository.findByFilter(filter, pageRequest);
            List<SearchHit<FileHeaderElastic>> list = flux.collectList().block();
            return list.stream()
                    .map(SearchHit::getContent)
                    .map(fileHeaderMapper::toDto)
                    .peek(f -> f.setThumbnail(null))
                    .toList();
        } else {
            FileFilterSpecification spec =
                    new FileFilterSpecification(filter.getFolder(), filter.getFilename(),
                            filter.getKeywords().stream().map(Keyword::new).toList(),
                            filter.getStatus(), filter.getVersion(), filter.getType(),
                            filter.getDateFrom(), filter.getDateTo());
            return fileRepository.findAll(spec, pageRequest)
                    .stream()
                    .map(fileHeaderMapper::toDto)
                    .toList();
        }
    }

    @LogEvent
    @Override
    public void delete(FilenameDTO file) throws MinioException, FileNotFoundException,
            IOException, NoSuchAlgorithmException, InvalidKeyException {

        FileHeader fileHeader = fileRepository.findByFolderAndFilename(file.getFolder(),
                        file.getFilename())
                .orElseThrow(() -> new FileNotFoundException(
                        "delete : " + file.getFilename() + " not found in " + file.getFolder()));

        minioService.removeObject(
                fileHeader.getFolder() + "/" + fileHeader.getFilename());

        if (fileHeader.getThumbnailName() != null) {
            minioService.removeObject(fileHeader.getThumbnailName());
        }

        if (ap.isUseElasticsearch()) {
            fileElasticRepository.deleteById(fileHeader.getId()).subscribe();
        }

        fileRepository.delete(fileHeader);
        keywordService.deleteUnusedKeywords();
    }

    @LogEvent
    @Override
    public InputStreamResource getFile(FilenameDTO file) throws MinioException,
            IOException, NoSuchAlgorithmException, InvalidKeyException,
            FileNotFoundException {
        FileHeader fileHeader = fileRepository.findByFolderAndFilename(file.getFolder(),
                        file.getFilename())
                .orElseThrow(() -> new FileNotFoundException(
                        "getFile : " + file.getFilename() + " not found in " + file.getFolder()));

        return new InputStreamResource(minioService.getObject(
                fileHeader.getFolder() + "/" + fileHeader.getFilename()));
    }

    @Override
    public InputStreamResource getThumbnail(FilenameDTO file) throws MinioException,
            IOException, NoSuchAlgorithmException, InvalidKeyException,
            FileNotFoundException, NoThumbnailException {
        FileHeader fileHeader = fileRepository.findByFolderAndFilename(file.getFolder(),
                        file.getFilename())
                .orElseThrow(() -> new FileNotFoundException(
                        "getThumbnail : " + file.getFilename() + " not found in " + file.getFolder()));

        if (fileHeader.getThumbnailName() == null) {
            throw new NoThumbnailException(
                    "getThumbnail : " + file.getFilename() + " has no thumbnail");
        }

        return new InputStreamResource(
                minioService.getObject(fileHeader.getThumbnailName()));
    }

    @Override
    public MediaType getFileType(FilenameDTO file) throws FileNotFoundException {
        FileHeader fileHeader = fileRepository.findByFolderAndFilename(file.getFolder(),
                        file.getFilename())
                .orElseThrow(() -> new FileNotFoundException(
                        "getFileType : " + file.getFilename() + " not found in " + file.getFolder()));

        return MediaType.parseMediaType(fileHeader.getType());
    }

    @LogEvent
    @Override
    public String getLink(FilenameDTO file) throws MinioException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, FileNotFoundException {
        FileHeader fileHeader = fileRepository.findByFolderAndFilename(file.getFolder(),
                        file.getFilename())
                .orElseThrow(() -> new FileNotFoundException(
                        "getLink : " + file.getFilename() + " not found in " + file.getFolder()));

        return minioService.getObjectUrl(
                fileHeader.getFolder() + "/" + fileHeader.getFilename(), 1,
                TimeUnit.DAYS);
    }

    @LogEvent
    @Override
    public void duplicate(FilenameDTO file) throws MinioException, FileNotFoundException,
            IOException, NoSuchAlgorithmException, InvalidKeyException {
        FileHeader fileHeader = fileRepository.findByFolderAndFilename(file.getFolder(),
                        file.getFilename())
                .orElseThrow(() -> new FileNotFoundException(
                        "duplicate : " + file.getFilename() + " not found in " + file.getFolder()));

        FileHeader newFileHeader = fileHeaderMapper.copy(fileHeader);

        updateMetadata(fileHeader, newFileHeader);

        minioService.copyObject(fileHeader.getFolder() + "/" + fileHeader.getFilename(),
                newFileHeader.getFolder() + "/" + newFileHeader.getFilename());

        if (fileHeader.getThumbnailName() != null) {
            minioService.copyObject(fileHeader.getThumbnailName(),
                    newFileHeader.getThumbnailName());
        }

        newFileHeader = fileRepository.save(newFileHeader);
        if (ap.isUseElasticsearch()) {
            FileHeaderElastic fileHeaderElastic =
                    fileHeaderMapper.toElastic(newFileHeader);
            if (fileHeader.getThumbnailName() != null) {
                FileHeaderElastic f =
                        fileElasticRepository.findById(fileHeader.getId()).block();
                if (f == null) throw new IncoherentStateException(
                        "duplicate : " + file.getFilename() + " not found in Elasticsearch");
                fileHeaderElastic.setThumbnail(f.getThumbnail());
            }
            fileElasticRepository.save(fileHeaderElastic).subscribe();
        }
    }

    @LogEvent
    @Override
    public void update(FilenameDTO file, FileHeaderDTO metadata) throws
            FileNotFoundException, ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException,
            InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        FileHeader fileHeader = fileRepository.findByFolderAndFilename(file.getFolder(),
                        file.getFilename())
                .orElseThrow(() -> new FileNotFoundException(
                        "update : " + file.getFilename() + " not found in " + file.getFolder()));
        entityManager.detach(fileHeader);

        fileHeader.setDescription(metadata.getDescription());
        fileHeader.setVersion(metadata.getVersion());
        fileHeader.setStatus(metadata.getStatus());
        fileHeader.setKeywords(getKeywords(metadata));

        fileHeader = fileRepository.save(fileHeader);

        minioService.setObjectTags(
                fileHeader.getFolder() + "/" + fileHeader.getFilename(),
                metadata.getKeywords());

        if (ap.isUseElasticsearch()) {
            FileHeaderElastic fileHeaderElastic = fileHeaderMapper.toElastic(fileHeader);
            if (fileHeader.getThumbnailName() != null) {
                FileHeaderElastic f =
                        fileElasticRepository.findById(fileHeader.getId()).block();
                if (f == null) throw new IncoherentStateException(
                        "update : " + file.getFilename() + " not found in Elasticsearch");
                fileHeaderElastic.setThumbnail(f.getThumbnail());
            }
            fileElasticRepository.save(fileHeaderElastic).subscribe();
        }
        keywordService.deleteUnusedKeywords();
    }

    @Override
    public Collection<String> getTypes(String folder) {
        return fileRepository.findTypesByFolder(folder);
    }

    private void updateMetadata(FileHeader fileHeader, FileHeader newFileHeader) {
        String newName = getNewName(fileHeader);
        newFileHeader.setFilename(newName);
        if (fileHeader.getThumbnailName() != null) newFileHeader.setThumbnailName(
                "thumbnail/" + newFileHeader.getFolder() + "/" + newFileHeader.getFilename());
        newFileHeader.setDate(LocalDate.now());
        List<Keyword> newKeywords = fileHeader.getKeywords()
                .stream()
                .map(Keyword::new)
                .collect(Collectors.toList());
        newFileHeader.setKeywords(newKeywords);
    }

    private @NotNull String getNewName(FileHeader fileHeader) {
        String baseName = fileHeader.getFilename();
        String extension = "";
        if (baseName.contains(".")) {
            baseName = fileHeader.getFilename()
                    .substring(0, fileHeader.getFilename().lastIndexOf('.'));
            extension = fileHeader.getFilename()
                    .substring(fileHeader.getFilename().lastIndexOf('.'));
        }

        String newName = baseName + "_copy" + extension;
        int copyCounter = 1;

        while (fileRepository.findByFolderAndFilename(fileHeader.getFolder(), newName)
                .isPresent()) {
            newName = baseName + "_copy" + copyCounter + extension;
            copyCounter++;
        }
        return newName;
    }

    /**
     * Generate a thumbnail for the file if it is an image or a video
     *
     * @param is   InputStream of the file
     * @param type String type of the file
     * @return BufferedImage thumbnail or null if the file is not an image or a video
     * @throws IOException if the InputStream is not valid
     */
    @Nullable
    private BufferedImage generateThumbnail(InputStream is, String type) throws
            IOException {
        BufferedImage thumbnail = null;
        if (thumbnailService.isImage(type)) {
            thumbnail =
                    thumbnailService.generateImageThumbnail(is, ThumbnailService.WIDTH,
                            ThumbnailService.HEIGHT);
        } else if (thumbnailService.isVideo(type)) {
            thumbnail =
                    thumbnailService.generateVideoThumbnail(is, ThumbnailService.WIDTH,
                            ThumbnailService.HEIGHT);
        }
        return thumbnail;
    }

    /**
     * Upload the file to Minio
     *
     * @param is       InputStream of the file
     * @param metadata FileHeaderDTO metadata of the file
     */
    private void uploadFileToMinio(InputStream is, FileHeaderDTO metadata, Collection<Keyword> keywords) throws
            MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        minioService.putObject(metadata.getFolder() + "/" + metadata.getFilename(),
                keywords.stream().map(Keyword::getName).toList(), is, metadata.getType());
    }

    /**
     * Upload the thumbnail to Minio
     *
     * @param thumbnail BufferedImage thumbnail of the file
     * @param metadata  FileHeaderDTO metadata of the file
     */
    private void uploadThumbnailToMinio(@Nullable BufferedImage thumbnail, FileHeaderDTO metadata) throws
            MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        if (thumbnail != null) {
            InputStream thumbnailInputStream =
                    thumbnailService.getInputStreamFromBufferedImage(thumbnail, "png");
            minioService.putObject(
                    "thumbnail/" + metadata.getFolder() + "/" + metadata.getFilename(),
                    List.of(), thumbnailInputStream, "image/png");
        }
    }

    /**
     * Save the file metadata to the database
     *
     * @param metadata          FileHeaderDTO metadata of the file
     * @param thumbnail         BufferedImage thumbnail of the file
     * @param keywordCollection Collection of keywords of the file
     */
    private FileHeader saveFileMetadata(FileHeaderDTO metadata, @Nullable BufferedImage thumbnail, Collection<Keyword> keywordCollection) throws
            IOException {
        FileHeader f = fileHeaderMapper.toMinimalEntity(metadata);
        f.setKeywords(keywordCollection);
        f.setDate(LocalDate.now());
        if (thumbnail != null) {
            f.setThumbnailName(
                    "thumbnail/" + metadata.getFolder() + '/' + metadata.getFilename());
        }

        f = fileRepository.save(f);

        if (ap.isUseElasticsearch()) {
            FileHeaderElastic fileHeaderElastic = fileHeaderMapper.toElastic(f);
            if (thumbnail != null) {
                fileHeaderElastic.setThumbnail(
                        thumbnailService.getByteArrayFromBufferedImage(thumbnail, "png"));
            }
            fileElasticRepository.save(fileHeaderElastic).subscribe();
        }

        return f;
    }

    /**
     * Get the keywords from the metadata (if they exist in the database, they are
     * retrieved, otherwise they are created in the database)
     *
     * @param metadata FileHeaderDTO metadata of the file
     * @return Collection of keywords
     */
    private Collection<Keyword> getKeywords(FileHeaderDTO metadata) {
        // Compute the keywords
        Collection<Keyword> keywordCollection = new LinkedList<>();
        for (String key : metadata.getKeywords()) {
            keywordCollection.add(keywordService.getOrAddKeyword(key));
        }
        return keywordCollection;
    }

}
