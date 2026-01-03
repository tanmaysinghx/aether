package com.aether.engine.tube.internal.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChannelStatsResponse {
    private String channelId;
    private Long subscribersCount;
    private Long videosCount;
    private Long viewsCount;
}
