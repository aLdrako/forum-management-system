package com.telerikacademy.web.fms.repositories.contracts;

import com.telerikacademy.web.fms.models.Comment;

import java.util.List;

public interface CommentRepository {
    List<Comment> getAll();
    Comment getById(Long id);
    Comment create(Comment comment);
    Comment update(Comment comment);
    void delete(Long id);
}
