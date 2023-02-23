package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.models.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAll();
    User getById(Long id);
    List<User> search(String parameter);
    User getByEmail(String email);
    User create(User user);
    User update(User user);
    void delete(Long id);
}
