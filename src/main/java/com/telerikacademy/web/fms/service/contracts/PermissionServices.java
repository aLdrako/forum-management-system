package com.telerikacademy.web.fms.service.contracts;

import com.telerikacademy.web.fms.models.Permission;
import com.telerikacademy.web.fms.models.User;

public interface PermissionServices {
    Permission getById(Long id);
    Permission update(Permission permission, User user);
}
