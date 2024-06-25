package com.dcm.backend.services.impl;

import com.dcm.backend.config.MinioConfig;
import com.dcm.backend.config.MinioProperties;
import com.dcm.backend.dto.FileFilterDTO;
import com.dcm.backend.dto.FileHeaderDTO;
import com.dcm.backend.entities.FileHeader;
import com.dcm.backend.entities.Keyword;
import com.dcm.backend.enumeration.Status;
import com.dcm.backend.exceptions.FileNotFoundException;
import com.dcm.backend.exceptions.NoThumbnailException;
import com.dcm.backend.repositories.FileRepository;
import com.dcm.backend.repositories.specifications.FileFilterSpecification;
import com.dcm.backend.services.FileService;
import com.dcm.backend.services.KeywordService;
import com.dcm.backend.services.ThumbnailService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

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
    private MinioConfig mc;

    @Autowired
    private MinioProperties mp;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private KeywordService keywordService;

    @Autowired
    private ThumbnailService thumbnailService;

    @Override
    public void upload(InputStream is, FileHeaderDTO metadata) throws IOException,
            ServerException, InsufficientDataException, ErrorResponseException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException {

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
    public long count() {
        return fileRepository.count();
    }

    @Override
    public Page<FileHeader> getPage(FileFilterDTO filter) {
        Pageable pageRequest = PageRequest.of(filter.getPage(), filter.getSize());

        FileFilterSpecification spec =
                new FileFilterSpecification(filter.getFilename(), filter.getKeywords().stream().map(Keyword::new).toList(), filter.getStatus());

        return fileRepository.findAll(spec, pageRequest);
    }

    @Override
    public void delete(String[] filename) throws FileNotFoundException, ServerException,
            InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException {
        for (String f : filename) {
            FileHeader fileHeader = fileRepository.findByFilename(f).orElseThrow(
                    () -> new FileNotFoundException("delete : " + f + " not found")
            );

            mc.minioClient()
                    .removeObject(RemoveObjectArgs.builder()
                            .bucket(mp.getBucketName())
                            .object(fileHeader.getFilename())
                            .build());
            if (fileHeader.getThumbnailName() != null) {
                mc.minioClient()
                        .removeObject(RemoveObjectArgs.builder()
                                .bucket(mp.getBucketName())
                                .object(fileHeader.getThumbnailName())
                                .build());
            }

            fileRepository.delete(fileHeader);
            keywordService.deleteUnusedKeywords();
        }
    }

    @Override
    public void rename(String oldName, String newName) throws IOException,
            ServerException, InsufficientDataException, ErrorResponseException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException {

    }

    @Override
    public void getData(String filename) throws IOException, ServerException,
            InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException,
            InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

    }

    @Override
    public InputStreamResource getFile(String filename) throws ServerException,
            InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException, FileNotFoundException {
        FileHeader fileHeader = fileRepository.findByFilename(filename).orElseThrow(
                () -> new FileNotFoundException("getFile : " + filename + " not found")
        );

        return new InputStreamResource(mc.minioClient()
                .getObject(GetObjectArgs.builder()
                        .bucket(mp.getBucketName())
                        .object(fileHeader.getFilename())
                        .build()));
    }

    @Override
    public InputStreamResource getThumbnail(String filename) throws ServerException,
            InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException, FileNotFoundException,
            NoThumbnailException {
        FileHeader fileHeader = fileRepository.findByFilename(filename).orElseThrow(
                () -> new FileNotFoundException("getThumbnail : " + filename + " not " +
                        "found")
        );

        if (fileHeader.getThumbnailName() == null) {
            throw new NoThumbnailException(
                    "getThumbnail : " + filename + " no thumbnail");
        }

        return new InputStreamResource(mc.minioClient()
                .getObject(GetObjectArgs.builder()
                        .bucket(mp.getBucketName())
                        .object(fileHeader.getThumbnailName())
                        .build()));
    }

    @Override
    public MediaType getFileType(String filename) throws FileNotFoundException {
        FileHeader fileHeader = fileRepository.findByFilename(filename).orElseThrow(
                () -> new FileNotFoundException("getFileType : " + filename + " not " +
                        "found")
        );

        return MediaType.parseMediaType(fileHeader.getType());
    }

    @Override
    public String getLink(String filename) throws ServerException,
            InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException {
        return mc.minioClient().getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(mp.getBucketName())
                .object(filename)
                .expiry(1, TimeUnit.DAYS)
                .build());
    }

    @Override
    public void duplicate(String filename) throws FileNotFoundException, ServerException,
            InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException {
        FileHeader fileHeader = fileRepository.findByFilename(filename).orElseThrow(
                () -> new FileNotFoundException("duplicate : " + filename + " not found")
        );

        FileHeader newFileHeader = new FileHeader(fileHeader);
        newFileHeader.setFilename("copy_" + fileHeader.getFilename());
        newFileHeader.setThumbnailName("thumbnail_" + newFileHeader.getFilename());
        newFileHeader.setDate(LocalDate.now().toString());

        List<Keyword> newKeywords = fileHeader.getKeywords().stream()
                .map(Keyword::new)
                .collect(Collectors.toList());
        newFileHeader.setKeywords(newKeywords);

        mc.minioClient().copyObject(CopyObjectArgs.builder()
                .source(CopySource.builder()
                        .bucket(mp.getBucketName())
                        .object(fileHeader.getFilename())
                        .build())
                .bucket(mp.getBucketName())
                .object(newFileHeader.getFilename())
                .build());

        mc.minioClient().copyObject(CopyObjectArgs.builder()
                .source(CopySource.builder()
                        .bucket(mp.getBucketName())
                        .object(fileHeader.getThumbnailName())
                        .build())
                .bucket(mp.getBucketName())
                .object(newFileHeader.getThumbnailName())
                .build());

        fileRepository.save(newFileHeader);
    }

    @Override
    public void update(String filename, FileHeaderDTO metadata) throws
            FileNotFoundException, ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException,
            InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        FileHeader fileHeader = fileRepository.findByFilename(filename).orElseThrow(
                () -> new FileNotFoundException("update : " + filename + " not found")
        );

        fileHeader.setDescription(metadata.getDescription());
        fileHeader.setVersion(metadata.getVersion());
        fileHeader.setStatus(metadata.getStatus());
        fileHeader.setKeywords(getKeywords(metadata));

        mc.minioClient().setObjectTags(SetObjectTagsArgs.builder()
                .bucket(mp.getBucketName())
                .object(fileHeader.getFilename())
                .tags(metadata.getKeywords().stream()
                        .collect(Collectors.toMap(k -> k, v -> "")))
                .build());

        fileRepository.save(fileHeader);
    }

    /**
     * Generate a thumbnail for the file if it is an image or a video
     *
     * @param is   InputStream of the file
     * @param type
     * @return BufferedImage thumbnail or null if the file is not an image or a video
     * @throws IOException if the InputStream is not valid
     */
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
    private void uploadFileToMinio(InputStream is, FileHeaderDTO metadata,
                                   Collection<Keyword> keywords) throws
            IOException, ServerException, InsufficientDataException,
            ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        Map<String, String> keywordsTags = new HashMap<>();
        for (Keyword k : keywords) {
            keywordsTags.put(k.getName(), "");
        }

        mc.minioClient()
                .putObject(PutObjectArgs.builder()
                        .bucket(mp.getBucketName())
                        .object(metadata.getFilename())
                        .tags(keywordsTags)
                        .stream(is, -1, 5_000_000_000L)
                        .contentType(metadata.getType())
                        .build());
    }

    /**
     * Upload the thumbnail to Minio
     *
     * @param thumbnail BufferedImage thumbnail of the file
     * @param metadata  FileHeaderDTO metadata of the file
     */
    private void uploadThumbnailToMinio(BufferedImage thumbnail, FileHeaderDTO metadata) throws
            IOException, ServerException, InsufficientDataException,
            ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        if (thumbnail != null) {
            InputStream thumbnailInputStream =
                    thumbnailService.getInputStreamFromBufferedImage(thumbnail, "png");
            mc.minioClient().putObject(PutObjectArgs.builder()
                    .bucket(mp.getBucketName())
                    .object("thumbnail_" + metadata.getFilename())
                    .stream(thumbnailInputStream, -1, 5_000_000_000L)
                    .contentType("image/png")
                    .build());
        }
    }

    /**
     * Save the file metadata to the database
     *
     * @param metadata          FileHeaderDTO metadata of the file
     * @param thumbnail         BufferedImage thumbnail of the file
     * @param keywordCollection Collection of keywords of the file
     */
    private void saveFileMetadata(FileHeaderDTO metadata, BufferedImage thumbnail, Collection<Keyword> keywordCollection) {
        FileHeader f = new FileHeader(metadata.getFilename(), metadata.getDescription(),
                metadata.getVersion(), metadata.getStatus(), LocalDate.now().toString(),
                metadata.getType(), metadata.getSize(), keywordCollection);
        if (thumbnail != null) {
            f.setThumbnailName("thumbnail_" + metadata.getFilename());
        }
        fileRepository.save(f);
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
