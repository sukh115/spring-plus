package org.example.expert.domain.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class S3Uploader {

    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/gif", "image/webp");
    private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".gif", ".webp");
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String dir) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("허용되지 않는 이미지 형식입니다.");
        }

        String extension = extractExtension(file);
        String key = dir + "/" + UUID.randomUUID() + extension;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return key;

        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    private String extractExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("파일 이름이 유효하지 않습니다.");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("이미지 확장자만 업로드할 수 있습니다.");
        }

        return extension;
    }

    public String getFileUrl(String key) {
        return s3Client.utilities().getUrl(builder ->
                builder.bucket(bucketName).key(key)
        ).toExternalForm();
    }
}
