package com.company.web.forummanagementsystem.service;

import com.company.web.forummanagementsystem.models.User;

import java.util.List;
import java.util.Optional;

public interface UserServices {
    List<User> getAll();
    User getById(Long id);
    User getByUsername(String username);
    User create(User user);
    User update(User user);
    void delete(Long user);
    public List<User> getAllWithParams(Optional<Long> id, Optional<String> username);
}
