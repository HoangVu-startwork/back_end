package com.example.shop.service;


import ch.qos.logback.core.LayoutBase;
import com.example.shop.dto.request.ApiResponse;
import com.example.shop.dto.request.UserCreationRequest;
import com.example.shop.dto.request.UserUpdateRequest;
import com.example.shop.dto.response.PermissionResponse;
import com.example.shop.dto.response.RoleResponse;
import com.example.shop.dto.response.UserResponse;
import com.example.shop.entily.Role;
import com.example.shop.entily.User;
import com.example.shop.exception.AppException;
import com.example.shop.exception.ErrorCode;
import com.example.shop.repository.RoleRepository;
import com.example.shop.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;


    @Autowired
    private RoleRepository roleRepository;

    public ApiResponse<User> createUser(UserCreationRequest request) {
        ApiResponse<User> response = new ApiResponse<>();

        try {
            validatePassword(request.getPassword());
            validateEmail(request.getEmail());
        } catch (AppException e) {
            response.setCode(e.getErrorCode().getCode());
            response.setMessage(e.getMessage());
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        String encodedPassword = encodePassword(request.getPassword());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encodedPassword);
        user.setEmail(request.getEmail());
        user.setDob(request.getDob());


//        user.setRole(Role.USER.name());
        
        User savedUser = userRepository.save(user);

        response.setResult(savedUser);
        return response;
    }

    private void validatePassword(String password) {
        // Kiểm tra độ dài mật khẩu
        if (password.length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters long");
        }

        // Kiểm tra xem mật khẩu có chứa ít nhất một chữ cái
        if (!password.matches(".*[a-zA-Z].*")) {
            throw new RuntimeException("Password must contain at least one letter");
        }

        // Kiểm tra xem mật khẩu có chứa ít nhất một số
        if (!password.matches(".*\\d.*")) {
            throw new RuntimeException("Password must contain at least one digit");
        }

        // Kiểm tra xem mật khẩu có chứa ít nhất một chữ cái viết hoa
        if (!password.matches(".*[A-Z].*")) {
            throw new RuntimeException("Password must contain at least one uppercase letter");
        }

        // Kiểm tra xem mật khẩu có chứa ít nhất một ký tự đặc biệt
        if (!password.matches(".*[!@#$%^&*()].*")) {
            throw new RuntimeException("Password must contain at least one special character");
        }
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
    }

//    public User updateUser(String userId, UserUpdateRequest request){
//        User user = getUserById(userId);
//
//        if (user == null) {
//            throw new RuntimeException("User not found");
//        }
//
//        user.setUsername(request.getUsername());
//        user.setPassword(request.getPassword());
//        user.setDob(request.getDob());
//
//        return userRepository.save(user);
//    }

    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String encodedPassword = encodePassword(request.getPassword());
        user.setUsername(request.getUsername());
        user.setPassword(encodedPassword);
        user.setDob(request.getDob());

        Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.getRoles()));
        user.setRoles(roles);

        user = userRepository.save(user);

        // Convert Set<Role> to Set<RoleResponse>
        Set<RoleResponse> roleResponses = user.getRoles().stream()
                .map(role -> {
                    RoleResponse roleResponse = new RoleResponse();
                    roleResponse.setName(role.getName());
                    roleResponse.setDescription(role.getDescription());

                    // Make sure permissions are properly set in Role entity
                    Set<PermissionResponse> permissionResponses = role.getPermissions().stream()
                            .map(permission -> {
                                PermissionResponse permissionResponse = new PermissionResponse();
                                // Map permission attributes
                                permissionResponse.setName(permission.getName());
                                permissionResponse.setDescription(permission.getDescription());
                                // Set other permission attributes if needed
                                return permissionResponse;
                            })
                            .collect(Collectors.toSet());

                    roleResponse.setPermissions(permissionResponses);
                    return roleResponse;
                })
                .collect(Collectors.toSet());

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setDob(user.getDob());
        userResponse.setRoles(roleResponses);

        return userResponse;
    }


//    public UserResponse getMyInfo() {
//        var context = SecurityContextHolder.getContext();
//        String name = context.getAuthentication().getName();
//        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
//        UserResponse userResponse = new UserResponse();
//        userResponse.setId(user.getId());
//        userResponse.setUsername(user.getUsername());
//        userResponse.setEmail(user.getEmail());
//        userResponse.setDob(user.getDob());
//        return userResponse;
//    }
public UserResponse getMyInfo() {
    var context = SecurityContextHolder.getContext();
    String name = context.getAuthentication().getName();
    User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

    // Fetch roles associated with the user
    Set<Role> roles = user.getRoles();

    // Convert roles to role responses

    Set<RoleResponse> roleResponses = user.getRoles().stream()
            .map(role -> {
                RoleResponse roleResponse = new RoleResponse();
                roleResponse.setName(role.getName());
                roleResponse.setDescription(role.getDescription());

                // Make sure permissions are properly set in Role entity
                Set<PermissionResponse> permissionResponses = role.getPermissions().stream()
                        .map(permission -> {
                            PermissionResponse permissionResponse = new PermissionResponse();
                            // Map permission attributes
                            permissionResponse.setName(permission.getName());
                            permissionResponse.setDescription(permission.getDescription());
                            // Set other permission attributes if needed
                            return permissionResponse;
                        })
                        .collect(Collectors.toSet());

                roleResponse.setPermissions(permissionResponses);
                return roleResponse;
            })
            .collect(Collectors.toSet());
    // Create and populate the UserResponse object
    UserResponse userResponse = new UserResponse();
    userResponse.setId(user.getId());
    userResponse.setUsername(user.getUsername());
    userResponse.setEmail(user.getEmail());
    userResponse.setDob(user.getDob());
    userResponse.setRoles(roleResponses);

    return userResponse;
}


    //@PreAuthorize("hasRole('ADMIN')") // dùng head rone thì spring sẽ mặc định sẽ map với những cái authorty nào mà có cái (ROLE_)
    // trước
    @PreAuthorize("hasAuthority('APPROVE_POST')")
    public List<User> getAllUser(){
        log.info("In method Users");
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            log.error("Error occurred while retrieving users: {}", e.getMessage());
            throw new AppException(ErrorCode.AUTH_EX);
        }
    }


    @PostAuthorize("returnObject.username == authentication.name")
    public User getUserById(String id){
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }


    private String encodePassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    @Bean
    @Lazy
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

}
