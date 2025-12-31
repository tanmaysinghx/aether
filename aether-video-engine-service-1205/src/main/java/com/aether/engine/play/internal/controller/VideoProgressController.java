package com.aether.engine.play.internal.controller;

import com.aether.engine.common.api.ApiResponse;
import com.aether.engine.play.internal.entity.VideoProgress;
import com.aether.engine.play.internal.repository.VideoProgressRepository;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/play/progress")
@RequiredArgsConstructor
public class VideoProgressController {

    private final VideoProgressRepository videoProgressRepository;

    @PostMapping
    public ApiResponse<Void> updateProgress(
            @RequestHeader(value = "X-User-Id", defaultValue = "00000000-0000-0000-0000-000000000000") String userIdHeader,
            @RequestHeader(value = "X-App-Id", defaultValue = "DEFAULT") String appId,
            @RequestBody Map<String, Object> payload) {

        try {
            UUID userId = UUID.fromString(userIdHeader);
            String videoIdStr = (String) payload.get("videoId");
            Object progressObj = payload.get("progressSeconds");

            if (videoIdStr == null || progressObj == null) {
                return ApiResponse.error("ERR-400", "Invalid payload: videoId and progressSeconds are required", null);
            }

            Double progress = ((Number) progressObj).doubleValue();

            UUID videoId = UUID.fromString(videoIdStr);

            VideoProgress videoProgress = videoProgressRepository.findByUserIdAndVideoIdAndAppId(userId, videoId, appId)
                    .orElse(VideoProgress.builder()
                            .userId(userId)
                            .videoId(videoId)
                            .appId(appId)
                            .build());

            videoProgress.setProgressSeconds(progress);
            videoProgress.setLastUpdated(LocalDateTime.now());

            videoProgressRepository.save(videoProgress);
            return ApiResponse.success(null, "Progress updated");

        } catch (Exception e) {
            log.error("Failed to update progress", e);
            return ApiResponse.error("ERR-500", "Failed to update progress", null);
        }
    }

    @GetMapping("/{videoId}")
    public ApiResponse<Double> getProgress(
            @RequestHeader(value = "X-User-Id", defaultValue = "00000000-0000-0000-0000-000000000000") String userIdHeader,
            @RequestHeader(value = "X-App-Id", defaultValue = "DEFAULT") String appId,
            @PathVariable UUID videoId) {

        UUID userId = UUID.fromString(userIdHeader);

        return videoProgressRepository.findByUserIdAndVideoIdAndAppId(userId, videoId, appId)
                .map(vp -> ApiResponse.success(vp.getProgressSeconds(), "Progress retrieved"))
                .orElse(ApiResponse.success(0.0, "No progress found"));
    }
}
