package com.ga.airticketmanagement.dto.response;

import lombok.NonNull;

import java.util.Map;
import java.util.Objects;

public record ApiErrorResponse(@NonNull String code, @NonNull String message, @NonNull Map<String, Object> data) {
    public ApiErrorResponse(String code, String message) {
        this(code, message, Map.of());
    }

    public ApiErrorResponse(String code, String message, Map<String, Object> data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
