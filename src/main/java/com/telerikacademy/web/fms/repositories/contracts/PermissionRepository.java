package com.telerikacademy.web.fms.repositories.contracts;

import com.telerikacademy.web.fms.models.Permission;

public interface PermissionRepository {
    Permission getById(Long id);
    Permission update(Permission permission);
}
