package com.dcm.backend.services;

import com.dcm.backend.dto.FileFilterDTO;
import com.dcm.backend.dto.FileHeaderDTO;
import com.dcm.backend.entities.FileHeader;
import com.dcm.backend.exceptions.FileNotFoundException;
import com.dcm.backend.exceptions.NoThumbnailException;
import io.minio.errors.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface FileService {

    /**
     * Uploads a file to the Minio server
     *
     * @param is       InputStream of the file
     * @param metadata Metadata of the file
     */
    void upload(InputStream is, FileHeaderDTO metadata) throws IOException,
            ServerException, InsufficientDataException, ErrorResponseException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException;

    /**
     * Returns the number of files in the database that match the filter
     *
     * @param  filter Filter to apply
     * @return Number of files
     */
    long count(FileFilterDTO filter);

    /**
     * Returns a page of files headers
     *
     * @param filter Filter to apply
     * @return Page of files
     */
    Page<FileHeader> getPage(FileFilterDTO filter);

    /**
     * Deletes a file from the Minio server
     *
     * @param filename Name of the file
     */
    void delete(String[] filename) throws FileNotFoundException, ServerException,
            InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException;

    /**
     * Gets a file data from the Minio server
     *
     * @param filename Name of the file
     * @return InputStreamResource of the file
     * @throws FileNotFoundException If the file is not found
     */
    InputStreamResource getFile(String filename) throws ServerException,
            InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException, FileNotFoundException;

    /**
     * Gets the thumbnail of a file
     *
     * @param filename Name of the file
     * @return InputStreamResource of the thumbnail
     * @throws FileNotFoundException If the file is not found
     * @throws NoThumbnailException  If the file has no thumbnail
     */
    InputStreamResource getThumbnail(String filename) throws ServerException,
            InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException, FileNotFoundException,
            NoThumbnailException;

    /**
     * Gets the type of file
     *
     * @param filename Name of the file
     * @return MediaType of the file
     * @throws FileNotFoundException If the file is not found
     */
    MediaType getFileType(String filename) throws FileNotFoundException;

    /**
     * Gets the link of a file
     *
     * @param filename Name of the file
     */
    String getLink(String filename) throws ServerException,
            InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException, FileNotFoundException;

    /**
     * Duplicates a file
     *
     * @param filename file to duplicate
     * @throws FileNotFoundException If the file is
     *                               not found
     */
    void duplicate(String filename) throws FileNotFoundException, ServerException,
            InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException;


    /**
     * Updates the metadata of a file
     *
     * @param filename Name of the file
     * @param metadata Metadata of the file
     */
    void update(String filename, FileHeaderDTO metadata) throws
            FileNotFoundException, ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException,
            InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException;
}
