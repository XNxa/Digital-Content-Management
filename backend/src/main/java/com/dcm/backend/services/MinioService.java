package com.dcm.backend.services;

import io.minio.GetObjectResponse;
import io.minio.errors.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Service for interacting with the Minio server.
 */
public interface MinioService {

    /**
     * Uploads an object to the Minio server.
     *
     * @param name     the name of the object
     * @param keywords the keywords associated with the object
     * @param object   the object to upload
     * @param mimetype the mimetype of the object
     */
    void putObject(String name, Collection<String> keywords, InputStream object, String mimetype) throws
            ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException;

    /**
     * Downloads an object from the Minio server.
     *
     * @param name the name of the object
     * @return the object
     */
    GetObjectResponse getObject(String name) throws ServerException,
            InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException;

    /**
     * Remove an object from the Minio server.
     *
     * @param name the name of the object
     */
    void removeObject(String name) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException,
            InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException;

    /**
     * Duplicate an object on the Minio server.
     *
     * @param sourceName the name of the source object
     * @param targetName the name of the target object
     */
    void copyObject(String sourceName, String targetName) throws ServerException,
            InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException;

    /**
     * Set tags on an object on the Minio server.
     *
     * @param name the name of the object
     * @param keywords the keywords to set
     */
    void setObjectTags(String name, Collection<String> keywords) throws ServerException,
            InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException;

    /**
     * Generates an object's access URL.
     *
     * @param name the name of the object
     * @param duration the duration of the URL
     * @param timeUnit the time unit of the duration
     * @return the URL of the object
     */
    String getObjectUrl(String name, int duration, TimeUnit timeUnit) throws
            ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException;

}
