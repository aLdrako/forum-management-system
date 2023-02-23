package com.company.web.forummanagementsystem.service;

import com.company.web.forummanagementsystem.models.Comment;
import com.company.web.forummanagementsystem.repositories.CommentRepository;
import com.company.web.forummanagementsystem.repositories.PostRepository;
import com.company.web.forummanagementsystem.repositories.PostRepositoryImpl;
import com.company.web.forummanagementsystem.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServicesImpl implements CommentServices {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentServicesImpl(CommentRepository commentRepository, UserRepository userRepository,
                               PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
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
    public Comment create(Comment comment) {
        return commentRepository.create(comment);
    }

    @Override
    public Comment update(Comment comment) {
        return commentRepository.update(comment);
    }

    @Override
    public void delete(Long id) {
        commentRepository.delete(id);
    }

    @Override
    public List<Comment> getCommentsByUserId(Long userId) {
        userRepository.getById(userId);
        return commentRepository.getCommentsByUserId(userId);
    }

    @Override
    public Comment getCommentByUserId(Long userId, Long commentId) {
        userRepository.getById(userId);
        return commentRepository.getCommentByUserId(userId, commentId);
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        postRepository.getById(postId);
        return commentRepository.getCommentsByPostId(postId);
    }

    @Override
    public Comment getCommentByPostId(Long postId, Long commentId) {
        postRepository.getById(postId);
        return commentRepository.getCommentByPostId(postId, commentId);
    }
}
