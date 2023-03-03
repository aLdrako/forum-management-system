package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.models.Permission;

public interface PermissionRepository {
    Permission getById(Long id);
    Permission update(Permission permission);
}
