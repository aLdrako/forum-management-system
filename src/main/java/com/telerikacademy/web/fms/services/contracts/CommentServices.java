package com.telerikacademy.web.fms.services.contracts;

import com.telerikacademy.web.fms.models.Comment;
import com.telerikacademy.web.fms.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface CommentServices {
    List<Comment> getAll(Map<String, String> parameters);

    Page<Comment> findAll(Map<String, String> parameters, Pageable pageable);

    Comment getById(Long id);

    Comment create(Comment comment, User user);

    Comment update(Comment comment, User user);

    void delete(Long id, User user);

    List<Comment> getCommentsByUserId(Long userId, Map<String, String> parameters);

    Comment getCommentByUserId(Long userId, Long commentId);

    List<Comment> getCommentsByPostId(Long postId, Map<String, String> parameters);

    Comment getCommentByPostId(Long postId, Long commentId);
}
