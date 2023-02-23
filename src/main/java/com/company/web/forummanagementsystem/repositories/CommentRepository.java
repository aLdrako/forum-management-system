package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.models.Comment;

import java.util.List;

public interface CommentRepository {
    List<Comment> getAll();
    Comment getById(Long id);
    Comment create(Comment comment);
    Comment update(Comment comment);
    void delete(Long id);
    List<Comment> getCommentsByUserId(Long userId);
    Comment getCommentByUserId(Long userId, Long commentId);
    List<Comment> getCommentsByPostId(Long postId);
    Comment getCommentByPostId(Long postId, Long commentId);
}
