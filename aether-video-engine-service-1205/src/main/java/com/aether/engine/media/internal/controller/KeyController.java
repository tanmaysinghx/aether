package com.aether.engine.media.internal.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/keys")
public class KeyController {

    @Value("${app.storage.location:video_storage}")
    private String storageLocation;

    @GetMapping(value = "/{jobId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getKey(@PathVariable UUID jobId) {
        log.info("Requesting encryption key for Job ID: {}", jobId);

        // Path should match FFmpegServiceImpl: video_storage/hls/{jobId}/enc.key
        java.nio.file.Path keyPath = java.nio.file.Paths.get(storageLocation, "hls", jobId.toString(), "enc.key");
        File keyFile = keyPath.toFile();

        if (!keyFile.exists()) {
            log.error("Key file not found: {}", keyPath);
            return ResponseEntity.notFound().build();
        }

        try {
            byte[] keyBytes = Files.readAllBytes(keyPath);
            ByteArrayResource resource = new ByteArrayResource(keyBytes);
            return ResponseEntity.ok(resource);
        } catch (IOException e) {
            log.error("Error reading key file", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
