package com.telerikacademy.web.fms.repositories.contracts;

import com.telerikacademy.web.fms.models.Permission;
import com.telerikacademy.web.fms.models.User;

import java.util.List;

public interface UserRepository {
    List<User> getAll();
    User getById(Long id);
    List<User> search(String parameter);
    User create(User user);
    User update(User user);
    Permission updatePermissions(Permission permission);
    void delete(Long id);
}
