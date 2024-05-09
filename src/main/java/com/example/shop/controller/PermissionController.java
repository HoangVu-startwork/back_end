package com.example.shop.controller;


import com.example.shop.dto.request.ApiResponse;
import com.example.shop.dto.request.PermissionRequest;
import com.example.shop.dto.response.PermissionResponse;
import com.example.shop.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@Slf4j
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @PostMapping
    public ResponseEntity<ApiResponse<PermissionResponse>> create(@RequestBody PermissionRequest request) {
        try {
            PermissionResponse createdPermission = permissionService.create(request);
            ApiResponse<PermissionResponse> response = new ApiResponse<>(createdPermission);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while creating permission", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", null));
        }
    }

    @GetMapping
    public ApiResponse<List<PermissionResponse>> getAllPermissions() {
        List<PermissionResponse> permissions = permissionService.getAll();
        return new ApiResponse<>(permissions);
    }

    @DeleteMapping("/{permission}")
    public ApiResponse<Void> deletePermission(@PathVariable String permission) {
        try {
            permissionService.delete(permission);
            return new ApiResponse<>(); // Assuming ApiResponse has a default constructor
        } catch (Exception e) {
            // Handle any exceptions that might occur during deletion
            return new ApiResponse<>(500, "Internal Server Error", null); // Or provide a more specific error message
        }
    }
}
