package com.company.web.forummanagementsystem.service;

import com.company.web.forummanagementsystem.exceptions.AuthorizationException;
import com.company.web.forummanagementsystem.exceptions.DuplicateEntityException;
import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.exceptions.UnauthorizedOperationException;
import com.company.web.forummanagementsystem.models.User;
import com.company.web.forummanagementsystem.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServicesImpl implements UserServices {
    private final static String DELETE_USER_ERROR_MESSAGE = "Only the owner of the account can delete his account!";
    private final static String UPDATE_USER_ERROR_MESSAGE = "Only the owner of the account can change his account details!";
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
    public List<User> search(String parameter) {
        return userRepository.search(parameter);
    }

    @Override
    public User create(User user) {
        checkForDuplicate(user);
        return userRepository.create(user);
    }

    @Override
    public User update(User... users) {
        User userToUpdate = userRepository.getById(users[0].getId());
        checkPermissions(UPDATE_USER_ERROR_MESSAGE, userToUpdate, users[1]);
        checkForDuplicate(users[0]);
        return userRepository.update(users[0]);
    }

    @Override
    public void delete(Long id, User user) {
        User userToDelete = userRepository.getById(id);
        checkPermissions(DELETE_USER_ERROR_MESSAGE, userToDelete, user);
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

    private static void checkPermissions(String message, User... users) {
        if (!users[0].getUsername().equals(users[1].getUsername())) {
            throw new UnauthorizedOperationException(message);
        }
    }
}
