package com.aether.engine.media;

import java.util.Map;

public record UploadRequest(String title, String appSource, String videoType, String visibility, String description,
                String language, java.util.UUID uploaderId, Map<String, Object> metadata) {
}
