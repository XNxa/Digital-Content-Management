package com.dcm.backend.services.impl;

import com.dcm.backend.config.MinioProperties;
import com.dcm.backend.services.MinioService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class MinioServiceImpl implements MinioService {

    @Autowired
    MinioClient minioClient;

    @Autowired
    MinioProperties mp;

    @Override
    public void putObject(String name, Collection<String> keywords, InputStream object, String mimetype) throws
            ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        this.minioClient.putObject(PutObjectArgs.builder()
                .bucket(mp.getBucketName())
                .object(name)
                .tags(getKeywordsTags(keywords))
                .stream(object, -1, 5_000_000_000L)
                .contentType(mimetype)
                .build());
    }

    @Override
    public GetObjectResponse getObject(String name) throws ServerException,
            InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException {
        return this.minioClient.getObject(
                GetObjectArgs.builder().bucket(mp.getBucketName()).object(name).build());
    }

    @Override
    public void removeObject(String name) throws ServerException,
            InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException {
        this.minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(mp.getBucketName())
                .object(name)
                .build());
    }

    @Override
    public void copyObject(String sourceName, String targetName) throws ServerException,
            InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException {
        this.minioClient.copyObject(CopyObjectArgs.builder()
                .source(CopySource.builder()
                        .bucket(mp.getBucketName())
                        .object(sourceName)
                        .build())
                .bucket(mp.getBucketName())
                .object(targetName)
                .build());
    }

    @Override
    public void setObjectTags(String name, Collection<String> keywords) throws
            ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        this.minioClient.setObjectTags(SetObjectTagsArgs.builder()
                .bucket(mp.getBucketName())
                .object(name)
                .tags(getKeywordsTags(keywords))
                .build());
    }

    @Override
    public String getObjectUrl(String name, int duration, TimeUnit timeUnit) throws
            ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        return this.minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(mp.getBucketName())
                .object(name)
                .expiry(duration, timeUnit)
                .build());
    }

    private @NotNull Map<String, String> getKeywordsTags(Collection<String> keywords) {
        return keywords.stream().collect(Collectors.toMap(k -> k, k -> ""));
    }

}
