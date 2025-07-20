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

    private static final String ITEM_IMAGE_KEY = "items/";
    private static final String USER_AVATAR_KEY = "user-avatars/";

    public String uploadItemImage(long itemId, MultipartFile file) throws IOException {
        String fileName = itemId + "_" + file.getOriginalFilename();
        String key = ITEM_IMAGE_KEY + fileName;

        PutObjectRequest objRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .acl("public-read")
                .contentType(file.getContentType())
                .build();
        s3Client.putObject(objRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return String.format("https://%s.storage.yandexcloud.net/%s", bucket, key);
    }

    public String uploadUserAvatar(long userId, MultipartFile file) throws IOException {
        String fileName = userId + "_" + file.getOriginalFilename();
        String key = USER_AVATAR_KEY + fileName;

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
