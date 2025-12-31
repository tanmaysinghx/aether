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
    public String generateThumbnail(UUID jobId, String inputPath) {
        try {
            String outputDir = java.nio.file.Paths.get(storageLocation, "hls", jobId.toString()).toString();
            new File(outputDir).mkdirs();

            String thumbnailPath = java.nio.file.Paths.get(outputDir, "thumbnail.jpg").toString();

            // FFMPEG command to extract frame at 1 second
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg", "-y",
                    "-i", inputPath,
                    "-ss", "00:00:01.000",
                    "-vframes", "1",
                    thumbnailPath);

            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0 && new File(thumbnailPath).exists()) {
                log.info("Thumbnail generated for Job {}: {}", jobId, thumbnailPath);
                return "hls/" + jobId + "/thumbnail.jpg"; // Return relative path/URL suffix
            } else {
                log.warn("Failed to generate thumbnail for Job {}", jobId);
                return null;
            }
        } catch (Exception e) {
            log.error("Error generating thumbnail", e);
            return null;
        }
    }

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

            // 3. Inspect Input Media (Audio & Resolution)
            boolean hasAudio = hasAudioStream(inputPath);
            int videoHeight = getVideoHeight(inputPath);
            int videoWidth = getVideoWidth(inputPath);
            log.info("Job {}: Audio: {}, Height: {}, Width: {}", jobId, hasAudio, videoHeight, videoWidth);

            boolean isVertical = videoWidth < videoHeight;

            // Determine Quality Checks based on the smaller dimension (resolution
            // convention)
            int minDim = Math.min(videoWidth, videoHeight);
            boolean add1080p = minDim >= 1080;
            boolean add4k = minDim >= 2160;

            // 4. Build FFmpeg Command Logic
            java.util.List<String> command = new java.util.ArrayList<>();
            command.add("ffmpeg");
            command.add("-i");
            command.add(inputPath);

            // Construct Filter Complex
            int splitCount = 2 + (add1080p ? 1 : 0) + (add4k ? 1 : 0);

            StringBuilder filterComplex = new StringBuilder();
            filterComplex.append("[0:v]split=").append(splitCount);

            filterComplex.append("[v1][v2]");
            if (add1080p)
                filterComplex.append("[v3]");
            if (add4k)
                filterComplex.append("[v4]");
            filterComplex.append(";");

            // Scaling Logic:
            // Horizontal: scale=w=-2:h=TARGET
            // Vertical: scale=w=TARGET:h=-2
            String scale480 = isVertical ? "scale=w=480:h=-2" : "scale=w=-2:h=480";
            String scale720 = isVertical ? "scale=w=720:h=-2" : "scale=w=-2:h=720";
            String scale1080 = isVertical ? "scale=w=1080:h=-2" : "scale=w=-2:h=1080";
            String scale4k = isVertical ? "scale=w=2160:h=-2" : "scale=w=-2:h=2160";

            filterComplex.append("[v1]").append(scale480).append("[v480];");
            filterComplex.append("[v2]").append(scale720).append("[v720]");
            if (add1080p) {
                filterComplex.append(";[v3]").append(scale1080).append("[v1080]");
            }
            if (add4k) {
                filterComplex.append(";[v4]").append(scale4k).append("[v4k]");
            }

            command.add("-filter_complex");
            command.add(filterComplex.toString());

            // --- 480p ---
            command.add("-map");
            command.add("[v480]");
            command.add("-c:v:0");
            command.add("libx264");
            command.add("-b:v:0");
            command.add("800k");
            if (hasAudio) {
                command.add("-map");
                command.add("0:a");
                command.add("-c:a:0");
                command.add("aac");
                command.add("-b:a:0");
                command.add("128k");
            }

            // --- 720p ---
            command.add("-map");
            command.add("[v720]");
            command.add("-c:v:1");
            command.add("libx264");
            command.add("-b:v:1");
            command.add("2500k");
            if (hasAudio) {
                command.add("-map");
                command.add("0:a");
                command.add("-c:a:1");
                command.add("aac");
                command.add("-b:a:1");
                command.add("128k");
            }

            // --- 1080p (Optional) ---
            if (add1080p) {
                command.add("-map");
                command.add("[v1080]");
                command.add("-c:v:2");
                command.add("libx264");
                command.add("-b:v:2");
                command.add("4500k");
                if (hasAudio) {
                    command.add("-map");
                    command.add("0:a");
                    command.add("-c:a:2");
                    command.add("aac");
                    command.add("-b:a:2");
                    command.add("192k");
                }
            }

            // --- 4K (Optional) ---
            if (add4k) {
                // Ensure map index is correct (0, 1, 2 used). Next is 3.
                // Note: The audio mapping index depends on if 1080p was added or not.
                // Wait, command -map 0:a is "input file 0, audio stream". It's the same source
                // for all.
                // The stream index in output file determines the variant map.
                // v:0,a:0 -> first output set
                // v:1,a:1 -> second output set
                // v:2,a:2 -> third output set (1080p)
                // v:3,a:3 -> fourth output set (4k)

                // However, we are adding them sequentially to the command line.
                // FFmpeg maps output streams in order of appearance.
                // 480p -> v:0, a:0
                // 720p -> v:1, a:1
                // 1080p -> v:2, a:2 (if present)
                // 4k -> v:3, a:3 (if present, but if 1080p missing, it would be v:2... careful)

                // Actually, if 1080p is missing but 4k is present (unlikely with this logic),
                // indices shift.
                // But my logic `add4k` requires `videoHeight >= 2160` which implies `add1080p`
                // is also true (>= 1080).
                // So order is preserved: 480, 720, 1080, 4K.

                command.add("-map");
                command.add("[v4k]");
                command.add("-c:v:3");
                command.add("libx264");
                command.add("-b:v:3");
                command.add("8000k"); // 8Mbps for 4K
                if (hasAudio) {
                    command.add("-map");
                    command.add("0:a");
                    command.add("-c:a:3");
                    command.add("aac");
                    command.add("-b:a:3");
                    command.add("192k");
                }
            }

            // HLS Settings
            command.add("-f");
            command.add("hls");
            command.add("-hls_time");
            command.add("10");
            command.add("-hls_playlist_type");
            command.add("vod");
            command.add("-hls_key_info_file");
            command.add(keyInfoPath);
            command.add("-master_pl_name");
            command.add("master.m3u8");

            // Stream Map
            command.add("-var_stream_map");
            StringBuilder streamMap = new StringBuilder();
            if (hasAudio) {
                streamMap.append("v:0,a:0 v:1,a:1");
                if (add1080p)
                    streamMap.append(" v:2,a:2");
                if (add4k)
                    streamMap.append(" v:3,a:3");
            } else {
                streamMap.append("v:0 v:1");
                if (add1080p)
                    streamMap.append(" v:2");
                if (add4k)
                    streamMap.append(" v:3");
            }
            command.add(streamMap.toString());

            command.add(outputDir + "/stream_%v.m3u8");

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);

            // DBG: Log the command
            String cmdString = String.join(" ", command);
            log.info("Executing FFmpeg command: {}", cmdString);

            Process process = processBuilder.start();

            // 5. Parse Output for Progress
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

    private int getVideoHeight(String inputPath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "ffprobe", "-v", "error", "-select_streams", "v:0", "-show_entries", "stream=height", "-of",
                    "csv=s=x:p=0", inputPath);
            Process process = pb.start();
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line != null && !line.trim().isEmpty()) {
                    return Integer.parseInt(line.trim());
                }
            }
        } catch (Exception e) {
            log.warn("Failed to probe video height for file: {}", inputPath, e);
        }
        return 0; // Default to 0 (assume low quality if probe fails)
    }

    private int getVideoWidth(String inputPath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "ffprobe", "-v", "error", "-select_streams", "v:0", "-show_entries", "stream=width", "-of",
                    "csv=s=x:p=0", inputPath);
            Process process = pb.start();
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line != null && !line.trim().isEmpty()) {
                    return Integer.parseInt(line.trim());
                }
            }
        } catch (Exception e) {
            log.warn("Failed to probe video width for file: {}", inputPath, e);
        }
        return 0;
    }

    private boolean hasAudioStream(String inputPath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "ffprobe", "-v", "error", "-select_streams", "a", "-show_entries", "stream=index", "-of", "csv=p=0",
                    inputPath);
            Process process = pb.start();
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                return line != null && !line.trim().isEmpty();
            }
        } catch (IOException e) {
            log.warn("Failed to probe audio stream for file: {}", inputPath, e);
            return false; // Assume no audio on error or handle differently
        }
    }
}
