package com.cloud.kinopoisk;

import com.cloud.kinopoisk.service.MinioService;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MinioServiceTests {

    @InjectMocks
    private MinioService minioService;

    @Mock
    private MinioClient minioClient;

    @PostConstruct
    void post() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        when(minioClient.putObject(any())).thenReturn(null);
        when(minioClient.bucketExists(any())).thenReturn(false);
        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));
        doNothing().when(minioClient).makeBucket(any(MakeBucketArgs.class));
    }

    @Test
    void downloadPosterSuccessfully() {
        ReflectionTestUtils.setField(minioService, "minioPublicUrl", "http://localhost:9000");
        String poster = Base64.getEncoder().encodeToString("test".getBytes(StandardCharsets.UTF_8));

        assertTrue(minioService.downloadPoster(poster, "bucket").startsWith("http://localhost:9000/bucket/"));
    }

    @Test
    void deleteFileSuccessfully()  {
        String url = "https://minio.test/bucket/test-bucket";

        assertDoesNotThrow(() -> minioService.deleteFile(url));
    }

    @Test
    void uploadFileSuccessfully() {
        String data = Base64.getEncoder().encodeToString("test".getBytes(StandardCharsets.UTF_8));

        assertDoesNotThrow(() -> minioService.uploadFile("pap-s3-storage", "test", data));
    }

    @Test
    void getFileUrlSuccessfully() {
        ReflectionTestUtils.setField(minioService, "minioPublicUrl", "http://localhost:9000");
        String expected = "http://localhost:9000/bucket/name";

        assertEquals(minioService.gitFileUrl("bucket", "name"), expected);
    }

    @Test
    void handleBucketExistsSuccessfully() {
        assertDoesNotThrow(() -> minioService.handleBucketExists("bucket"));
    }
}
