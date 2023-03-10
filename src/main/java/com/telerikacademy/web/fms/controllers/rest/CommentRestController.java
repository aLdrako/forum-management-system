package com.telerikacademy.web.fms.controllers.rest;

import com.telerikacademy.web.fms.exceptions.AuthorizationException;
import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.helpers.AuthenticationHelper;
import com.telerikacademy.web.fms.services.ModelMapper;
import com.telerikacademy.web.fms.models.Comment;
import com.telerikacademy.web.fms.models.dto.CommentDTO;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.models.dto.CommentOutputDTO;
import com.telerikacademy.web.fms.models.validations.CreateValidationGroup;
import com.telerikacademy.web.fms.models.validations.UpdateValidationGroup;
import com.telerikacademy.web.fms.services.contracts.CommentServices;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CommentRestController {
    private final ModelMapper modelMapper;
    private final CommentServices commentServices;
    private final AuthenticationHelper authenticationHelper;

    public CommentRestController(ModelMapper modelMapper, CommentServices commentServices, AuthenticationHelper authenticationHelper) {
        this.modelMapper = modelMapper;
        this.commentServices = commentServices;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/comments")
    public List<CommentOutputDTO> getAll() {
        return commentServices.getAll().stream().map(modelMapper::objectToDto).toList();
    }

    @GetMapping("/comments/{id}")
    public CommentOutputDTO getById(@PathVariable Long id) {
        try {
            Comment comment = commentServices.getById(id);
            return modelMapper.objectToDto(comment);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/comments")
    public CommentOutputDTO create(@Validated(CreateValidationGroup.class) @RequestBody CommentDTO commentDTO, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Comment comment = modelMapper.dtoToObject(commentDTO);
            comment.setCreatedBy(user);
            return modelMapper.objectToDto(commentServices.create(comment, user));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/comments/{id}")
    public CommentOutputDTO update(@PathVariable Long id, @Validated(UpdateValidationGroup.class) @RequestBody CommentDTO commentDTO, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Comment comment = modelMapper.dtoToObject(id, commentDTO);
            return modelMapper.objectToDto(commentServices.update(comment, user));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/comments/{id}")
    public void delete(@PathVariable Long id, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            commentServices.delete(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    // TODO - MOVED TO USER CONTROLLER - UNDER REVIEW / TO BE REMOVED
//    @GetMapping("users/{userId}/comments")
    public List<CommentOutputDTO> getCommentsByUserId(@PathVariable Long userId, @RequestParam Map<String, String> parameters) {
        try {
            return commentServices.getCommentsByUserId(userId, parameters).stream().map(modelMapper::objectToDto).toList();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // TODO - MOVED TO USER CONTROLLER - UNDER REVIEW / TO BE REMOVED
//    @GetMapping("users/{userId}/comments/{commentId}")
    public CommentOutputDTO getCommentByUserId(@PathVariable Long userId, @PathVariable Long commentId) {
        try {
            Comment comment = commentServices.getCommentByUserId(userId, commentId);
            return modelMapper.objectToDto(comment);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // TODO - TO BE MOVED TO USER POSTS - UNDER REVIEW / TO BE REMOVED
    @GetMapping("posts/{postId}/comments")
    public List<CommentOutputDTO> getCommentsByPostId(@PathVariable Long postId, @RequestParam Map<String, String> parameters) {
        try {
            return commentServices.getCommentsByPostId(postId, parameters).stream().map(modelMapper::objectToDto).toList();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // TODO - TO BE MOVED TO USER POSTS - UNDER REVIEW / TO BE REMOVED
    @GetMapping("posts/{postId}/comments/{commentId}")
    public CommentOutputDTO getCommentByPostId(@PathVariable Long postId, @PathVariable Long commentId) {
        try {
            Comment comment = commentServices.getCommentByPostId(postId, commentId);
            return modelMapper.objectToDto(comment);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
