package com.company.web.forummanagementsystem.service;

import com.company.web.forummanagementsystem.models.User;

import java.util.List;
import java.util.Optional;

public interface UserServices {
    List<User> getAll();
    User getById(Long id);
    User searchByUsername(String username);
    User create(User user);
    User update(User user);
    void delete(Long user);
}
