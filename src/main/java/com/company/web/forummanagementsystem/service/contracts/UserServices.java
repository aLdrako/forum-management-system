package com.company.web.forummanagementsystem.service.contracts;

import com.company.web.forummanagementsystem.models.User;

import java.util.List;

public interface UserServices {
    List<User> getAll();
    User getById(Long id);
    List<User> search(String parameter);
    User create(User user);
    User update(User... users);
    void delete(Long id, User user);
}
