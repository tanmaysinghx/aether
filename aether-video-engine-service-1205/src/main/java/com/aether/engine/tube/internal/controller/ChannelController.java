package com.aether.engine.tube.internal.controller;

import com.aether.engine.common.ApiResponse;
import com.aether.engine.tube.internal.dto.ChannelResponse;
import com.aether.engine.tube.internal.dto.CreateChannelRequest;
import com.aether.engine.tube.internal.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Channels", description = "Endpoints for channel management")
@RestController
@RequestMapping("/api/v1/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @io.swagger.v3.oas.annotations.Operation(summary = "Create a new channel", description = "Allows users to create a unique channel with a name and handle.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Channel created successfully")
    @PostMapping
    public ResponseEntity<ApiResponse<ChannelResponse>> createChannel(
            @Valid @RequestBody CreateChannelRequest request) {
        ChannelResponse response = channelService.createChannel(request);
        return new ResponseEntity<>(
                ApiResponse.success(response, "Channel created successfully"),
                HttpStatus.CREATED);
    }
}
