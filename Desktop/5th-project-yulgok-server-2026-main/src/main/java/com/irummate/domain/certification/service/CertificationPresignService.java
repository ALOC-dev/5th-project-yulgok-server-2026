package com.irummate.domain.certification.service;

import com.irummate.domain.certification.dto.CertificationPresignRequestDto;
import com.irummate.domain.certification.dto.CertificationPresignResponseDto;
import com.irummate.domain.user.entity.UserRole;
import com.irummate.domain.user.entity.UserStatus;
import com.irummate.domain.user.entity.Users;
import com.irummate.domain.user.repository.UserDetailsRepository;
import com.irummate.domain.user.repository.UsersRepository;
import com.irummate.global.exception.BusinessException;
import com.irummate.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CertificationPresignService {

    private static final Duration EXPIRES_IN = Duration.ofMinutes(10);
    private static final DateTimeFormatter AMZ_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
            .withZone(ZoneOffset.UTC);
    private static final DateTimeFormatter DATE_SCOPE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd")
            .withZone(ZoneOffset.UTC);

    private final UsersRepository usersRepository;
    private final UserDetailsRepository userDetailsRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.credentials.access-key-id}")
    private String accessKeyId;

    @Value("${aws.credentials.secret-access-key}")
    private String secretAccessKey;

    @Value("${aws.credentials.session-token:}")
    private String sessionToken;

    public CertificationPresignResponseDto createUploadUrl(Long userId, CertificationPresignRequestDto requestDto) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        validateEligible(userId, user);

        String extension = extractExtension(requestDto.getFileName());
        String fileKey = "certifications/" + user.getId() + "/" + requestDto.getSemester() + "/"
                + UUID.randomUUID() + extension;

        Instant now = Instant.now();
        String amzDate = AMZ_DATE_FORMAT.format(now);
        String dateScope = DATE_SCOPE_FORMAT.format(now);
        String credentialScope = dateScope + "/" + region + "/s3/aws4_request";
        String host = bucketName + ".s3." + region + ".amazonaws.com";
        String canonicalUri = "/" + encodePath(fileKey);

        String signedHeaders = buildSignedHeaders(requestDto.getContentType());
        String canonicalQueryString = buildCanonicalQueryString(amzDate, credentialScope, signedHeaders);
        String canonicalHeaders = buildCanonicalHeaders(host, requestDto.getContentType());
        String payloadHash = "UNSIGNED-PAYLOAD";

        String canonicalRequest = String.join("\n",
                "PUT",
                canonicalUri,
                canonicalQueryString,
                canonicalHeaders,
                signedHeaders,
                payloadHash
        );

        String stringToSign = String.join("\n",
                "AWS4-HMAC-SHA256",
                amzDate,
                credentialScope,
                hexSha256(canonicalRequest)
        );

        byte[] signingKey = deriveSigningKey(secretAccessKey, dateScope, region, "s3");
        String signature = HexFormat.of().formatHex(hmacSha256(signingKey, stringToSign));

        String uploadUrl = "https://" + host + canonicalUri + "?" + canonicalQueryString + "&X-Amz-Signature=" + signature;

        return CertificationPresignResponseDto.builder()
                .uploadUrl(uploadUrl)
                .fileKey(fileKey)
                .expiresAt(LocalDateTime.ofInstant(now.plus(EXPIRES_IN), ZoneOffset.UTC))
                .build();
    }

    private void validateEligible(Long userId, Users user) {
        if (user.getRole() != UserRole.USER || user.getStatus() != UserStatus.PENDING) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "USER/PENDING 상태에서만 업로드 URL을 발급할 수 있습니다.");
        }

        if (!userDetailsRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_DETAILS_REQUIRED);
        }
    }

    private String buildCanonicalQueryString(String amzDate, String credentialScope, String signedHeaders) {
        StringBuilder builder = new StringBuilder();
        appendQueryParam(builder, "X-Amz-Algorithm", "AWS4-HMAC-SHA256");
        appendQueryParam(builder, "X-Amz-Credential", accessKeyId + "/" + credentialScope);
        appendQueryParam(builder, "X-Amz-Date", amzDate);
        appendQueryParam(builder, "X-Amz-Expires", String.valueOf(EXPIRES_IN.toSeconds()));
        if (sessionToken != null && !sessionToken.isBlank()) {
            appendQueryParam(builder, "X-Amz-Security-Token", sessionToken);
        }
        appendQueryParam(builder, "X-Amz-SignedHeaders", signedHeaders);
        return builder.toString();
    }

    private void appendQueryParam(StringBuilder builder, String key, String value) {
        if (builder.length() > 0) {
            builder.append('&');
        }
        builder.append(urlEncode(key, true)).append('=').append(urlEncode(value, true));
    }

    private String buildCanonicalHeaders(String host, String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return "host:" + host + "\n";
        }

        return "content-type:" + contentType + "\n"
                + "host:" + host + "\n";
    }

    private String buildSignedHeaders(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return "host";
        }

        return "content-type;host";
    }

    private String extractExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex).toLowerCase(Locale.ROOT);
    }

    private String encodePath(String value) {
        return urlEncode(value, false).replace("%2F", "/");
    }

    private String urlEncode(String value, boolean encodeSlash) {
        String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8)
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~");
        return encodeSlash ? encoded : encoded.replace("%2F", "/");
    }

    private String hexSha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to hash canonical request.", e);
        }
    }

    private byte[] deriveSigningKey(String secret, String date, String region, String service) {
        byte[] kDate = hmacSha256(("AWS4" + secret).getBytes(StandardCharsets.UTF_8), date);
        byte[] kRegion = hmacSha256(kDate, region);
        byte[] kService = hmacSha256(kRegion, service);
        return hmacSha256(kService, "aws4_request");
    }

    private byte[] hmacSha256(byte[] key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to calculate HMAC.", e);
        }
    }
}
