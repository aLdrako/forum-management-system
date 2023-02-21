package com.company.web.forummanagementsystem.controllers;

import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.models.Comment;
import com.company.web.forummanagementsystem.service.CommentServices;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {
    private final CommentServices commentServices;

    public CommentController(CommentServices commentServices) {
        this.commentServices = commentServices;
    }

    @GetMapping("/comments")
    public List<Comment> getAll() {
        return commentServices.getAll();
    }

    @GetMapping("/comments/{id}")
    public Comment getById(@PathVariable Long id) {
        return commentServices.getById(id);
    }

    @PostMapping("/comments")
    public Comment create(@Valid @RequestBody Comment comment) {
        try {
            commentServices.create(comment);
            return comment;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/comments/{id}")
    public Comment update(@PathVariable Long id, @Valid @RequestBody Comment comment) {
        try {
            comment.setId(id);
            commentServices.update(comment);
            return comment;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/comments/{id}")
    public void delete(@PathVariable Long id) {
        try {
            commentServices.delete(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
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
