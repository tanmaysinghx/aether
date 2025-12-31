package com.aether.engine.media.internal.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MediaJobDto {
    private UUID id;
    private String title;
    private String status;
    private Double progress;
    private String appSource;
    private String videoType;
    private String visibility;
    private String thumbnailUrl;
    private UUID uploaderId;
    private String description;
    private Double durationSeconds;
    private String language;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
}
