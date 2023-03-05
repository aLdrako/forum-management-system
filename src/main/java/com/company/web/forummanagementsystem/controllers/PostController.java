package com.company.web.forummanagementsystem.controllers;

import com.company.web.forummanagementsystem.exceptions.AuthorizationException;
import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.exceptions.UnauthorizedOperationException;
import com.company.web.forummanagementsystem.helpers.AuthenticationHelper;
import com.company.web.forummanagementsystem.helpers.ModelMapper;
import com.company.web.forummanagementsystem.models.Post;
import com.company.web.forummanagementsystem.models.PostDTO;
import com.company.web.forummanagementsystem.models.User;
import com.company.web.forummanagementsystem.service.PostServices;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PostController {
    private final PostServices postServices;
    private final ModelMapper postMapper;

    private final AuthenticationHelper authenticationHelper;

    public PostController(PostServices postServices, ModelMapper postMapper,
                          AuthenticationHelper authenticationHelper) {
        this.postServices = postServices;
        this.postMapper = postMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/posts")
    public List<Post> getAll(@RequestParam(required = false) Optional<String> title,
                             @RequestParam(required = false) Optional<String> sortBy,
                             @RequestParam(required = false) Optional<String> orderBy) {
        return postServices.getAll(Optional.empty(), title, sortBy, orderBy);
    }

    @GetMapping("/posts/{id}")
    public Post getById(@PathVariable Long id) {
        try {
            return postServices.getById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/posts")
    public Post create(@Valid @RequestBody PostDTO postDTO, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Post post = postMapper.dtoToObject(postDTO);
            post.setUserCreated(user);
            return postServices.create(post, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/posts/{id}")
    public Post update(@PathVariable Long id, @Valid @RequestBody PostDTO postDTO,
                       @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Post post = postMapper.dtoToObject(id, postDTO);
            return postServices.update(post, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException |  AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/posts/{id}")
    public void delete(@PathVariable Long id, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            postServices.delete(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException | AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/users/{userId}/posts")
    public List<Post> getPostsByUserId(@PathVariable Long userId,
                                    @RequestParam(required = false) Optional<String> title,
                                    @RequestParam(required = false) Optional<String> sortBy,
                                    @RequestParam(required = false) Optional<String> orderBy) {
        try {
            return postServices.getAll(userId.describeConstable(), title, sortBy, orderBy);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/users/{userId}/posts/{postId}")
    public Post getPostByUserId(@PathVariable Long userId, @PathVariable Long postId) {
        try {
            return postServices.getPostByUserId(userId, postId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
