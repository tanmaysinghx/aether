# Aether Video Engine Service

The **Aether Video Engine** is the core microservice behind the Aether streaming platform. It handles high-performance video ingestion, adaptive bitrate transcoding (HLS), and secure streaming delivery.

## 🚀 Key Features

*   **Adaptive Transcoding**: Automatically converts uploaded videos into multi-quality HLS streams (1080p, 720p, 480p) using FFmpeg.
*   **Virtual Threads**: Built on **Java 25** and **Spring Boot 4.0.1** to leverage virtual threads for high-throughput I/O operations.
*   **Resumable Playback**: Tracks user progress to allow seamless cross-device resumption.
*   **Secure Streaming**: Dynamic HLS manifest generation with path-based security.

## 🛠️ Technology Stack

*   **Java 25** (Preview Features Enabled)
*   **Spring Boot 4.0.1** (Spring Modulith)
*   **FFmpeg** (Video Processing)
*   **MySQL** (Metadata Storage)
*   **Spring Data JPA**

## 📋 Prerequisites

Ensure you have the following installed:

1.  **Java 25 (EA/Preview)**: Required for virtual threads support.
2.  **FFmpeg**: Must be installed and available in your system's `PATH`.
    *   Verify with: `ffmpeg -version`
3.  **Maven**: For building the project.
4.  **MySQL Database**: (Or use the provided Aiven configuration).

## ⚙️ Configuration

The application is configured via `src/main/resources/application.properties`.

| Property | Description | Default |
| :--- | :--- | :--- |
| `server.port` | Service Port | `1205` |
| `spring.threads.virtual.enabled` | Enable Loot/Virtual Threads | `true` |
| `spring.datasource.url` | Database URL | (Set in properties) |
| `spring.servlet.multipart.max-file-size` | Max Upload Size | `1000MB` |

## 🏃‍♂️ How to Run

1.  **Clone the repository**:
    ```bash
    git clone <repo-url>
    cd aether-video-engine-service-1205
    ```

2.  **Build and Run**:
    ```bash
    ./mvnw spring-boot:run
    ```

3.  **Verify**:
    The service will start on port `1205`.
    Health check: `http://localhost:1205/actuator/health` (if enabled) or try the API.

## 🔌 API Reference

### Video Streaming
*   **Get HLS Master Manifest**:
    `GET /api/v1/stream/{videoId}/master.m3u8`
    *   Returns the master playlist for adaptive streaming.

### Playback Progress
*   **Save Progress (Heartbeat)**:
    `POST /api/v1/play/progress`
    ```json
    {
      "videoId": "uuid",
      "progress": 120.5
    }
    ```
*   **Get Progress**:
    `GET /api/v1/play/progress/{videoId}`

### Upload
*   **Upload Video**:
    `POST /api/v1/videos/upload` (Multipart File)

## 🤝 Contributing

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request
