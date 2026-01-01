package com.aether.engine.tube.internal.controller;

import com.aether.engine.common.api.ApiResponse;
import com.aether.engine.tube.internal.dto.SubscriptionRequest;
import com.aether.engine.tube.internal.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<Void>> subscribe(@Valid @RequestBody SubscriptionRequest request) {
        subscriptionService.subscribe(request.getSubscriberEmail(), request.getChannelEmail());
        return new ResponseEntity<>(
                ApiResponse.success(null, "Subscribed successfully"),
                HttpStatus.OK);
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<ApiResponse<Void>> unsubscribe(@Valid @RequestBody SubscriptionRequest request) {
        subscriptionService.unsubscribe(request.getSubscriberEmail(), request.getChannelEmail());
        return new ResponseEntity<>(
                ApiResponse.success(null, "Unsubscribed successfully"),
                HttpStatus.OK);
    }
}
