package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class YandexStorageService {
    private final S3Client s3Client;

    @Value("${aws.bucket}")
    private String bucket;

    public String uploadFile(String folder, MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        String key = (folder != null ? folder + "/" : "") + filename;

        PutObjectRequest objRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .acl("public-read")
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(objRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return String.format("https://%s.storage.yandexcloud.net/%s", bucket, key);
    }
}
