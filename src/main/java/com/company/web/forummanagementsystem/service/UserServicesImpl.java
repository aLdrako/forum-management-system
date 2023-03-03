package com.company.web.forummanagementsystem.service;

import com.company.web.forummanagementsystem.exceptions.DuplicateEntityException;
import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.exceptions.UnauthorizedOperationException;
import com.company.web.forummanagementsystem.models.User;
import com.company.web.forummanagementsystem.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.company.web.forummanagementsystem.helpers.DateTimeFormat.formatToLocalDateTime;

@Service
public class UserServicesImpl implements UserServices {
    private final static String USER_CHANGE_OR_DELETE_ERROR_MESSAGE = "Only admins and owners of the account can delete or change their account!";
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

    /**
     * @param users users[0] - user from Requested Body (JSON)
     *              users[1] - authenticated user from Header (Http Header)
     * @return updated user from DB
     */
    @Override
    public User update(User... users) {
        User userToUpdate = userRepository.getById(users[0].getId());
        checkAuthorizedPermissions(userToUpdate, users[1]);
        if (!userToUpdate.getEmail().equals(users[0].getEmail())) {
            checkForDuplicate(users[0]);
        }
        users[0].setUsername(userToUpdate.getUsername());
        users[0].setJoiningDate(formatToLocalDateTime(userToUpdate.getJoiningDate()));
        users[0].setPermission(userToUpdate.getPermission());
        return userRepository.update(users[0]);
    }

    @Override
    public void delete(Long id, User user) {
        User userToDelete = userRepository.getById(id);
        checkAuthorizedPermissions(userToDelete, user);
        userRepository.delete(id);
    }

    private void checkForDuplicate(User user) {
        boolean duplicateExists = true;

        try {
            userRepository.unique(user.getEmail());
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new DuplicateEntityException("User", "email", user.getEmail());
        }
    }

    private static void checkAuthorizedPermissions(User... users) {
        if (users[1].getPermission().isAdmin()) return;
        if (!users[0].getUsername().equals(users[1].getUsername())) {
            throw new UnauthorizedOperationException(USER_CHANGE_OR_DELETE_ERROR_MESSAGE);
        }
    }
}
