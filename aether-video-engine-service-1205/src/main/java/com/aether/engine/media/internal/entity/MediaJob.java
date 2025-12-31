package com.aether.engine.media.internal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Entity
public class MediaJob {

    @Id
    @GeneratedValue
    private UUID id;

    private String title;
    private String status; // PENDING, PROCESSING, COMPLETED, FAILED
    private Double progress; // 0.0 to 100.0
    private String appSource; // TUBE or PLAY

    private String videoType; // REEL, MOVIE, EPISODE, SHORT
    private String visibility; // PUBLIC, PRIVATE, UNLISTED
    private String thumbnailUrl;

    private UUID uploaderId;
    private String description;
    private Double durationSeconds;
    private String language; // e.g., "en", "hi"

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> metadata;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
