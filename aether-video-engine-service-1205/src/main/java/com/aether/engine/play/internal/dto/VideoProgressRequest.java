package com.aether.engine.play.internal.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoProgressRequest {
    @NotNull
    private UUID videoId;
    @NotNull
    private Double progressSeconds;
}
