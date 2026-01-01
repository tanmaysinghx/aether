package com.aether.engine.tube.internal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubscriptionRequest {
    @NotBlank
    @Email
    private String subscriberEmail;

    @NotBlank
    @Email
    private String channelEmail;
}
