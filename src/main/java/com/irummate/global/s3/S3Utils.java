package com.irummate.global.s3;

import com.irummate.global.exception.BusinessException;
import com.irummate.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class S3Utils {

    private static final Duration PRESIGNED_URL_DURATION = Duration.ofMinutes(5);

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region}")
    private String region;

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;


    public String createDownloadUrl(String key) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .responseContentDisposition("inline")
                .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(PRESIGNED_URL_DURATION)
                        .getObjectRequest(objectRequest)
                        .build();

        return s3Presigner.presignGetObject(presignRequest)
                .url()
                .toString();
    }


    /**
     * 업로드용 Presigned URL 발급
     */
    public PresignedUrlResponse createUploadUrl(String fileName, String contentType, String dirName) {

        String extension = validateAndExtractExtension(fileName, contentType);

        String key = S3UrlUtil.createKey(dirName, extension);

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(PRESIGNED_URL_DURATION)
                .putObjectRequest(objectRequest)
                .build();

        String presignedUrl = s3Presigner.presignPutObject(presignRequest).url().toString();
        String fileUrl = S3UrlUtil.createFileUrl(bucket, region, key);

        return new PresignedUrlResponse(presignedUrl, key, fileUrl);
    }

    /**
     * 파일 삭제
     */
    public void delete(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
    }


    private static final Map<String, Set<String>> ALLOWED_EXTENSIONS_BY_CONTENT_TYPE = Map.of(
            "image/jpeg", Set.of("jpg", "jpeg"),
            "image/png", Set.of("png"),
            "image/webp", Set.of("webp")
    );

    private String validateAndExtractExtension(String fileName, String contentType) {
        Set<String> allowedExtensions = ALLOWED_EXTENSIONS_BY_CONTENT_TYPE.get(contentType);

        if (allowedExtensions == null) {
            throw new BusinessException(ErrorCode.INVALID_IMAGE_FORMAT);
        }

        String extension = extractExtension(fileName);

        if (!allowedExtensions.contains(extension)) {
            throw new BusinessException(ErrorCode.INVALID_IMAGE_FORMAT);
        }

        return extension.equals("jpeg") ? "jpg" : extension;
    }

    private String extractExtension(String fileName) {
        String normalized = fileName.replace("\\", "/");
        String lastFileName = normalized.substring(normalized.lastIndexOf("/") + 1);

        int dotIndex = lastFileName.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex == lastFileName.length() - 1) {
            throw new BusinessException(ErrorCode.INVALID_IMAGE_FORMAT);
        }

        return lastFileName.substring(dotIndex + 1).toLowerCase();
    }
}
