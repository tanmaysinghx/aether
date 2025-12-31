package com.aether.engine.media.internal.service;

import java.util.UUID;

public interface FFmpegService {
    void transcode(UUID jobId, String inputPath, java.util.function.Consumer<Double> progressCallback);

    String generateThumbnail(UUID jobId, String inputPath);
}
