package com.aether.engine.media.internal.controller;

import com.aether.engine.media.internal.event.MediaProgressUpdateEvent;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Media Progress", description = "Endpoints for real-time media progress updates via SSE")
@Slf4j
@RestController
@RequestMapping("/api/v1/media")
public class MediaProgressController {

    private final Map<UUID, Map<String, SseEmitter>> emitters = new ConcurrentHashMap<>();

    @GetMapping(value = "/{id}/progress/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamProgress(@PathVariable UUID id) {
        SseEmitter emitter = new SseEmitter(300_000L); // 5 minutes timeout
        String emitterId = UUID.randomUUID().toString();

        emitters.computeIfAbsent(id, k -> new ConcurrentHashMap<>()).put(emitterId, emitter);

        emitter.onCompletion(() -> removeEmitter(id, emitterId));
        emitter.onTimeout(() -> removeEmitter(id, emitterId));
        emitter.onError((e) -> removeEmitter(id, emitterId));

        try {
            emitter.send(SseEmitter.event().name("INIT").data("Connected for job " + id));
        } catch (IOException e) {
            removeEmitter(id, emitterId);
        }

        return emitter;
    }

    @EventListener
    public void handleProgressUpdate(MediaProgressUpdateEvent event) {
        Map<String, SseEmitter> jobEmitters = emitters.get(event.jobId());
        if (jobEmitters != null) {
            jobEmitters.forEach((emitterId, emitter) -> {
                try {
                    emitter.send(SseEmitter.event()
                            .name("PROGRESS")
                            .data(event));
                } catch (IOException e) {
                    removeEmitter(event.jobId(), emitterId);
                }
            });
        }
    }

    private void removeEmitter(UUID jobId, String emitterId) {
        Map<String, SseEmitter> jobEmitters = emitters.get(jobId);
        if (jobEmitters != null) {
            jobEmitters.remove(emitterId);
            if (jobEmitters.isEmpty()) {
                emitters.remove(jobId);
            }
        }
    }
}
