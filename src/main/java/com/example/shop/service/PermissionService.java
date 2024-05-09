package com.example.shop.service;


import com.example.shop.dto.request.PermissionRequest;
import com.example.shop.dto.response.PermissionResponse;
import com.example.shop.entily.Permission;
import com.example.shop.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public PermissionResponse create(PermissionRequest request) {
        // Tạo một đối tượng Permission từ dữ liệu trong PermissionRequest
        Permission permission = new Permission();
        permission.setName(request.getName());
        permission.setDescription(request.getDescription());

        // Lưu đối tượng Permission vào cơ sở dữ liệu
        Permission savedPermission = permissionRepository.save(permission);

        // Tạo một đối tượng PermissionResponse từ đối tượng Permission đã lưu
        PermissionResponse response = new PermissionResponse();
        response.setName(savedPermission.getName());
        response.setDescription(savedPermission.getDescription());

        return response;
    }

    public List<PermissionResponse> getAll() {
        List<Permission> permissions = permissionRepository.findAll();
        // Sử dụng phương thức stream và map để chuyển đổi từ danh sách Permission thành danh sách PermissionResponse
        return permissions.stream()
                .map(this::mapToPermissionResponse)
                .collect(Collectors.toList());
    }

    private PermissionResponse mapToPermissionResponse(Permission permission) {
        PermissionResponse response = new PermissionResponse();
        response.setName(permission.getName());
        response.setDescription(permission.getDescription());
        return response;
    }

    public void delete(String permission) {
        permissionRepository.deleteById(permission);
    }


}
