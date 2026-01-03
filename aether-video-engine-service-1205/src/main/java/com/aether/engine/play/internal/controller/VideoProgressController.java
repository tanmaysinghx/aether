package com.aether.engine.play.internal.controller;

import com.aether.engine.common.ApiResponse;
import com.aether.engine.media.MediaJobRepository;
import com.aether.engine.media.MediaService;
import com.aether.engine.play.internal.dto.VideoProgressRequest;
import com.aether.engine.play.internal.entity.VideoProgress;
import com.aether.engine.play.internal.repository.VideoProgressRepository;
import java.time.LocalDateTime;
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

@io.swagger.v3.oas.annotations.tags.Tag(name = "Video Progress", description = "Endpoints for tracking user playback progress")
@Slf4j
@RestController
@RequestMapping("/api/v1/play/progress")
@RequiredArgsConstructor
public class VideoProgressController {

    private final VideoProgressRepository videoProgressRepository;
    private final MediaJobRepository mediaJobRepository;
    private final MediaService mediaService;

    @PostMapping
    public ApiResponse<Void> updateProgress(
            @RequestHeader(value = "X-User-Id", defaultValue = "00000000-0000-0000-0000-000000000000") String userIdHeader,
            @RequestHeader(value = "X-App-Id", defaultValue = "DEFAULT") String appId,
            @jakarta.validation.Valid @RequestBody VideoProgressRequest request) {

        try {
            UUID userId = UUID.fromString(userIdHeader);
            UUID videoId = request.getVideoId();
            Double currentProgress = request.getProgressSeconds();

            VideoProgress videoProgress = videoProgressRepository.findByUserIdAndVideoIdAndAppId(userId, videoId, appId)
                    .orElse(VideoProgress.builder()
                            .userId(userId)
                            .videoId(videoId)
                            .appId(appId)
                            .progressSeconds(0.0)
                            .build());

            Double previousProgress = videoProgress.getProgressSeconds();
            if (previousProgress == null)
                previousProgress = 0.0;

            // Update Progress in DB
            videoProgress.setProgressSeconds(currentProgress);
            videoProgress.setLastUpdated(LocalDateTime.now());
            videoProgressRepository.save(videoProgress);

            // View Count Logic (45% Threshold)
            final Double finalPreviousProgress = previousProgress;

            mediaJobRepository.findById(videoId).ifPresent(job -> {
                Double duration = job.getDurationSeconds();
                if (duration != null && duration > 0) {
                    double thresholdSeconds = duration * 0.45;

                    boolean previouslyBelow = finalPreviousProgress < thresholdSeconds;
                    boolean currentlyAbove = currentProgress >= thresholdSeconds;

                    if (previouslyBelow && currentlyAbove) {
                        mediaService.incrementViewCount(videoId);
                        log.info("View count incremented for video {} by user {} (crossed 45% threshold)", videoId,
                                userId);
                    }
                }
            });

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
