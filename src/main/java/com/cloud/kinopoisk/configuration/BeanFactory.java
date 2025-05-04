package com.cloud.kinopoisk.configuration;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanFactory {
    @Value("${minio.url}")
    private String minioUrl; //TODO k8s

    @Value("${minio.login}")
    private String minioLogin; //TODO k8s

    @Value("${minio.password}")
    private String minioPassword; //TODO k8s

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(minioLogin, minioPassword)
                .build();

    }
}
