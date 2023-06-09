package com.telerikacademy.web.fms.repositories.contracts;

import com.telerikacademy.web.fms.models.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface CommentRepository {
    List<Comment> getAll(Map<String, String> parameters);

    Page<Comment> findAll(Map<String, String> parameters, Pageable pageable);

    Comment getById(Long id);

    Comment create(Comment comment);

    Comment update(Comment comment);

    void delete(Long id);

    List<Comment> getCommentsByUserId(Long userId, Map<String, String> parameters);

    Comment getCommentByUserId(Long userId, Long commentId);

    List<Comment> getCommentsByPostId(Long postId, Map<String, String> parameters);

    Comment getCommentByPostId(Long postId, Long commentId);
}
