package com.dcm.backend.services;

import com.dcm.backend.dto.FileFilterDTO;
import com.dcm.backend.dto.FileHeaderDTO;
import com.dcm.backend.dto.FilenameDTO;
import com.dcm.backend.exceptions.FileAlreadyPresentException;
import com.dcm.backend.exceptions.FileNotFoundException;
import com.dcm.backend.exceptions.NoThumbnailException;
import io.minio.errors.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface FileService {

    /**
     * Uploads a file to the Minio server
     *
     * @param is       InputStream of the file
     * @param metadata Metadata of the file
     */
    void upload(InputStream is, FileHeaderDTO metadata) throws IOException,
            NoSuchAlgorithmException, InvalidKeyException, FileAlreadyPresentException,
            MinioException;

    /**
     * Returns the number of files in the database that match the filter
     *
     * @param filter Filter to apply
     * @return Number of files
     */
    long count(FileFilterDTO filter);

    /**
     * Returns a page of files headers
     *
     * @param filter Filter to apply
     * @return Page of files
     */
    List<FileHeaderDTO> getFiles(FileFilterDTO filter);

    /**
     * Deletes a file from the Minio server
     *
     * @param file DTO containing the folder and name of the file
     */
    void delete(FilenameDTO file) throws FileNotFoundException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, MinioException;

    /**
     * Gets a file data from the Minio server
     *
     * @param file DTO containing the folder and name of the file
     * @return InputStreamResource of the file
     * @throws FileNotFoundException If the file is not found
     */
    InputStreamResource getFile(FilenameDTO file) throws IOException,
            NoSuchAlgorithmException, InvalidKeyException, FileNotFoundException,
            MinioException;

    /**
     * Gets the thumbnail of a file
     *
     * @param file DTO containing the folder and name of the file
     * @return InputStreamResource of the thumbnail
     * @throws FileNotFoundException If the file is not found
     * @throws NoThumbnailException  If the file has no thumbnail
     */
    InputStreamResource getThumbnail(FilenameDTO file) throws IOException,
            NoSuchAlgorithmException, InvalidKeyException, FileNotFoundException,
            NoThumbnailException, MinioException;

    /**
     * Gets the type of file
     *
     * @param file DTO containing the folder and name of the file
     * @return MediaType of the file
     * @throws FileNotFoundException If the file is not found
     */
    MediaType getFileType(FilenameDTO file) throws FileNotFoundException;

    /**
     * Gets the link of a file
     *
     * @param file DTO containing the folder and name of the file
     */
    String getLink(FilenameDTO file) throws IOException, NoSuchAlgorithmException,
            InvalidKeyException, FileNotFoundException, MinioException;

    /**
     * Duplicates a file
     *
     * @param file DTO containing the folder and name of the file
     * @throws FileNotFoundException If the file is not found
     */
    void duplicate(FilenameDTO file) throws FileNotFoundException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, MinioException;

    /**
     * Updates the metadata of a file
     *
     * @param file     DTO containing the folder and name of the file
     * @param metadata Metadata of the file
     */
    void update(FilenameDTO file, FileHeaderDTO metadata) throws FileNotFoundException,
            ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException;

    /**
     * Gets the types of files in a folder
     *
     * @param folder Folder to get the types from
     * @return List of types
     */
    Collection<String> getTypes(String folder);

    /**
     * Get the number of new files since a certain date
     *
     * @return Number of new files for each subfolders (see com.dcm.backend.enumeration
     * .Subfolders)
     */
    Collection<Long> getNewStats(LocalDate dateFrom);

    /**
     * Get the number of files with each status
     *
     * @return Number of files for each status (see com.dcm.backend.enumeration.Status)
     */
    Collection<Long> getStatusStats();

    /**
     * Searches for files
     *
     * @param query Query to search
     * @return List of files
     */
    List<FileHeaderDTO> search(String query);

    /**
     * Retrieves the metadata of a file
     *
     * @param id Id of the file
     * @return Metadata of the file
     */
    FileHeaderDTO getFileHeader(int id);
}

