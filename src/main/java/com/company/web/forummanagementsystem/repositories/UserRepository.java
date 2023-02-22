package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.models.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAll();
    User getById(Long id);
    User getByUsername(String username);
    User getByEmail(String email);
    User create(User user);
    User update(User user);
    void delete(Long id);
    public List<User> getAllWithParams(Optional<Long> id, Optional<String> username);

}
