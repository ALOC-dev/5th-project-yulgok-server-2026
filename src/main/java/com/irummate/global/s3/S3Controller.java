package com.irummate.global.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/s3")
public class S3Controller {

    private final S3Utils s3Utils;

    @GetMapping("/presigned-url")
    public PresignedUrlResponse createUrl(
            @RequestParam String fileName,
            @RequestParam String contentType
    ) {
        return s3Utils.createUploadUrl(fileName, contentType, "test");
    }
}