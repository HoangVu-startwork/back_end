package com.example.shop.controller;


import com.example.shop.dto.request.ApiResponse;
import com.example.shop.dto.request.AuthenticaticationRequest;
import com.example.shop.dto.request.IntrospectRequest;
import com.example.shop.dto.request.LogoutRequest;
import com.example.shop.dto.response.AuthenticationResponse;
import com.example.shop.dto.response.IntrospectResponse;
import com.example.shop.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
public class AuthenticonController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticonController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticaticationRequest request) {
        return authenticationService.login(request);
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) {
        try {
            IntrospectResponse response = authenticationService.introspect(request);
            return new ApiResponse<>(200, "Introspection successful", response);
        } catch (JOSEException e) {
            return new ApiResponse<>(500, "Internal server error", null);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

//    @PostMapping("/logout")
//    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody LogoutRequest request) {
//        try {
//            authenticationService.logout(request);
//            return ResponseEntity.ok(ApiResponse.<Void>builder().build());
//        } catch (ParseException | JOSEException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null));
//        }
//    }
@PostMapping("/logout")
public ResponseEntity<ApiResponse<Void>> logout(@RequestBody LogoutRequest request) {
    try {
        authenticationService.logout(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Logout successful", null));
    } catch (JOSEException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", null));
    } catch (ParseException e) {
        throw new RuntimeException(e);
    }
}

}
