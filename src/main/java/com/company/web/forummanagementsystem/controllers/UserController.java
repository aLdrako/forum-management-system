package com.company.web.forummanagementsystem.controllers;

import com.company.web.forummanagementsystem.exceptions.DuplicateEntityException;
import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.models.User;
import com.company.web.forummanagementsystem.service.UserServices;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServices userServices;

    public UserController(UserServices userServices) {
        this.userServices = userServices;
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
    public User getByUsername(@RequestParam String username) {
        try {
            return userServices.getByUsername(username);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        try {
            return userServices.create(user);
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @Valid @RequestBody User user) {
        try {
            user.setId(id);
            return userServices.update(user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
//        return user;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        try {
            userServices.delete(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * sample method, to test request with params - can be deleted later (with all it's dependencies)
     * @param id
     * @param username
     * @return
     */
    @GetMapping("/params")
    public List<User> getAllWithParams(@RequestParam(required = false) Optional<Long> id, @RequestParam(required = false) Optional<String> username) {
        return userServices.getAllWithParams(id, username);
    }
}
