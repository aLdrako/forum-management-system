package com.telerikacademy.web.fms.services;

import com.telerikacademy.web.fms.exceptions.DuplicateEntityException;
import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.repositories.contracts.UserRepository;
import com.telerikacademy.web.fms.services.contracts.UserServices;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.telerikacademy.web.fms.helpers.DateTimeFormat.formatToLocalDateTime;

@Service
public class UserServicesImpl implements UserServices {
    private final static String USER_CHANGE_OR_DELETE_ERROR_MESSAGE = "Only admin or owner of the account can delete or change their account!";
    private final static String SUPER_USER_DELETION_ERROR_MESSAGE = "Super user cannot be deleted!";
    private static final String EMAIL_PREFIX = "email=";
    private static final String USERNAME_PREFIX = "username=";
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
        checkForDuplicateEmail(user);
        checkForDuplicateUsername(user);
        return userRepository.create(user);
    }

    @Override
    public User update(User... users) {
        User userToUpdate = userRepository.getById(users[0].getId());
        checkAuthorizedPermissions(users[0], users[1]);
        if (!userToUpdate.getEmail().equals(users[0].getEmail())) checkForDuplicateEmail(users[0]);
        return userRepository.update(users[0]);
    }

    @Override
    public void delete(Long id, User user) {
        if (id == 1) throw new UnauthorizedOperationException(SUPER_USER_DELETION_ERROR_MESSAGE);
        User userToDelete = userRepository.getById(id);
        checkAuthorizedPermissions(userToDelete, user);
        userRepository.delete(id);
    }

    private void checkForDuplicateEmail(User user) {
        boolean duplicateEmail = true;
        try {
            userRepository.search(EMAIL_PREFIX + user.getEmail());
        } catch (EntityNotFoundException e) {
            duplicateEmail = false;
        }
        if (duplicateEmail) throw new DuplicateEntityException("User", "email", user.getEmail());

    }

    private void checkForDuplicateUsername(User user) {
        boolean duplicateUsername = true;
        try {
            userRepository.search(USERNAME_PREFIX + user.getUsername());
        } catch (EntityNotFoundException e) {
            duplicateUsername = false;
        }
        if (duplicateUsername) throw new DuplicateEntityException("User", "username", user.getUsername());
    }

    private static void checkAuthorizedPermissions(User... users) {
        if (users[1].getPermission().isAdmin()) return;
        if (!users[0].getUsername().equals(users[1].getUsername())) {
            throw new UnauthorizedOperationException(USER_CHANGE_OR_DELETE_ERROR_MESSAGE);
        }
    }
}
