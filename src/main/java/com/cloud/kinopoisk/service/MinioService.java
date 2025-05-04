package com.cloud.kinopoisk.service;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {
    @Value("${minio.url}")
    private String minioUrl;

    private final MinioClient minioClient;

    @SneakyThrows
    public String downloadPoster(String poster, String bucket) {
        String objectName = UUID.randomUUID().toString();

        handleBucketExists(bucket);

        uploadFile(bucket, objectName, poster);

        String posterUrl = gitFileUrl(bucket, objectName);

        log.info("File successfully uploaded to minio with url: {}", posterUrl);

        return posterUrl;

    }

    @SneakyThrows
    public void handleBucketExists(String bucket) {
        boolean isExistBucket = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!isExistBucket) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            log.info("Create bucket with name {}", bucket);
        }
    }

    public String gitFileUrl(String bucket, String objectName) {
        return String.format("%s/%s/%s", minioUrl, bucket, objectName);
    }

    @SneakyThrows
    public void uploadFile(String bucket, String objectName, String data) {
        byte[] decodedBytes = Base64.getDecoder().decode(data);

        try (ByteArrayInputStream bais = new ByteArrayInputStream(decodedBytes)) {

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(bais, decodedBytes.length, -1)
                    .build());
        }

    }

    @SneakyThrows
    public void deleteFile(String url) {
        String[] data = url.split("/");
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(data[data.length - 2])
                .object(data[data.length - 1])
                .build());
        log.info("Delete file with url = {} successful", url);

    }
}
