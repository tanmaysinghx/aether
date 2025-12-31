package com.aether.engine.media.internal.service.impl;

import com.aether.engine.media.internal.service.FFmpegService;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FFmpegServiceImpl implements FFmpegService {

    @Value("${server.port:1205}")
    private String serverPort;

    @Value("${app.storage.location:video_storage}")
    private String storageLocation;

    @Override
    public void transcode(UUID jobId, String inputPath, java.util.function.Consumer<Double> progressCallback) {
        File inputFile = new File(inputPath);
        if (!inputFile.exists()) {
            throw new RuntimeException("Input file not found: " + inputPath);
        }
        if (!inputFile.isFile()) {
            throw new RuntimeException("Input path is not a file: " + inputPath);
        }

        log.info("Starting Secure HLS transcoding for Job: {} Input: {} (Size: {} bytes)", jobId, inputPath,
                inputFile.length());

        // Ensure absolute path usage
        String outputDir = java.nio.file.Paths.get(storageLocation, "hls", jobId.toString()).toString();
        new File(outputDir).mkdirs();

        try {
            // 1. Generate Encryption Key (AES-128)
            byte[] key = new byte[16];
            new SecureRandom().nextBytes(key);
            String keyPath = outputDir + "/enc.key";
            java.nio.file.Files.write(java.nio.file.Path.of(keyPath), key);

            // 2. Create Key Info File
            String keyUri = "http://localhost:" + serverPort + "/api/v1/keys/" + jobId;
            String keyInfoPath = outputDir + "/enc.keyinfo";
            try (FileWriter writer = new FileWriter(keyInfoPath)) {
                writer.write(keyUri + "\n");
                writer.write(new File(keyPath).getAbsolutePath() + "\n");
            }

            // 3. Build FFmpeg Command
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg",
                    "-i", inputPath,
                    "-filter_complex", "[0:v]split=2[v1][v2];[v1]scale=w=-2:h=480[v480];[v2]scale=w=-2:h=720[v720]",
                    "-map", "[v480]", "-c:v:0", "libx264", "-b:v:0", "800k",
                    "-map", "0:a", "-c:a:0", "aac", "-b:a:0", "128k",
                    "-map", "[v720]", "-c:v:1", "libx264", "-b:v:1", "2500k",
                    "-map", "0:a", "-c:a:1", "aac", "-b:a:1", "128k",
                    "-f", "hls",
                    "-hls_time", "10",
                    "-hls_playlist_type", "vod",
                    "-hls_key_info_file", keyInfoPath,
                    "-master_pl_name", "master.m3u8",
                    "-var_stream_map", "v:0,a:0 v:1,a:1",
                    outputDir + "/stream_%v.m3u8");

            processBuilder.redirectErrorStream(true); // Merge stderr into stdout to capture progress

            Process process = processBuilder.start();

            // 4. Parse Output for Progress
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                double totalDurationSeconds = 0.0;
                java.util.regex.Pattern durationPattern = java.util.regex.Pattern
                        .compile("Duration: (\\d{2}):(\\d{2}):(\\d{2}\\.\\d{2})");
                java.util.regex.Pattern timePattern = java.util.regex.Pattern
                        .compile("time=(\\d{2}):(\\d{2}):(\\d{2}\\.\\d{2})");

                while ((line = reader.readLine()) != null) {
                    // Extract Duration
                    if (totalDurationSeconds == 0.0) {
                        java.util.regex.Matcher durationMatcher = durationPattern.matcher(line);
                        if (durationMatcher.find()) {
                            int hours = Integer.parseInt(durationMatcher.group(1));
                            int minutes = Integer.parseInt(durationMatcher.group(2));
                            double seconds = Double.parseDouble(durationMatcher.group(3));
                            totalDurationSeconds = hours * 3600 + minutes * 60 + seconds;
                            log.info("Total Duration for Job {}: {} seconds", jobId, totalDurationSeconds);
                        }
                    }

                    // Extract Progress
                    if (totalDurationSeconds > 0) {
                        java.util.regex.Matcher timeMatcher = timePattern.matcher(line);
                        if (timeMatcher.find()) {
                            int hours = Integer.parseInt(timeMatcher.group(1));
                            int minutes = Integer.parseInt(timeMatcher.group(2));
                            double seconds = Double.parseDouble(timeMatcher.group(3));
                            double currentTimeSeconds = hours * 3600 + minutes * 60 + seconds;

                            double percentage = (currentTimeSeconds / totalDurationSeconds) * 100.0;
                            if (percentage > 100.0)
                                percentage = 100.0;

                            progressCallback.accept(percentage);
                        }
                    }
                }
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("Transcoding finished successfully for Job: {}", jobId);
                progressCallback.accept(100.0);
            } else {
                log.error("Transcoding failed for Job: {} with exit code: {}", jobId, exitCode);
                throw new RuntimeException("FFmpeg exited with code " + exitCode);
            }

        } catch (Exception e) {
            log.error("Error during transcoding", e);
            throw new RuntimeException("Transcoding failed", e);
        }
    }
}
