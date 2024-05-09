package com.example.shop.dto.request;

public class IntrospectRequest {

    private String token;
    public IntrospectRequest() {
        // Constructor mặc định không có tham số
    }
    public IntrospectRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String token;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public IntrospectRequest build() {
            return new IntrospectRequest(token);
        }
    }
}
