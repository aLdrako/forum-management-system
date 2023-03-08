package com.telerikacademy.web.fms.controllers;

import com.telerikacademy.web.fms.exceptions.AuthorizationException;
import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.helpers.AuthenticationHelper;
import com.telerikacademy.web.fms.helpers.ModelMapper;
import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.models.dto.PostDTO;
import com.telerikacademy.web.fms.models.dto.PostOutputDTO;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.services.contracts.PostServices;
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
    public PostOutputDTO create(@Valid @RequestBody PostDTO postDTO, @RequestHeader HttpHeaders headers) {
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
            postServices.update(post, user);
            postServices.updateTagsInPost(postDTO.getTags(), post);
            return postMapper.dtoToObject(post);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException |  AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/posts/{id}/like")
    public void changePostLikes(@PathVariable Long id,
                                @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            postServices.changePostLikes(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException | AuthorizationException e) {
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
