package com.example.shop.dto.request;


import ch.qos.logback.classic.spi.LoggingEventVO;
import com.example.shop.dto.response.AuthenticationResponse;
import com.example.shop.dto.response.IntrospectResponse;
import com.example.shop.dto.response.PermissionResponse;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse <T> {
    private int code = 1000;
    private String message;
    private T result;

    public ApiResponse() {
    }

    public ApiResponse(int code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public ApiResponse(T result) {
        this.result = result;
    }

    // Builder method
    public static <T> ApiResponse<T> builder() {
        return new ApiResponse<>();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public ApiResponse<T> build() {
        return null;
    }
}
