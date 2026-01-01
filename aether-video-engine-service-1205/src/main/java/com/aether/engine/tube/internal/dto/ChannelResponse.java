package com.aether.engine.tube.internal.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ChannelResponse {
    private String email;
    private String name;
    private String handle;
    private Long subscribersCount;
    private LocalDateTime createdAt;
}
