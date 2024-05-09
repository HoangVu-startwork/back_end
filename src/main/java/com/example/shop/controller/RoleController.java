package com.example.shop.controller;

import com.example.shop.dto.request.RoleRequest;
import com.example.shop.dto.response.RoleResponse;
import com.example.shop.service.PermissionService;
import com.example.shop.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/roles")
@Slf4j
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    public RoleResponse create(@RequestBody RoleRequest request) {
        return roleService.create(request);
    }

    @GetMapping
    public List<RoleResponse> getAll() {
        return roleService.getAll();
    }

    @DeleteMapping("/{role}")
    public void delete(@PathVariable String role) {
        roleService.delete(role);
    }
}
