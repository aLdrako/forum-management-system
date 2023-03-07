package com.company.web.forummanagementsystem.controllers;

import com.company.web.forummanagementsystem.exceptions.AuthorizationException;
import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.exceptions.UnauthorizedOperationException;
import com.company.web.forummanagementsystem.helpers.AuthenticationHelper;
import com.company.web.forummanagementsystem.helpers.ModelMapper;
import com.company.web.forummanagementsystem.models.Post;
import com.company.web.forummanagementsystem.models.dto.PostDTO;
import com.company.web.forummanagementsystem.models.dto.PostOutputDTO;
import com.company.web.forummanagementsystem.models.User;
import com.company.web.forummanagementsystem.models.validations.CreateValidationGroup;
import com.company.web.forummanagementsystem.service.contracts.PostServices;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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
    public List<PostOutputDTO> getAll(@RequestParam(required = false) Optional<String> title,
                                      @RequestParam(required = false) Optional<String> sortBy,
                                      @RequestParam(required = false) Optional<String> orderBy) {
        return postMapper.dtoToObject(postServices.getAll(Optional.empty(), title, sortBy, orderBy));
    }

    @GetMapping("/posts/{id}")
    public PostOutputDTO getById(@PathVariable Long id) {
        try {
           Post post = postServices.getById(id);
           return postMapper.dtoToObject(post);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/posts")
    public PostOutputDTO create(@Validated(CreateValidationGroup.class) @RequestBody PostDTO postDTO, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Post post = postMapper.dtoToObject(postDTO);
            post.setUserCreated(user);
            Post postCreated = postServices.create(post, user);
            return postMapper.dtoToObject(postCreated);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/posts/{id}")
    public PostOutputDTO update(@PathVariable Long id, @Valid @RequestBody PostDTO postDTO,
                       @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Post post = postMapper.dtoToObject(id, postDTO);
            Post updatedPost = postServices.update(post, user);
            return postMapper.dtoToObject(updatedPost);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException |  AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/posts/{id}/like")
    public void addLike(@PathVariable Long id,
                         @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            postServices.changePostLikes(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
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
    public List<PostOutputDTO> getPostsByUserId(@PathVariable Long userId,
                                    @RequestParam(required = false) Optional<String> title,
                                    @RequestParam(required = false) Optional<String> sortBy,
                                    @RequestParam(required = false) Optional<String> orderBy) {
        try {
            return postMapper.dtoToObject(postServices.getAll(userId.describeConstable(),
                    title, sortBy, orderBy));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/users/{userId}/posts/{postId}")
    public PostOutputDTO getPostByUserId(@PathVariable Long userId, @PathVariable Long postId) {
        try {
            return postMapper.dtoToObject(postServices.getPostByUserId(userId, postId));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
