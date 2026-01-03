package com.aether.engine.media.internal.controller;

import com.aether.engine.common.ApiResponse;
import com.aether.engine.media.JobStatus;
import com.aether.engine.media.MediaJobDto;
import com.aether.engine.media.MediaService;
import com.aether.engine.media.MediaJobRepository;
import com.aether.engine.media.UploadRequest;
import com.aether.engine.media.internal.dto.MediaUploadDto;
import com.aether.engine.media.MediaJob;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Media", description = "Endpoints for video upload, view count, and job status")
@Slf4j
@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;
    private final MediaJobRepository mediaJobRepository;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<MediaJobDto> uploadVideo(
            @org.springframework.web.bind.annotation.RequestHeader(value = "X-User-Id", defaultValue = "00000000-0000-0000-0000-000000000000") String userIdHeader,
            @ModelAttribute MediaUploadDto uploadDto) {

        log.info("Received upload request for title: {}", uploadDto.getTitle());

        Map<String, Object> metadata = new HashMap<>();
        if (uploadDto.getMetadata() != null && !uploadDto.getMetadata().isEmpty()) {
            try {
                metadata = objectMapper.readValue(uploadDto.getMetadata(), Map.class);
            } catch (JsonProcessingException e) {
                log.error("Invalid metadata JSON", e);
                throw new RuntimeException("Invalid metadata JSON format");
            }
        }

        UploadRequest request = new UploadRequest(uploadDto.getTitle(), uploadDto.getAppSource(),
                uploadDto.getVideoType(), uploadDto.getVisibility(), uploadDto.getDescription(),
                uploadDto.getLanguage(), UUID.fromString(userIdHeader), metadata);
        MediaJob job = mediaService.processUpload(uploadDto.getFile(), request);

        return ApiResponse.success(mapToDto(job), "Video upload started successfully");
    }

    @PostMapping("/{id}/view")
    public ApiResponse<Void> incrementViewCount(@PathVariable UUID id) {
        mediaService.incrementViewCount(id);
        return ApiResponse.success(null, "View count incremented");
    }

    @GetMapping("/{id}")
    public ApiResponse<MediaJobDto> getJobStatus(@PathVariable UUID id) {
        MediaJob job = mediaJobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        return ApiResponse.success(mapToDto(job), "Job status retrieved");
    }

    @GetMapping("/feed")
    public ApiResponse<java.util.List<MediaJobDto>> getFeed(
            @org.springframework.web.bind.annotation.RequestParam String appSource) {
        java.util.List<MediaJob> jobs = mediaService.getFeed(appSource);
        java.util.List<MediaJobDto> dtos = jobs.stream().map(this::mapToDto).toList();
        return ApiResponse.success(dtos, "Feed retrieved successfully");
    }

    private MediaJobDto mapToDto(MediaJob job) {
        return MediaJobDto.builder()
                .id(job.getId())
                .title(job.getTitle())
                .status(job.getStatus())
                .progress(job.getProgress())
                .appSource(job.getAppSource())
                .videoType(job.getVideoType())
                .visibility(job.getVisibility())
                .thumbnailUrl(job.getThumbnailUrl())
                .uploaderId(job.getUploaderId())
                .description(job.getDescription())
                .durationSeconds(job.getDurationSeconds())
                .viewCount(job.getViewCount())
                .language(job.getLanguage())
                .metadata(job.getMetadata())
                .createdAt(job.getCreatedAt())
                .build();
    }
}
