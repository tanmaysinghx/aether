package com.aether.engine.tube.internal.controller;

import com.aether.engine.common.ApiResponse;
import com.aether.engine.tube.internal.entity.Subscription;
import com.aether.engine.tube.internal.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Subscriptions", description = "Endpoints for managing user subscriptions and channel growth")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "Subscribe to a channel", description = "Allows a user to subscribe to a specific channel by ID.")
    @PostMapping("/channels/{channelId}/subscribe")
    public ResponseEntity<ApiResponse<Subscription>> subscribe(
            @PathVariable String channelId,
            @RequestBody Map<String, String> body) {
        String email = body.get("subscriberEmail");
        if (email == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("BAD_REQUEST", "subscriberEmail is required"));
        }
        Subscription sub = subscriptionService.subscribe(channelId, email);
        return new ResponseEntity<>(ApiResponse.success(sub, "Subscribed successfully"), HttpStatus.CREATED);
    }

    @Operation(summary = "Unsubscribe from a channel", description = "Allows a user to unsubscribe from a specific channel.")
    @DeleteMapping("/channels/{channelId}/subscribers/{subscriberEmail}")
    public ResponseEntity<ApiResponse<Void>> unsubscribe(
            @PathVariable String channelId,
            @PathVariable String subscriberEmail) {
        subscriptionService.unsubscribe(channelId, subscriberEmail);
        return new ResponseEntity<>(ApiResponse.success(null, "Unsubscribed successfully"), HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Get my subscriptions", description = "Returns a paginated list of channels the authenticated user is subscribed to.")
    @GetMapping("/me/subscriptions")
    public ApiResponse<Page<Subscription>> getMySubscriptions(
            @Parameter(description = "Authenticated user's email") String email,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(subscriptionService.getMySubscriptions(email, pageable), "Subscriptions retrieved");
    }

    @Operation(summary = "Get channel subscribers", description = "Returns a paginated list of subscribers for a specific channel.")
    @GetMapping("/channels/{channelId}/subscribers")
    public ApiResponse<Page<Subscription>> getChannelSubscribers(
            @PathVariable String channelId,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(subscriptionService.getChannelSubscribers(channelId, pageable),
                "Subscribers retrieved");
    }

    @Operation(summary = "Check subscription status", description = "Checks if a specific user is subscribed to a channel.")
    @GetMapping("/channels/{channelId}/subscribers/{subscriberEmail}")
    public ResponseEntity<ApiResponse<Void>> checkStatus(
            @PathVariable String channelId,
            @PathVariable String subscriberEmail) {
        boolean subbed = subscriptionService.isSubscribed(subscriberEmail, channelId);
        if (subbed) {
            return ResponseEntity.ok(ApiResponse.success(null, "Subscribed"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("NOT_FOUND", "Not subscribed"));
        }
    }

    @Operation(summary = "Get subscriber count", description = "Returns the total number of subscribers for a channel.")
    @GetMapping("/channels/{channelId}/subscriber-count")
    public ApiResponse<Map<String, Long>> getCount(@PathVariable String channelId) {
        long count = subscriptionService.getSubscriberCount(channelId);
        return ApiResponse.success(Map.of("subscribersCount", count), "Count retrieved");
    }
}
