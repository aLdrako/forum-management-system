package com.company.web.forummanagementsystem.service;

import com.company.web.forummanagementsystem.exceptions.DuplicateEntityException;
import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.exceptions.UnauthorizedOperationException;
import com.company.web.forummanagementsystem.models.User;
import com.company.web.forummanagementsystem.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServicesImpl implements UserServices {
    private final static String USER_CHANGE_OR_DELETE_ERROR_MESSAGE = "Only admins and owners of the account can delete or change their account!";
    private final static String UPDATE_ADMIN_PERMISSION_ERROR_MESSAGE = "Only admin can modify these settings privileges: <<Delete>>, <<Block>>, <<Set Admin>>!";
    private static final String UPDATE_DELETE_FLAG_ERROR_MESSAGE = "Deletion is restricted operation!";
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
     *
     * @param users users[0] - user from Requested Body (JSON)
     *              users[1] - authenticated user from Header (Http Header)
     * @return updated user from DB
     *
     */
    @Override
    public User update(User... users) {
        User userToUpdate = userRepository.getById(users[0].getId());
        checkAuthorizedPermissions(USER_CHANGE_OR_DELETE_ERROR_MESSAGE, userToUpdate, users[1]);
        checkAdminPermissions(users[0], users[1], userToUpdate);
        if (!userToUpdate.getEmail().equals(users[0].getEmail())) {
            checkForDuplicate(users[0]);
        }
        return userRepository.update(users[0]);
    }

    @Override
    public void delete(Long id, User user) {
        User userToDelete = userRepository.getById(id);
        checkAuthorizedPermissions(USER_CHANGE_OR_DELETE_ERROR_MESSAGE, userToDelete, user);
        userRepository.delete(id);
    }

    private void checkForDuplicate(User user) {
        boolean duplicateExists = true;

        try {
            userRepository.search("email=" + user.getEmail());
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new DuplicateEntityException("User", "email", user.getEmail());
        }
    }

    private static void checkAuthorizedPermissions(String message, User... users) {
        if (users[1].isAdmin()) return;
        if (!users[0].getUsername().equals(users[1].getUsername())) {
            throw new UnauthorizedOperationException(message);
        }
    }

    /**
     *
     * @param users users[0] - user from Requested Body (JSON)
     *              users[1] - authenticated user from Header (Http Header)
     *              users[2] - user to update retrieved from DB by ID
     * users[2] is needed to prevent him from removing (set to false) admin restrictions
     */
    private static void checkAdminPermissions(User... users) {
        if (users[0].isDeleted()) throw new UnauthorizedOperationException(UPDATE_DELETE_FLAG_ERROR_MESSAGE);
        if (users[1].isAdmin()) return;

        if ((users[0].isAdmin() || users[0].isBlocked()) && !users[1].isAdmin()) {
            throw new UnauthorizedOperationException(UPDATE_ADMIN_PERMISSION_ERROR_MESSAGE);
        }
        if (users[2].isBlocked() && !users[0].isBlocked()) users[0].setBlocked(true);
    }
}
