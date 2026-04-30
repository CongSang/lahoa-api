package com.lahoa.lahoa_be.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.lahoa.lahoa_be.util.TransactionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public Map<String, Object> generateSignature(String folder) {

        long timestamp = System.currentTimeMillis() / 1000;

        Map<String, Object> params = ObjectUtils.asMap(
                "timestamp", timestamp,
                "folder", folder
        );

        String signature = cloudinary.apiSignRequest(params, cloudinary.config.apiSecret);

        return Map.of(
                "timestamp", timestamp,
                "signature", signature,
                "apiKey", cloudinary.config.apiKey,
                "cloudName", cloudinary.config.cloudName,
                "folder", folder
        );
    }

    @Async
    public void deleteImage(String publicId) {
        if (publicId == null || publicId.isBlank()) return;

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Deleted image Cloudinary {}", publicId);
        } catch (Exception e) {
            log.error("Delete image Cloudinary failed {}", publicId, e);
            throw new RuntimeException("Xoá ảnh Cloudinary thất bại");
        }
    }

    public void deleteAfterCommit(String publicId) {
        TransactionUtils.runAfterCommit(() -> deleteImage(publicId));
    }
}
