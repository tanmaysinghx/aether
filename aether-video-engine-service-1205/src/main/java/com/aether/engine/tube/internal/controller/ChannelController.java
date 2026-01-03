package com.aether.engine.tube.internal.controller;

import com.aether.engine.common.ApiResponse;
import com.aether.engine.tube.internal.dto.ChannelResponse;
import com.aether.engine.tube.internal.dto.ChannelStatsResponse;
import com.aether.engine.tube.internal.dto.CreateChannelRequest;
import com.aether.engine.tube.internal.entity.ChannelStatus;
import com.aether.engine.tube.internal.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Channels", description = "Endpoints for channel management, discovery, and analytics")
@RestController
@RequestMapping("/api/v1/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @Operation(summary = "Create a new channel", description = "Allows users to create a unique channel with a name, handle, and profile details.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Channel created successfully")
    @PostMapping
    public ResponseEntity<ApiResponse<ChannelResponse>> createChannel(
            @Valid @RequestBody CreateChannelRequest request) {
        ChannelResponse response = channelService.createChannel(request);
        return new ResponseEntity<>(
                ApiResponse.success(response, "Channel created successfully"),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Get all channels (Paginated)", description = "Retrieves a paginated list of all channels with optional filtering by query, country, and status.")
    @GetMapping
    public ApiResponse<Page<ChannelResponse>> getAllChannels(
            @Parameter(description = "Search query (names or handles)") @RequestParam(required = false) String q,
            @Parameter(description = "Filter by country code") @RequestParam(required = false) String country,
            @Parameter(description = "Filter by channel status") @RequestParam(required = false) ChannelStatus status,
            @PageableDefault(size = 10, sort = "subscribersCount", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<ChannelResponse> channels = channelService.searchChannelsPaginated(q, country, status, pageable);
        return ApiResponse.success(channels, "Channels retrieved successfully");
    }

    @Operation(summary = "Get channel by ID", description = "Retrieves detailed profile information for a specific channel using its unique UUID.")
    @GetMapping("/{id}")
    public ApiResponse<ChannelResponse> getChannelById(@PathVariable String id) {
        ChannelResponse channel = channelService.getChannelById(id);
        return ApiResponse.success(channel, "Channel found");
    }

    @Operation(summary = "Get channel by handle", description = "Retrieves detailed profile information for a specific channel using its unique handle (e.g. @username).")
    @GetMapping("/handle/{handle}")
    public ApiResponse<ChannelResponse> getChannelByHandle(@PathVariable String handle) {
        ChannelResponse channel = channelService.getChannelByHandle(handle);
        return ApiResponse.success(channel, "Channel found");
    }

    @Operation(summary = "Get channel statistics", description = "Exposes metrics such as subscriber count, total videos, and total views for a specific channel.")
    @GetMapping("/{id}/stats")
    public ApiResponse<ChannelStatsResponse> getChannelStats(@PathVariable String id) {
        ChannelStatsResponse stats = channelService.getChannelStats(id);
        return ApiResponse.success(stats, "Channel stats retrieved");
    }

    @Operation(summary = "Search channels (Dedicated)", description = "Explicit endpoint for searching channels by name or handle with pagination.")
    @GetMapping("/search")
    public ApiResponse<Page<ChannelResponse>> searchChannels(
            @Parameter(description = "Search query") @RequestParam String q,
            @Parameter(description = "Filter by country code") @RequestParam(required = false) String country,
            @Parameter(description = "Filter by channel status") @RequestParam(required = false) ChannelStatus status,
            @PageableDefault(size = 10, sort = "subscribersCount", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<ChannelResponse> channels = channelService.searchChannelsPaginated(q, country, status, pageable);
        return ApiResponse.success(channels, "Search results retrieved");
    }
}
