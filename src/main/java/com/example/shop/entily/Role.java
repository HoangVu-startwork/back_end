package com.example.shop.entily;


import com.example.shop.dto.response.PermissionResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


@Setter
@Getter
@Entity
public class Role {

    @Id
    private String name;

    private String description;

    @ManyToMany
    Set<Permission> permissions;


}
