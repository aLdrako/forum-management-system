package com.telerikacademy.web.fms.service.contracts;

import com.telerikacademy.web.fms.models.Comment;
import com.telerikacademy.web.fms.models.User;

import java.util.List;
import java.util.Map;

public interface CommentServices {
    List<Comment> getAll();
    Comment getById(Long id);
    Comment create(Comment comment, User user);
    Comment update(Comment comment, User user);
    void delete(Long id, User user);
    List<Comment> getCommentsByUserId(Long userId, Map<String, String> parameters);
    Comment getCommentByUserId(Long userId, Long commentId);
    List<Comment> getCommentsByPostId(Long postId, Map<String, String> parameters);
    Comment getCommentByPostId(Long postId, Long commentId);
}
