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

    }

    @Override
    public void update(Comment comment) {

    }

    @Override
    public void delete(Comment comment) {

    }
}
