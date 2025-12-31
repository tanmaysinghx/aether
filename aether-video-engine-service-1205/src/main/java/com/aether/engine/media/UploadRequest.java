package com.aether.engine.media;

import java.util.Map;

public record UploadRequest(String title, String appSource, Map<String, Object> metadata) {
}
