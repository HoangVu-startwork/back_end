package com.example.shop.dto.response;

import com.example.shop.entily.Role;

import java.time.LocalDate;
import java.util.Set;
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private LocalDate dob;
    Set<RoleResponse> roles;


    public Set<RoleResponse> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleResponse> roles) {
        this.roles = roles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }
}
