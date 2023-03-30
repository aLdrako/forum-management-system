package com.telerikacademy.web.fms.controllers.rest;

import com.telerikacademy.web.fms.exceptions.AuthorizationException;
import com.telerikacademy.web.fms.exceptions.EntityDuplicateException;
import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.helpers.AuthenticationHelper;
import com.telerikacademy.web.fms.models.Comment;
import com.telerikacademy.web.fms.models.dto.CommentOutputDTO;
import com.telerikacademy.web.fms.models.dto.PostOutputDTO;
import com.telerikacademy.web.fms.models.validations.CreateValidationGroup;
import com.telerikacademy.web.fms.models.validations.UpdateValidationGroup;
import com.telerikacademy.web.fms.services.ModelMapper;
import com.telerikacademy.web.fms.models.Permission;
import com.telerikacademy.web.fms.models.dto.PermissionDTO;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.models.dto.UserDTO;
import com.telerikacademy.web.fms.services.contracts.CommentServices;
import com.telerikacademy.web.fms.services.contracts.PostServices;
import com.telerikacademy.web.fms.services.contracts.UserServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Tag(name = "User Rest Controller", description = "User management API")
@RestController
@RequestMapping("/api/users")
public class UserRestController {
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

    @Operation(
            summary = "Get all Users",
            description = "Get a list of all User objects",
            tags = {"users", "get all"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All users", content = { @Content(schema = @Schema(implementation = User.class, type="array"), mediaType = "application/json") })
    })
    @GetMapping
    public List<User> getAll() {
        return userServices.getAll();
    }

    @Operation(
            summary = "Retrieve a User by Id",
            description = "Get a User object by specifying its id. The response is User object",
            tags = {"users", "get"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found", content = { @Content(schema = @Schema(implementation = User.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "User not found", content = { @Content(schema = @Schema()) })
    })
    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        try {
            return userServices.getById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(
            summary = "Search users",
            description = "Returns a list of users based on the provided search parameters ('firstName', 'username' or 'email'). If no parameters are provided, returns nothing.",
            tags = {"users", "search"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User(s) found",
                    content = { @Content(schema = @Schema(implementation = User.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "No users found", content = { @Content(schema = @Schema()) }),
    })
    @Parameters({
            @Parameter(name = "firstName", example = "admin", description = "First name of the user", schema = @Schema(type = "string")),
            @Parameter(name = "username", example = "anonymous", description = "Username of the user", schema = @Schema(type = "string")),
            @Parameter(name = "email", example = "tester@mail.com", description = "Email of the user", schema = @Schema(type = "string"))
    })
    @GetMapping("/search")
    public List<User> search(@Parameter(hidden = true) @RequestParam Map<String, String> parameter) {
        if (parameter.size() == 0) parameter = Collections.singletonMap("*", "*");
        try {
            return userServices.search(String.valueOf(parameter.entrySet().iterator().next()));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(
            summary = "Create a new user",
            description = "Create a User object",
            tags = {"users", "create"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "409", description = "User with same email/username already exists", content = { @Content(schema = @Schema()) })
    })
    @PostMapping
    public User create(@Validated(CreateValidationGroup.class) @RequestBody UserDTO userDTO) {
        try {
            User user = modelMapper.dtoToObject(userDTO);
            return userServices.create(user);
        } catch (EntityDuplicateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @Operation(
            summary = "Update a User by Id",
            description = "Update a User object by specifying its id. The response is the updated User object",
            tags = {"users", "update"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated", content = {@Content(schema = @Schema(implementation = User.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "User not found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "409", description = "User with same email/username already exists", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "401", description = "Unauthorized operation", content = {@Content(schema = @Schema())})
    })
    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @Validated(UpdateValidationGroup.class) @RequestBody UserDTO userDTO, @RequestHeader HttpHeaders headers) {
        try {
            User authenticatedUser = authenticationHelper.tryGetUser(headers);
            User user = modelMapper.dtoToObject(id, userDTO);
            return userServices.update(user, authenticatedUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (EntityDuplicateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthorizationException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @Operation(
            summary = "Update permissions for a User by Id",
            description = "Update permissions for a User object by specifying its id. The response is the updated User object",
            tags = {"users", "update"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User permissions updated", content = {@Content(schema = @Schema(implementation = User.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "User not found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "401", description = "Unauthorized operation", content = {@Content(schema = @Schema())})
    })
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

    @Operation(
            summary = "Delete a User by Id",
            description = "Delete a User object by specifying its id.",
            tags = {"users", "delete"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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

    @Operation(
            summary = "Retrieve Posts by User Id",
            description = "Get a list of Posts objects by specifying User id. The response is a list of PostOutputDTO objects",
            tags = {"posts", "get", "sort", "filter"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All posts of a specific user", content = { @Content(schema = @Schema(implementation = PostOutputDTO.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "User not found", content = { @Content(schema = @Schema()) })
    })
    @Parameters({
            @Parameter(name = "title", example = "Java", description = "Filter by title, username, tag, content", schema = @Schema(type = "string")),
            @Parameter(name = "sortBy", example = "content", description = "Sort by content, title, datecreated", schema = @Schema(type = "string")),
            @Parameter(name = "orderBy", example = "desc", description = "Order by Ascending (asc) or Descending (desc)", schema = @Schema(type = "string"))
    })
    @GetMapping("/{userId}/posts")
    public List<PostOutputDTO> getPostsByUserId(@PathVariable Long userId, @Parameter(hidden = true) @RequestParam Map<String, String> parameters) {
        try {
            parameters.put("userId", String.valueOf(userId));
            return modelMapper.objectToDto(postServices.getAll(parameters));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(
            summary = "Get a post by userId and postId",
            description = "Get a post by specifying its userId and postId. The response is a PostOutputDTO object",
            tags = {"posts", "get"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Specific post of a specific user", content = { @Content(schema = @Schema(implementation = PostOutputDTO.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "User or post not found", content = { @Content(schema = @Schema()) })
    })
    @GetMapping("/{userId}/posts/{postId}")
    public PostOutputDTO getPostByUserId(@PathVariable Long userId, @PathVariable Long postId) {
        try {
            return modelMapper.objectToDto(postServices.getPostByUserId(userId, postId));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(
            summary = "Get comments by user ID",
            description = "Retrieve a list of comments created by a specific user by specifying their user ID.",
            tags = {"comments", "get", "sort"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All comments of a specific user", content = { @Content(schema = @Schema(implementation = CommentOutputDTO.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "User not found", content = { @Content(schema = @Schema()) })
    })
    @Parameters({
            @Parameter(name = "sort", example = "content", description = "Sort by content, postedOn, dateCreated", schema = @Schema(type = "string")),
            @Parameter(name = "order", example = "desc", description = "Order by Ascending (asc) or Descending (desc)", schema = @Schema(type = "string"))
    })
    @GetMapping("/{userId}/comments")
    public List<CommentOutputDTO> getCommentsByUserId(@PathVariable Long userId, @Parameter(hidden = true) @RequestParam Map<String, String> parameters) {
        try {
            return commentServices.getCommentsByUserId(userId, parameters).stream().map(modelMapper::objectToDto).toList();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(
            summary = "Retrieve a Comment by User Id and Comment Id",
            description = "Get a Comment object by specifying its User id and Comment id. The response is Comment object",
            tags = {"comments", "get"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Specific comment of a specific user", content = { @Content(schema = @Schema(implementation = CommentOutputDTO.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "User or comment not found", content = { @Content(schema = @Schema()) })
    })
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
