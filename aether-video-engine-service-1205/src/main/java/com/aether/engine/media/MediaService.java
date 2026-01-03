package com.aether.engine.media;

import com.aether.engine.media.internal.event.MediaProgressUpdateEvent;
import com.aether.engine.media.internal.service.FFmpegService;
import com.aether.engine.media.internal.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaJobRepository mediaJobRepository;
    private final FFmpegService ffmpegService;
    private final StorageService storageService;
    private final ApplicationEventPublisher eventPublisher;

    @jakarta.annotation.PostConstruct
    public void resumeInterruptedJobs() {
        log.info("Checking for interrupted media jobs...");
        java.util.List<MediaJob> processingJobs = mediaJobRepository.findByStatus(JobStatus.PROCESSING);
        for (MediaJob job : processingJobs) {
            log.warn("Found interrupted job {}. Marking as FAILED.", job.getId());
            job.setStatus(JobStatus.FAILED);
            mediaJobRepository.save(job);
            eventPublisher.publishEvent(new MediaProgressUpdateEvent(job.getId(), job.getProgress(), "FAILED"));
        }
    }

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
        job.setStatus(JobStatus.PROCESSING);
        job.setProgress(0.0);

        MediaJob savedJob = mediaJobRepository.save(job);
        log.info("MediaJob created with ID: {}", savedJob.getId());

        // Publish initial event
        eventPublisher.publishEvent(new MediaProgressUpdateEvent(savedJob.getId(), 0.0, "PROCESSING"));

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
                    eventPublisher.publishEvent(
                            new MediaProgressUpdateEvent(savedJob.getId(), savedJob.getProgress(), "PROCESSING"));
                }

                // Perform Transcoding with Progress Tracking
                Double duration = ffmpegService.transcode(savedJob.getId(), inputPath.toString(), (percentage) -> {
                    if (shouldUpdate(savedJob.getProgress(), percentage)) {
                        log.info("Job {} Progress: {}%", savedJob.getId(), String.format("%.2f", percentage));
                        savedJob.setProgress(percentage);
                        mediaJobRepository.save(savedJob);
                        eventPublisher
                                .publishEvent(new MediaProgressUpdateEvent(savedJob.getId(), percentage, "PROCESSING"));
                    }
                });

                // Update Status COMPLETED
                savedJob.setStatus(JobStatus.COMPLETED);
                savedJob.setProgress(100.0);
                savedJob.setDurationSeconds(duration);
                mediaJobRepository.save(savedJob);
                eventPublisher.publishEvent(new MediaProgressUpdateEvent(savedJob.getId(), 100.0, "COMPLETED"));

            } catch (Exception e) {
                log.error("Transcoding failed for job " + savedJob.getId(), e);
                savedJob.setStatus(JobStatus.FAILED);
                mediaJobRepository.save(savedJob);
                eventPublisher
                        .publishEvent(new MediaProgressUpdateEvent(savedJob.getId(), savedJob.getProgress(), "FAILED"));
            }
        });

        return savedJob;
    }

    public java.util.List<MediaJob> getFeed(String appSource) {
        return mediaJobRepository.findByAppSourceAndStatusOrderByCreatedAtDesc(appSource, JobStatus.COMPLETED);
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
