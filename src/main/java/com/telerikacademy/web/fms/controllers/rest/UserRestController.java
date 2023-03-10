package com.telerikacademy.web.fms.controllers.rest;

import com.telerikacademy.web.fms.exceptions.AuthorizationException;
import com.telerikacademy.web.fms.exceptions.DuplicateEntityException;
import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.helpers.AuthenticationHelper;
import com.telerikacademy.web.fms.models.dto.CommentOutputDTO;
import com.telerikacademy.web.fms.models.dto.PostOutputDTO;
import com.telerikacademy.web.fms.models.validations.UpdateValidationGroup;
import com.telerikacademy.web.fms.services.ModelMapper;
import com.telerikacademy.web.fms.models.Permission;
import com.telerikacademy.web.fms.models.dto.PermissionDTO;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.models.dto.UserDTO;
import com.telerikacademy.web.fms.services.contracts.PermissionServices;
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
    private static final String USER_NO_POST_WITH_ID = "User with id %d does not have post with id %d!";
    private final ModelMapper modelMapper;
    private final UserServices userServices;
    private final PermissionServices permissionServices;
    private final AuthenticationHelper authenticationHelper;

    public UserRestController(ModelMapper modelMapper, UserServices userServices, AuthenticationHelper authenticationHelper, PermissionServices permissionServices) {
        this.modelMapper = modelMapper;
        this.userServices = userServices;
        this.permissionServices = permissionServices;
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
            User user = authenticationHelper.tryGetUser(headers);
            userServices.delete(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/{id}/posts")
    public List<PostOutputDTO> getUserPosts(@PathVariable Long id,  @RequestParam Map<String, String> parameters) {
        try {
            return userServices.getById(id).getPosts().stream()
                    .filter(getPostFilter(parameters))
                    .map(modelMapper::objectToDto)
                    .sorted(getPostDTOComparator(parameters)).toList();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

        @GetMapping("/{userId}/posts/{postId}")
    public PostOutputDTO getUserPost(@PathVariable Long userId, @PathVariable Long postId) {
        try {
            return userServices.getById(userId).getPosts().stream()
                    .filter(comment -> Objects.equals(comment.getId(), postId))
                    .map(modelMapper::objectToDto).findFirst()
                    .orElseThrow(() -> new EntityNotFoundException(String.format(USER_NO_POST_WITH_ID, userId, postId)));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}/comments")
    public List<CommentOutputDTO> getUserComments(@PathVariable Long id, @RequestParam Map<String, String> parameters) {
        try {
            return userServices.getById(id).getComments().stream()
                    .map(modelMapper::objectToDto)
                    .sorted(getCommentDTOComparator(parameters)).toList();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{userId}/comments/{commentId}")
    public CommentOutputDTO getUserComment(@PathVariable Long userId, @PathVariable Long commentId) {
        try {
            return userServices.getById(userId).getComments().stream()
                    .filter(comment -> Objects.equals(comment.getId(), commentId))
                    .map(modelMapper::objectToDto).findFirst()
                    .orElseThrow(() -> new EntityNotFoundException(String.format(USER_NO_COMMENT_WITH_ID, userId, commentId)));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
