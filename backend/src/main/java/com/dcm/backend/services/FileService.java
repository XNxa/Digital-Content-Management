package com.dcm.backend.services;

import com.dcm.backend.config.MinioConfig;
import com.dcm.backend.config.MinioProperties;
import com.dcm.backend.dto.FileUploadDTO;
import com.dcm.backend.entities.File;
import com.dcm.backend.entities.Keyword;
import com.dcm.backend.repositories.FileRepository;
import com.dcm.backend.repositories.KeywordRepository;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {

    @Autowired
    private MinioConfig mc;

    @Autowired
    private MinioProperties mp;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private FileRepository fileRepository;


    public void uploadFile(FileUploadDTO file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MultipartFile multipartFile = file.getFile();

        ObjectWriteResponse response = mc.minioClient().putObject(PutObjectArgs.builder().bucket(mp.getBucketName()).object(multipartFile.getOriginalFilename()).stream(multipartFile.getInputStream(), multipartFile.getSize(), -1).contentType(multipartFile.getContentType()).build());


        Collection<Keyword> keywordCollection = new LinkedList<>();
        for (String key : file.getKeywords()) {
            Optional<Keyword> k = keywordRepository.findById(key);
            if (k.isPresent()) {
                keywordCollection.add(k.get());
            } else {
                Keyword saved = keywordRepository.save(new Keyword(key));
                keywordCollection.add(saved);
            }
        }

        fileRepository.save(new File(
                multipartFile.getOriginalFilename(),
                file.getDescription(),
                file.getVersion(),
                file.getStatus(),
                LocalDate.now().toString(),
                multipartFile.getContentType(),
                multipartFile.getSize(),
                keywordCollection
        ));
    }

    public List<File> getAll() {
        return fileRepository.findAll();
    }
}
