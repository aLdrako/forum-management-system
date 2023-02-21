package com.company.web.forummanagementsystem.service;

import com.company.web.forummanagementsystem.models.Comment;

import java.util.List;

public interface CommentServices {
    List<Comment> getAll();
    Comment getById(Long id);
    void create(Comment comment);
    void update(Comment comment);
    void delete(Long id);
    List<Comment> getCommentsByUserId(Long userId);
    Comment getCommentByUserId(Long userId, Long commentId);
    List<Comment> getCommentsByPostId(Long postId);
    Comment getCommentByPostId(Long postId, Long commentId);
}
