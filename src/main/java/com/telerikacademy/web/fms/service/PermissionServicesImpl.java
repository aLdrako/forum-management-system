package com.company.web.forummanagementsystem.service;

import com.company.web.forummanagementsystem.exceptions.UnauthorizedOperationException;
import com.company.web.forummanagementsystem.models.Permission;
import com.company.web.forummanagementsystem.models.User;
import com.company.web.forummanagementsystem.repositories.contracts.PermissionRepository;
import com.company.web.forummanagementsystem.service.contracts.PermissionServices;
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
