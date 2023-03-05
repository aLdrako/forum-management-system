package com.company.web.forummanagementsystem.controllers;

import com.company.web.forummanagementsystem.exceptions.AuthorizationException;
import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.exceptions.UnauthorizedOperationException;
import com.company.web.forummanagementsystem.helpers.AuthenticationHelper;
import com.company.web.forummanagementsystem.helpers.ModelMapper;
import com.company.web.forummanagementsystem.models.Comment;
import com.company.web.forummanagementsystem.models.CommentDTO;
import com.company.web.forummanagementsystem.models.User;
import com.company.web.forummanagementsystem.service.CommentServices;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {
    private final ModelMapper modelMapper;
    private final CommentServices commentServices;
    private final AuthenticationHelper authenticationHelper;

    public CommentController(ModelMapper modelMapper, CommentServices commentServices, AuthenticationHelper authenticationHelper) {
        this.modelMapper = modelMapper;
        this.commentServices = commentServices;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/comments")
    public List<Comment> getAll() {
        return commentServices.getAll();
    }

    @GetMapping("/comments/{id}")
    public Comment getById(@PathVariable Long id) {
        try {
            return commentServices.getById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/comments")
    public Comment create(@Valid @RequestBody CommentDTO commentDTO, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            commentDTO.setUserId(user.getId()); // taking ID from authenticated user
            Comment comment = modelMapper.dtoToObject(commentDTO);
            return commentServices.create(comment, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException | UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/comments/{id}")
    public Comment update(@PathVariable Long id, @Valid @RequestBody CommentDTO commentDTO, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Comment comment = modelMapper.dtoToObject(id, commentDTO);
            return commentServices.update(comment, user);
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

    @GetMapping("users/{userId}/comments")
    public List<Comment> getCommentsByUserId(@PathVariable Long userId) {
        try {
            return commentServices.getCommentsByUserId(userId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("users/{userId}/comments/{commentId}")
    public Comment getCommentByUserId(@PathVariable Long userId, @PathVariable Long commentId) {
        try {
            return commentServices.getCommentByUserId(userId, commentId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("posts/{postId}/comments")
    public List<Comment> getCommentsByPostId(@PathVariable Long postId) {
        try {
            return commentServices.getCommentsByPostId(postId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("posts/{postId}/comments/{commentId}")
    public Comment getCommentByPostId(@PathVariable Long postId, @PathVariable Long commentId) {
        try {
            return commentServices.getCommentByPostId(postId, commentId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
