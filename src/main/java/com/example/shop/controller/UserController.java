package com.example.shop.controller;

import com.example.shop.dto.request.ApiResponse;
import com.example.shop.dto.request.UserUpdateRequest;
import com.example.shop.dto.response.UserResponse;
import com.example.shop.entily.User;
import com.example.shop.dto.request.UserCreationRequest;
import com.example.shop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public ApiResponse<User> ceateUser(@RequestBody @Valid UserCreationRequest request) {
        ApiResponse<User> apiResponse = new ApiResponse<>();

        User createdUser = userService.createUser(request).getResult();
        apiResponse.setResult(createdUser);

        return apiResponse;
    }

    @GetMapping
    public ApiResponse<List<User>> getAllUser() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return new ApiResponse<>(401, "Unauthorized", null);
            }

            log.info("Username: {}", authentication.getName());
            authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

            List<User> users = userService.getAllUser();
            if (users.isEmpty()) {
                return new ApiResponse<>(200, "No users found", null);
            } else {
                return new ApiResponse<>(200, "Retrieved all users successfully", users);
            }
        } catch (Exception e) {
            return new ApiResponse<>(500, "Internal server error", null);
        }
    }



    //    @GetMapping("/users")
//    public ResponseEntity<List<User>> getAllUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//        }
//
//        // Kiểm tra quyền ở đây (nếu cần)
//        // authentication.getAuthorities().forEach(grantedAuthority -> { });
//
//        List<User> users = userService.getAllUser();
//        return ResponseEntity.ok(users);
//    }
    @GetMapping("/{userId}")
    User getUserById(@PathVariable String userId){
        return userService.getUserById(userId);
    }

    @GetMapping("/info")
    public UserResponse getInfo() {
        return userService.getMyInfo();
    }

//    @PutMapping("/{userId}")
//    User updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request){
//        return userService.updateUser(userId, request);
//    }

    @PutMapping("/{userId}")
    public UserResponse updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        return "User deleted successfully";
    }
}
