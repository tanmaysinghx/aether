package com.aether.engine.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String code;
    private String message;
    private String traceId;
    private String txnId;
    private LocalDateTime timestamp;
    private T data;
    private Meta meta;
    private List<ErrorDetail> errors;

    @Data
    @Builder
    public static class Meta {
        private int page;
        private int pageSize;
        private long total;
    }

    @Data
    @Builder
    public static class ErrorDetail {
        private String field;
        private String reason;
        private String hint;
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .code("APP-0000")
                .message(message)
                .traceId(UUID.randomUUID().toString())
                .txnId("TXN-" + System.currentTimeMillis())
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String code, String message, List<ErrorDetail> errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .traceId(UUID.randomUUID().toString())
                .txnId("TXN-" + System.currentTimeMillis())
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();
    }
}
