package com.aether.engine.media;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaJobRepository extends JpaRepository<MediaJob, UUID> {
    java.util.List<MediaJob> findByAppSourceAndStatusOrderByCreatedAtDesc(String appSource, JobStatus status);

    java.util.List<MediaJob> findByStatus(JobStatus status);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE MediaJob m SET m.viewCount = COALESCE(m.viewCount, 0) + 1 WHERE m.id = :id")
    void incrementViewCount(@org.springframework.data.repository.query.Param("id") UUID id);
}
