package com.telerikacademy.web.fms.service;

import com.telerikacademy.web.fms.exceptions.DuplicateEntityException;
import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.repositories.contracts.UserRepository;
import com.telerikacademy.web.fms.service.contracts.UserServices;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.telerikacademy.web.fms.helpers.DateTimeFormat.formatToLocalDateTime;

@Service
public class UserServicesImpl implements UserServices {
    private final static String USER_CHANGE_OR_DELETE_ERROR_MESSAGE = "Only admin or owner of the account can delete or change their account!";
    private final static String SUPER_USER_DELETION_ERROR_MESSAGE = "Super user cannot be deleted!";
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
     * {@code @Description} reset users[0] username to prevent username change
     */
    @Override
    public User update(User... users) {
        User userToUpdate = userRepository.getById(users[0].getId());
        checkAuthorizedPermissions(userToUpdate, users[1]);
        users[0].setJoiningDate(formatToLocalDateTime(userToUpdate.getJoiningDate()));
        users[0].setPermission(userToUpdate.getPermission());
        users[0].setUsername("***");
        if (!userToUpdate.getEmail().equals(users[0].getEmail())) checkForDuplicate(users[0]);
        users[0].setUsername(userToUpdate.getUsername());
        return userRepository.update(users[0]);
    }

    @Override
    public void delete(Long id, User user) {
        if (id == 1) throw new UnauthorizedOperationException(SUPER_USER_DELETION_ERROR_MESSAGE);
        User userToDelete = userRepository.getById(id);
        checkAuthorizedPermissions(userToDelete, user);
        userRepository.delete(id);
    }

    private void checkForDuplicate(User user) {
        boolean duplicateEmail = true;
        boolean duplicateUsername = true;

        try {
            userRepository.search("email=" + user.getEmail());
        } catch (EntityNotFoundException e) {
            duplicateEmail = false;
        }
        if (duplicateEmail) throw new DuplicateEntityException("User", "email", user.getEmail());

        try {
            userRepository.search("username=" + user.getUsername());
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
