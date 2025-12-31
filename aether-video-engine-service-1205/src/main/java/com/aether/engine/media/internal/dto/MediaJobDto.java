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
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
}
