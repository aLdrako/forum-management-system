package com.telerikacademy.web.fms.services.contracts;

import com.telerikacademy.web.fms.models.Permission;
import com.telerikacademy.web.fms.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserServices {
    List<User> getAll();

    Page<User> findAll(Pageable pageable);

    User getById(Long id);

    List<User> search(String parameter);

    User create(User user);

    User update(User... users);

    User updatePermissions(Permission permission, User user);

    void delete(Long id, User user);
}
