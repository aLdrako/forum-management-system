package com.telerikacademy.web.fms.service;

import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.models.Permission;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.repositories.contracts.PermissionRepository;
import com.telerikacademy.web.fms.service.contracts.PermissionServices;
import org.springframework.stereotype.Service;

@Service
public class PermissionServicesImpl implements PermissionServices {
    private final static String UPDATE_ADMIN_PERMISSION_ERROR_MESSAGE = "Only admin can modify these settings: <<Block>>, <<Set Admin>>!";
    private final static String UPDATE_SUPER_USER_PERMISSION_ERROR_MESSAGE = "Super user permissions cannot be changed!";
    private final PermissionRepository permissionRepository;

    public PermissionServicesImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Permission getById(Long id) {
        return permissionRepository.getById(id);
    }

    @Override
    public Permission update(Permission permission, User authenticatedUser) {
        if (permission.getUser_id() == 1) throw new UnauthorizedOperationException(UPDATE_SUPER_USER_PERMISSION_ERROR_MESSAGE);
        permissionRepository.getById(permission.getUser_id());
        checkAdminPermissions(authenticatedUser);
        return permissionRepository.update(permission);
    }

    private static void checkAdminPermissions(User user) {
        if (!user.getPermission().isAdmin()) {
            throw new UnauthorizedOperationException(UPDATE_ADMIN_PERMISSION_ERROR_MESSAGE);
        }
    }
}
