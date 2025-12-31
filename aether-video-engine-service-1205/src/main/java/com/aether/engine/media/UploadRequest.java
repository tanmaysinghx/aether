package com.aether.engine.media;

import java.util.Map;

public record UploadRequest(String title, String appSource, String videoType, String visibility,
        Map<String, Object> metadata) {
}
