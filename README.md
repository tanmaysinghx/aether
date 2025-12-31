# Aether - Advanced Video Streaming Engine

![Java](https://img.shields.io/badge/Java-25%20(Preview)-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-green) ![FFmpeg](https://img.shields.io/badge/FFmpeg-Processed-blue) ![Status](https://img.shields.io/badge/Status-Active-success)

**Aether** is a next-generation video streaming platform designed for high-performance transcoding and adaptive bitrate streaming. It leverages the latest advancements in the Java ecosystem (Virtual Threads) and robust media processing tools (FFmpeg) to deliver a seamless viewing experience.

## 🌟 Concept & Architecture

Aether is built to handle the complexities of modern video delivery:

*   **Ingestion**: upload high-resolution video files.
*   **Processing**: Background jobs transcode videos into HLS-compatible segments (.ts) and manifests (.m3u8) for 1080p, 720p, and 480p simultaneously.
*   **Delivery**: Adaptive Bitrate Streaming (ABS) ensures users get the best quality for their network conditions.
*   **Experience**: Features like cross-device progress resumption mimic top-tier platforms like Netflix or YouTube.

### System Components

1.  **Backend Service (`aether-video-engine`)**:
    *   Spring Boot 4 Application.
    *   Handles Uploads, Transcoding Orchestration, and Streaming API.
    *   [Backend Documentation](./aether-video-engine-service-1205/README.md)

2.  **Frontend Client (`aetherTube-ui`)**:
    *   Simple HLS.js based player for verifying streams.
    *   Demonstrates adaptive switching and progress tracking.

## ✨ Key Features

*   🎥 **HLS Adaptive Streaming**: Dynamic quality switching based on bandwidth.
*   ⚡ **High Performance**: Powered by Java 25 Virtual Threads for efficient blocking I/O handling during transcoding.
*   🔄 **Smart Transcoding**: Analyzes input resolution to generate optimal quality ladders.
*   ⏯️ **Playback Resume**: Server-side tracking of watch history.
*   ☁️ **Cloud Ready**: Configured for cloud database integrations (Aiven).

## 🚀 Quick Start

### 1. Prerequisites
*   Java 25 (Preview)
*   FFmpeg (System Path)
*   Maven

### 2. Setup Backend
Navigate to the backend service and follow the setup instructions:
[Go to Backend README](./aether-video-engine-service-1205/README.md)

### 3. Verification Client
Open `aetherTube-ui-1250/index.html` in your browser to test the stream playback.

## 📜 License

This project is licensed under the MIT License.
