package com.dcm.backend;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.ObjectWriteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    @Autowired
    private MinioClient mc;

    public Boolean uploadFile(MultipartFile file) {
        try {
            ObjectWriteResponse response = mc.putObject(
                    PutObjectArgs.builder()
                            .bucket("mybucket") // TODO ? pass bucket in parameters
                            .object(file.getOriginalFilename())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
