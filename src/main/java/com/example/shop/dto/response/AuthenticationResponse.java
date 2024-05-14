package com.example.shop.dto.response;

import com.example.shop.dto.request.IntrospectRequest;

public class AuthenticationResponse {

    private String token;


    private boolean authenticated;


    public AuthenticationResponse(String token, boolean authenticated) {
        this.token = token;
        this.authenticated = authenticated;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    // Getter method
    public boolean isAuthenticated() {
        return authenticated;
    }
}
