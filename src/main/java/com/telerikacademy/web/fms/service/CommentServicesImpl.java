package com.company.web.forummanagementsystem.service;

import com.company.web.forummanagementsystem.exceptions.UnauthorizedOperationException;
import com.company.web.forummanagementsystem.models.Comment;
import com.company.web.forummanagementsystem.models.User;
import com.company.web.forummanagementsystem.repositories.contracts.CommentRepository;
import com.company.web.forummanagementsystem.repositories.contracts.PostRepository;
import com.company.web.forummanagementsystem.repositories.contracts.UserRepository;
import com.company.web.forummanagementsystem.service.contracts.CommentServices;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class CommentServicesImpl implements CommentServices {

    private static final String UNAUTHORIZED_ERROR_MESSAGE = "Only the owner of comment or admin can delete or update comment!";
    private static final String BLOCKED_ERROR_MESSAGE = "User is blocked and cannot create/edit/delete comment!";
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentServicesImpl(CommentRepository commentRepository, UserRepository userRepository, PostRepository postRepository) {
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
    public Comment create(Comment comment, User user) {
        checkAuthorizedPermissions(comment, user);
        return commentRepository.create(comment);
    }

    @Override
    public Comment update(Comment comment, User user) {
        checkAuthorizedPermissions(comment, user);
        return commentRepository.update(comment);
    }

    @Override
    public void delete(Long id, User user) {
        Comment comment = commentRepository.getById(id);
        checkAuthorizedPermissions(comment, user);
        commentRepository.delete(id);
    }

    @Override
    public List<Comment> getCommentsByUserId(Long userId, Map<String, String> parameters) {
        userRepository.getById(userId);
        return commentRepository.getCommentsByUserId(userId, parameters);
    }

    @Override
    public Comment getCommentByUserId(Long userId, Long commentId) {
        userRepository.getById(userId);
        return commentRepository.getCommentByUserId(userId, commentId);
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId, Map<String, String> parameters) {
        postRepository.getById(postId);
        return commentRepository.getCommentsByPostId(postId, parameters);
    }

    @Override
    public Comment getCommentByPostId(Long postId, Long commentId) {
        postRepository.getById(postId);
        return commentRepository.getCommentByPostId(postId, commentId);
    }

    private void checkAuthorizedPermissions(Comment comment, User user) {
        if (user.getPermission().isBlocked()) {
            throw new UnauthorizedOperationException(BLOCKED_ERROR_MESSAGE);
        }
        if (user.getPermission().isAdmin() ||
                Objects.equals(comment.getCreatedBy().getId(), user.getId())) return;

        throw new UnauthorizedOperationException(UNAUTHORIZED_ERROR_MESSAGE);
    }
}