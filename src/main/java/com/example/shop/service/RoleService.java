package com.example.shop.service;


import com.example.shop.dto.request.RoleRequest;
import com.example.shop.dto.response.PermissionResponse;
import com.example.shop.dto.response.RoleResponse;
import com.example.shop.entily.Permission;
import com.example.shop.entily.Role;
import com.example.shop.repository.PermissionRepository;
import com.example.shop.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionService permissionService;


    @Autowired
    private PermissionRepository permissionRepository;


    public RoleResponse create(RoleRequest request) {
        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());

        Set<Permission> permissions = new HashSet<>();
        if (request.getPermissions() != null) {
            for (String permissionId : request.getPermissions()) {
                Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permissionId));
                permissions.add(permission);
            }
        }
        role.setPermissions(permissions);

        role = roleRepository.save(role);

        RoleResponse roleResponse = new RoleResponse();
        roleResponse.setName(role.getName());
        roleResponse.setDescription(role.getDescription());
        roleResponse.setPermissions(permissionSetToResponseSet(role.getPermissions()));

        return roleResponse;
    }

    private Set<PermissionResponse> permissionSetToResponseSet(Set<Permission> permissions) {
        Set<PermissionResponse> responseSet = new HashSet<>();
        for (Permission permission : permissions) {
            PermissionResponse permissionResponse = new PermissionResponse();
            permissionResponse.setName(permission.getName());
            permissionResponse.setDescription(permission.getDescription());
            // Set other properties as needed
            responseSet.add(permissionResponse);
        }
        return responseSet;
    }


    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream()
                .map(role -> {
                    RoleResponse response = new RoleResponse();
                    response.setName(role.getName());
                    response.setDescription(role.getDescription());
                    response.setPermissions(permissionSetToResponseSet(role.getPermissions()));
                    return response;
                })
                .collect(Collectors.toList());
    }


    private Set<PermissionResponse> permissionSetToResponseSe(Set<Permission> permissions) {
        return permissions.stream()
                .map(permission -> {
                    PermissionResponse permissionResponse = new PermissionResponse();
                    permissionResponse.setName(permission.getName());
                    permissionResponse.setDescription(permission.getDescription());
                    return permissionResponse;
                })
                .collect(Collectors.toSet());
    }
    public void delete(String roleName) {
        roleRepository.deleteById(roleName);
    }
}
