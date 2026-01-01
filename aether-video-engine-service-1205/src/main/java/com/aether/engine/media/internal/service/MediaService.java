package com.aether.engine.media.internal.service;

import com.aether.engine.media.UploadRequest;
import com.aether.engine.media.internal.entity.MediaJob;
import com.aether.engine.media.internal.repository.MediaJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaJobRepository mediaJobRepository;
    private final FFmpegService ffmpegService;
    private final StorageService storageService;

    public MediaJob processUpload(MultipartFile file, UploadRequest request) {
        // 1. Save Job as PENDING/PROCESSING
        MediaJob job = new MediaJob();
        job.setTitle(request.title());
        job.setAppSource(request.appSource());
        job.setVideoType(request.videoType());
        job.setVisibility(request.visibility());
        job.setUploaderId(request.uploaderId());
        job.setDescription(request.description());
        job.setLanguage(request.language());
        job.setMetadata(request.metadata());
        job.setStatus("PROCESSING");

        MediaJob savedJob = mediaJobRepository.save(job);
        log.info("MediaJob created with ID: {}", savedJob.getId());

        // 2. Start Async Transcoding
        Thread.ofVirtual().start(() -> {
            try {
                // Use StorageService to persist the raw upload
                String rawDropdown = "raw/" + savedJob.getId();
                storageService.createDirectory(rawDropdown);

                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null || originalFilename.isBlank()) {
                    originalFilename = "source_video";
                }

                String rawFilename = java.nio.file.Paths.get(rawDropdown, originalFilename).toString();

                // StorageService stores it
                storageService.store(file.getInputStream(), rawFilename);
                java.nio.file.Path inputPath = storageService.getAbsolutePath(rawFilename);

                // Generate Thumbnail
                String thumbnailUrl = ffmpegService.generateThumbnail(savedJob.getId(), inputPath.toString());
                if (thumbnailUrl != null) {
                    savedJob.setThumbnailUrl(thumbnailUrl);
                    mediaJobRepository.save(savedJob);
                }

                // Perform Transcoding with Progress Tracking
                Double duration = ffmpegService.transcode(savedJob.getId(), inputPath.toString(), (percentage) -> {
                    // Update only if percentage changed significantly or periodically?
                    // For now, let's just log and update. Hibernate might be chatty.
                    // A better approach is to throttle here.

                    // Note: This runs on the virtual thread of the task.
                    // We need a new transaction or just save carefully.
                    // To avoid overwhelming DB, let's update every 5%

                    /*
                     * Optimistic throttling:
                     * IF (newPercentage - lastPercentage >= 5 || newPercentage >= 100)
                     * Update DB
                     * 
                     * But `savedJob` isn't thread-safe if shared, but here we are in a single
                     * lambda context.
                     * We re-fetch or just update the in-memory object and save.
                     */

                    if (shouldUpdate(savedJob.getProgress(), percentage)) {
                        log.info("Job {} Progress: {}%", savedJob.getId(), String.format("%.2f", percentage));
                        savedJob.setProgress(percentage);
                        mediaJobRepository.save(savedJob);
                    }
                });

                // Update Status COMPLETED
                savedJob.setStatus("COMPLETED");
                savedJob.setProgress(100.0);
                savedJob.setDurationSeconds(duration);
                mediaJobRepository.save(savedJob);

                // Cleanup raw file? Maybe keep it for re-transcoding?
                // keeping for now.

            } catch (Exception e) {
                log.error("Transcoding failed for job " + savedJob.getId(), e);
                savedJob.setStatus("FAILED");
                mediaJobRepository.save(savedJob);
            }
        });

        return savedJob;
    }

    public java.util.List<MediaJob> getFeed(String appSource) {
        return mediaJobRepository.findByAppSourceAndStatusOrderByCreatedAtDesc(appSource, "COMPLETED");
    }

    @org.springframework.transaction.annotation.Transactional
    public void incrementViewCount(java.util.UUID jobId) {
        mediaJobRepository.incrementViewCount(jobId);
    }

    private boolean shouldUpdate(Double current, Double newProgress) {
        if (current == null)
            return true;
        if (newProgress >= 100.0)
            return true;
        return (newProgress - current) >= 5.0;
    }
}
