package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.models.Comment;

import java.util.List;

public interface CommentRepository {
    List<Comment> getAll();
    Comment getById(Long id);
    void create(Comment comment);
    void update(Comment comment);
    void delete(Comment comment);
}
