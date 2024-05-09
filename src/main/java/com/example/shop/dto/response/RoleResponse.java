package com.example.shop.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class RoleResponse {
    private String name;

    private String description;

    Set<PermissionResponse> permissions;


}
