package com.irummate.global.s3;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Component
@AllArgsConstructor
public class S3Utils {

    private static final Duration PRESIGNED_URL_DURATION = Duration.ofMinutes(5);

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region}")
    private String region;

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    /**
     * 업로드용 Presigned URL 발급
     */
    public PresignedUrlResponse createUploadUrl(String fileName, String contentType, String dirName) {
        String key = S3UrlUtil.createKey(dirName, fileName);

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
}
