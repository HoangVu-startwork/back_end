package com.example.shop.exception;

import com.example.shop.dto.request.ApiResponse;
import com.example.shop.dto.response.AuthenticationResponse;

public enum ErrorCode {
    USER_EXISTED(1001, "User existed"),
    USER_NOT_FOUND(1002, "Đăng nhập không thành công"),
    AUTH_EX(401, "Authentication failed"),

    USER_NOT_EXISTED(1010, "User not existed Adim"),

    UNAUTHORIZED(401, "Unauthenticated"),
    TOKEN_EX(402, "Unauthenticated"),
    UNAUTHENTICATED(403, "Unauthenticated12"),
    ;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiResponse<AuthenticationResponse> toApiResponse() {
        return new ApiResponse<>(getCode(), getMessage(), null);
    }

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
