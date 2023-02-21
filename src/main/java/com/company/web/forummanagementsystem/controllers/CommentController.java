package com.company.web.forummanagementsystem.controllers;

import com.company.web.forummanagementsystem.models.Comment;
import com.company.web.forummanagementsystem.service.CommentServices;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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


    // TODO handle exceptions for user and post ids
    @PostMapping("/comments")
    public Comment create(@Valid @RequestBody Comment comment) {
        commentServices.create(comment);
        return comment;
    }
}
