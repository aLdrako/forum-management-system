package com.company.web.forummanagementsystem.controllers;

import com.company.web.forummanagementsystem.exceptions.AuthorizationException;
import com.company.web.forummanagementsystem.exceptions.DuplicateEntityException;
import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.exceptions.UnauthorizedOperationException;
import com.company.web.forummanagementsystem.helpers.AuthenticationHelper;
import com.company.web.forummanagementsystem.helpers.PermissionMapper;
import com.company.web.forummanagementsystem.helpers.UserMapper;
import com.company.web.forummanagementsystem.models.Permission;
import com.company.web.forummanagementsystem.models.PermissionDTO;
import com.company.web.forummanagementsystem.models.User;
import com.company.web.forummanagementsystem.models.UserDTO;
import com.company.web.forummanagementsystem.service.PermissionServices;
import com.company.web.forummanagementsystem.service.UserServices;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServices userServices;
    private final UserMapper userMapper;
    private final PermissionServices permissionServices;
    private final PermissionMapper permissionMapper;
    private final AuthenticationHelper authentication;

    public UserController(UserServices userServices, UserMapper userMapper, AuthenticationHelper authentication, PermissionServices permissionServices, PermissionMapper permissionMapper) {
        this.userServices = userServices;
        this.userMapper = userMapper;
        this.permissionServices = permissionServices;
        this.permissionMapper = permissionMapper;
        this.authentication = authentication;
    }

    @GetMapping
    public List<User> getAll() {
        return userServices.getAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        try {
            return userServices.getById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/search")
    public List<User> search(@RequestParam Map<String, String> parameter) {
        if (parameter.size() == 0) parameter = Collections.singletonMap("*", "*");
        try {
            return userServices.search(String.valueOf(parameter.entrySet().iterator().next()));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public User create(@Valid @RequestBody UserDTO userDTO) {
        try {
            User user = userMapper.dtoToObject(userDTO);
            return userServices.create(user);
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO, @RequestHeader HttpHeaders headers) {
        try {
            User user = userMapper.dtoToObject(id, userDTO);
            User authenticatedUser = authentication.tryGetUser(headers);
            return userServices.update(user, authenticatedUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthorizationException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{id}/permissions")
    public User updatePermissions(@PathVariable Long id, @RequestBody PermissionDTO permissionDTO, @RequestHeader HttpHeaders headers) {
        try {
            User authenticatedUser = authentication.tryGetUser(headers);
            Permission permission = permissionMapper.dtoToObject(id, permissionDTO);
            permissionServices.update(permission, authenticatedUser);
            return userServices.getById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @RequestHeader HttpHeaders headers) {
        try {
            User user = authentication.tryGetUser(headers);
            userServices.delete(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
