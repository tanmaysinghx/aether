package com.aether.engine.media.internal.repository;

import com.aether.engine.media.internal.entity.MediaJob;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaJobRepository extends JpaRepository<MediaJob, UUID> {
    java.util.List<MediaJob> findByAppSourceAndStatusOrderByCreatedAtDesc(String appSource, String status);
}
