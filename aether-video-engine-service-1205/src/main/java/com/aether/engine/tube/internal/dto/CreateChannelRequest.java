package com.aether.engine.tube.internal.dto;

import com.aether.engine.tube.internal.entity.DefaultVisibility;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateChannelRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String name;

    @NotBlank
    private String handle;

    private String description;
    private String profilePicUrl;
    private String bannerUrl;
    private String country;
    private String defaultLanguage;
    private boolean isMadeForKids;
    private boolean allowCommentsByDefault;
    private DefaultVisibility visibility = DefaultVisibility.PUBLIC;
    private String ownerId;
}
