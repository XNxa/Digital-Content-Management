package com.dcm.backend.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Autowired
    private MinioProperties mp;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(mp.getEndpoint())
                .credentials(mp.getAccessKey(), mp.getSecretKey())
                .build();
    }

}
