package com.example.shop.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class RoleRequest {

    private String name;
    private String description;

    Set<String> permissions;


}
