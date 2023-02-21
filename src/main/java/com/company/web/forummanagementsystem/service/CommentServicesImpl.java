package com.company.web.forummanagementsystem.service;

import com.company.web.forummanagementsystem.models.Comment;
import com.company.web.forummanagementsystem.repositories.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServicesImpl implements CommentServices {

    private final CommentRepository commentRepository;

    public CommentServicesImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<Comment> getAll() {
        return commentRepository.getAll();
    }

    @Override
    public Comment getById(Long id) {
        return commentRepository.getById(id);
    }

    @Override
    public void create(Comment comment) {
        commentRepository.create(comment);
    }

    @Override
    public void update(Comment comment) {
        commentRepository.update(comment);
    }

    @Override
    public void delete(Long id) {
        commentRepository.delete(id);
    }

    @Override
    public List<Comment> getCommentsByUserId(Long userId) {
        return commentRepository.getCommentsByUserId(userId);
    }

    @Override
    public Comment getCommentByUserId(Long userId, Long commentId) {
        return commentRepository.getCommentByUserId(userId, commentId);
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.getCommentsByPostId(postId);
    }

    @Override
    public Comment getCommentByPostId(Long postId, Long commentId) {
        return commentRepository.getCommentByPostId(postId, commentId);
    }
}
