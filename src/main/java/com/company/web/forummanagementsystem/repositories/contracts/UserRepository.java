package com.company.web.forummanagementsystem.repositories.contracts;

import com.company.web.forummanagementsystem.models.User;

import java.util.List;

public interface UserRepository {
    List<User> getAll();
    User getById(Long id);
    List<User> search(String parameter);
    User create(User user);
    User update(User user);
    void delete(Long id);
}
