package com.company.web.forummanagementsystem.service;

import com.company.web.forummanagementsystem.models.Permission;
import com.company.web.forummanagementsystem.models.User;

public interface PermissionServices {
    Permission getById(Long id);
    Permission update(Permission permission, User user);

}
