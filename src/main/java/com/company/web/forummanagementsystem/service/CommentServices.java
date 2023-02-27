package com.company.web.forummanagementsystem.service;

import com.company.web.forummanagementsystem.models.Comment;
import com.company.web.forummanagementsystem.models.User;

import java.util.List;

public interface CommentServices {
    List<Comment> getAll();
    Comment getById(Long id);
    Comment create(Comment comment, User user);
    Comment update(Comment comment, User user);
    void delete(Long id, User user);
    List<Comment> getCommentsByUserId(Long userId);
    Comment getCommentByUserId(Long userId, Long commentId);
    List<Comment> getCommentsByPostId(Long postId);
    Comment getCommentByPostId(Long postId, Long commentId);
}
