package com.telerikacademy.web.fms.controllers.rest;

import com.telerikacademy.web.fms.exceptions.AuthorizationException;
import com.telerikacademy.web.fms.exceptions.DuplicateEntityException;
import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.helpers.AuthenticationHelper;
import com.telerikacademy.web.fms.models.Comment;
import com.telerikacademy.web.fms.models.dto.CommentOutputDTO;
import com.telerikacademy.web.fms.models.dto.PostOutputDTO;
import com.telerikacademy.web.fms.models.validations.UpdateValidationGroup;
import com.telerikacademy.web.fms.services.ModelMapper;
import com.telerikacademy.web.fms.models.Permission;
import com.telerikacademy.web.fms.models.dto.PermissionDTO;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.models.dto.UserDTO;
import com.telerikacademy.web.fms.services.contracts.CommentServices;
import com.telerikacademy.web.fms.services.contracts.PostServices;
import com.telerikacademy.web.fms.services.contracts.UserServices;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static com.telerikacademy.web.fms.helpers.FilterAndSortParameters.*;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    private static final String USER_NO_COMMENT_WITH_ID = "User with id %d does not have comment with id %d!";
    private final ModelMapper modelMapper;
    private final UserServices userServices;
    private final PostServices postServices;
    private final CommentServices commentServices;
    private final AuthenticationHelper authenticationHelper;

    public UserRestController(ModelMapper modelMapper, UserServices userServices, PostServices postServices, CommentServices commentServices, AuthenticationHelper authenticationHelper) {
        this.modelMapper = modelMapper;
        this.userServices = userServices;
        this.postServices = postServices;
        this.commentServices = commentServices;
        this.authenticationHelper = authenticationHelper;
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
            User user = modelMapper.dtoToObject(userDTO);
            return userServices.create(user);
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @Validated(UpdateValidationGroup.class) @RequestBody UserDTO userDTO, @RequestHeader HttpHeaders headers) {
        try {
            User authenticatedUser = authenticationHelper.tryGetUser(headers);
            User user = modelMapper.dtoToObject(id, userDTO);
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
            User authenticatedUser = authenticationHelper.tryGetUser(headers);
            Permission permission = modelMapper.dtoToObject(id, permissionDTO);
            return userServices.updatePermissions(permission, authenticatedUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            userServices.delete(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/{userId}/posts")
    public List<PostOutputDTO> getPostsByUserId(@PathVariable Long userId,
                                                @RequestParam(required = false) Optional<String> title,
                                                @RequestParam(required = false) Optional<String> sortBy,
                                                @RequestParam(required = false) Optional<String> orderBy) {
        try {
            return modelMapper.objectToDto(postServices.getAll(userId.describeConstable(),
                    title, sortBy, orderBy));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{userId}/posts/{postId}")
    public PostOutputDTO getPostByUserId(@PathVariable Long userId, @PathVariable Long postId) {
        try {
            return modelMapper.objectToDto(postServices.getPostByUserId(userId, postId));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{userId}/comments")
    public List<CommentOutputDTO> getCommentsByUserId(@PathVariable Long userId, @RequestParam Map<String, String> parameters) {
        try {
            return commentServices.getCommentsByUserId(userId, parameters).stream().map(modelMapper::objectToDto).toList();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{userId}/comments/{commentId}")
    public CommentOutputDTO getCommentByUserId(@PathVariable Long userId, @PathVariable Long commentId) {
        try {
            Comment comment = commentServices.getCommentByUserId(userId, commentId);
            return modelMapper.objectToDto(comment);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
