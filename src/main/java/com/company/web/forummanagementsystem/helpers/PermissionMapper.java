package com.company.web.forummanagementsystem.helpers;

import com.company.web.forummanagementsystem.models.Permission;
import com.company.web.forummanagementsystem.models.PermissionDTO;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {

    public Permission dtoToObject(Long id, PermissionDTO permissionDTO) {
        Permission permission = dtoToObject(permissionDTO);
        permission.setUser_id(id);
        return permission;
    }

    public Permission dtoToObject(PermissionDTO permissionDTO) {
        Permission permission = new Permission();
        permission.setAdmin(permissionDTO.isAdmin());
        permission.setBlocked(permissionDTO.isBlocked());
        return permission;
    }
}
