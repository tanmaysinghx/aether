package com.aether.engine.media.internal.event;

import java.util.UUID;

public record MediaProgressUpdateEvent(UUID jobId, Double progress, String status) {
}
