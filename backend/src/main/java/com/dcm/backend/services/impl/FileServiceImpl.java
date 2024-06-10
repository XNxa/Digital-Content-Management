package com.dcm.backend.services.impl;

import com.dcm.backend.config.MinioConfig;
import com.dcm.backend.config.MinioProperties;
import com.dcm.backend.dto.FileHeaderDTO;
import com.dcm.backend.entities.FileHeader;
import com.dcm.backend.entities.Keyword;
import com.dcm.backend.repositories.FileRepository;
import com.dcm.backend.repositories.KeywordRepository;
import com.dcm.backend.services.FileService;
import com.dcm.backend.services.ThumbnailService;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

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
    public void upload(InputStream is, FileHeaderDTO metadata) throws IOException,
            ServerException, InsufficientDataException, ErrorResponseException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException {

        // Generate the thumbnail if necessary
        BufferedImage thumbnail = null;
        if (thumbnailService.isImage(metadata.getType())) {
            thumbnail = thumbnailService.generateImageThumbnail(is,
                    ThumbnailService.WIDTH,
                    ThumbnailService.HEIGHT);
        } else if (thumbnailService.isVideo(metadata.getType())) {
            thumbnail = thumbnailService.generateVideoThumbnail(is,
                    ThumbnailService.WIDTH,
                    ThumbnailService.HEIGHT);
        }

        // Add the file to the Minio bucket
        mc.minioClient()
                .putObject(PutObjectArgs.builder()
                        .bucket(mp.getBucketName())
                        .object(metadata.getFilename())
                        .stream(is, metadata.getSize(), -1)
                        .contentType(metadata.getType())
                        .build());

        if (thumbnail != null) {
            InputStream thumbnailInputStream =
                    thumbnailService.getInputStreamFromBufferedImage(thumbnail, "png");

            int size = thumbnailInputStream.available();

            // Save the thumbnail
            mc.minioClient().putObject(PutObjectArgs.builder()
                    .bucket(mp.getBucketName())
                    .object("thumbnails_" + metadata.getFilename())
                    .stream(thumbnailInputStream, size, -1)
                    .contentType("image/png")
                    .build());
        }

        Collection<Keyword> keywordCollection = getKeywords(metadata);

        // Save the file metadata
        FileHeader f = new FileHeader(metadata.getFilename(), metadata.getDescription(),
                metadata.getVersion(), metadata.getStatus(), LocalDate.now().toString(),
                metadata.getType(), metadata.getSize(), keywordCollection);
        fileRepository.save(f);
    }

    public long count() {
        return fileRepository.count();
    }

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
