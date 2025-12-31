package com.aether.engine.play.internal.repository;

import com.aether.engine.play.internal.entity.VideoProgress;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoProgressRepository extends JpaRepository<VideoProgress, UUID> {
    Optional<VideoProgress> findByUserIdAndVideoId(UUID userId, UUID videoId);

    Optional<VideoProgress> findByUserIdAndVideoIdAndAppId(UUID userId, UUID videoId, String appId);
}
