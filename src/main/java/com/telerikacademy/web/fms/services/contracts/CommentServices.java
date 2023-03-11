package com.telerikacademy.web.fms.services.contracts;

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
}
