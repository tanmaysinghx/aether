package com.aether.engine.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        private int page;
        private int pageSize;
        private long total;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetail {
        private String field;
        private String reason;
        private String hint;
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setCode("APP-0000");
        response.setMessage(message);
        response.setTraceId(UUID.randomUUID().toString());
        response.setTxnId("TXN-" + System.currentTimeMillis());
        response.setTimestamp(LocalDateTime.now());
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> error(String code, String message, List<ErrorDetail> errors) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setCode(code);
        response.setMessage(message);
        response.setTraceId(UUID.randomUUID().toString());
        response.setTxnId("TXN-" + System.currentTimeMillis());
        response.setTimestamp(LocalDateTime.now());
        response.setErrors(errors);
        return response;
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return error(code, message, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return error("ERR-9999", message, null);
    }
}
