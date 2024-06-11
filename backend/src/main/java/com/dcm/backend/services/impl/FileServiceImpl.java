package com.dcm.backend.services.impl;

import com.dcm.backend.config.MinioConfig;
import com.dcm.backend.config.MinioProperties;
import com.dcm.backend.dto.FileHeaderDTO;
import com.dcm.backend.entities.FileHeader;
import com.dcm.backend.entities.Keyword;
import com.dcm.backend.exceptions.FileNotFoundException;
import com.dcm.backend.repositories.FileRepository;
import com.dcm.backend.repositories.KeywordRepository;
import com.dcm.backend.services.FileService;
import com.dcm.backend.services.ThumbnailService;
import io.minio.GetObjectArgs;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.*;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private MinioConfig mc;

    @Autowired
    private MinioProperties mp;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ThumbnailService thumbnailService;

    @Override
    public void upload(InputStream is, @Valid FileHeaderDTO metadata) throws IOException,
            ServerException, InsufficientDataException, ErrorResponseException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException {

        Collection<Keyword> keywordCollection = getKeywords(metadata);
        BufferedImage thumbnail = generateThumbnail(is, metadata.getType());
        uploadFileToMinio(is, metadata, keywordCollection);
        uploadThumbnailToMinio(thumbnail, metadata);
        saveFileMetadata(metadata, thumbnail, keywordCollection);
    }

    @Override
    public long count() {
        return fileRepository.count();
    }

    @Override
    public Page<FileHeader> getPage(int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size);
        return fileRepository.findAll(pageRequest);
    }

    @Override
    public void delete(String filename) throws InsufficientDataException,
            ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

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
        Optional<FileHeader> fileHeader = fileRepository.findByFilename(filename);

        if (fileHeader.isPresent()) {
            return new InputStreamResource(mc.minioClient()
                    .getObject(GetObjectArgs.builder()
                            .bucket(mp.getBucketName())
                            .object(fileHeader.get().getFilename())
                            .build()));
        } else {
            throw new FileNotFoundException("getFile : " + filename + " not found");
        }
    }

    @Override
    public MediaType getFileType(String filename) throws FileNotFoundException {
        FileHeader fileHeader = fileRepository.findByFilename(filename).orElseThrow(() ->
                new FileNotFoundException("getFileType : " + filename + " not found"));

        return MediaType.parseMediaType(fileHeader.getType());
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
            f.setThumbnailName("thumbnails_" + metadata.getFilename());
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
            Optional<Keyword> k = keywordRepository.findById(key);
            if (k.isPresent()) {
                keywordCollection.add(k.get());
            } else {
                Keyword saved = keywordRepository.save(new Keyword(key));
                keywordCollection.add(saved);
            }
        }
        return keywordCollection;
    }


}
