package com.company.web.forummanagementsystem.service;

import com.company.web.forummanagementsystem.exceptions.DuplicateEntityException;
import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.models.User;
import com.company.web.forummanagementsystem.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServicesImpl implements UserServices {
    private final UserRepository userRepository;

    public UserServicesImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public User getById(Long id) {
        return userRepository.getById(id);
    }

    @Override
    public User searchByUsername(String username) {
        return userRepository.searchByUsername(username);
    }

    @Override
    public User create(User user) {
        checkForDuplicate(user);
        return userRepository.create(user);
    }

    @Override
    public User update(User user) {
        checkForDuplicate(user);
        return userRepository.update(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }

    private void checkForDuplicate(User user) {
        boolean duplicateExists = true;

        try {
            userRepository.getByEmail(user.getEmail());
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new DuplicateEntityException("User", "email", user.getEmail());
        }
    }
}
