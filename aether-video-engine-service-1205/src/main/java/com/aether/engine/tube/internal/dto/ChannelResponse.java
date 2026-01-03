package com.aether.engine.tube.internal.dto;

import com.aether.engine.tube.internal.entity.ChannelStatus;
import com.aether.engine.tube.internal.entity.DefaultVisibility;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ChannelResponse {
    private String id;
    private String email;
    private String name;
    private String handle;
    private String description;
    private String profilePicUrl;
    private String bannerUrl;
    private String country;
    private String defaultLanguage;
    private Long subscribersCount;
    private Long videosCount;
    private Long viewsCount;
    private ChannelStatus status;
    private boolean isVerified;
    private boolean isMadeForKids;
    private boolean allowCommentsByDefault;
    private DefaultVisibility visibility;
    private String ownerId;
    private String uploadsPlaylistId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
