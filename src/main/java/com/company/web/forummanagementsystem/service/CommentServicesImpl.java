package com.company.web.forummanagementsystem.service;

import com.company.web.forummanagementsystem.exceptions.UnauthorizedOperationException;
import com.company.web.forummanagementsystem.models.Comment;
import com.company.web.forummanagementsystem.models.User;
import com.company.web.forummanagementsystem.repositories.CommentRepository;
import com.company.web.forummanagementsystem.repositories.PostRepository;
import com.company.web.forummanagementsystem.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CommentServicesImpl implements CommentServices {

    private static final String UNAUTHORIZED_MESSAGE = "Only the user that created the post or an admin can update/delete a post.";
    private static final String UNAUTHORIZED_MESSAGE_BLOCKED = "User is blocked";
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
    public Comment create(Comment comment, User user) {
        checkAuthorizedPermissions(comment, user);
        return commentRepository.create(comment);
    }

    private void checkAuthorizedPermissions(Comment comment, User user) {
        if (user.isBlocked()) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_MESSAGE_BLOCKED);
        }

        if (!Objects.equals(comment.getUserId(), user.getId()) && !user.isAdmin()) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_MESSAGE);
        }
    }

    @Override
    public Comment update(Comment comment, User user) {
        Comment newComment = commentRepository.getById(comment.getId());
        comment.setUserId(newComment.getUserId());
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
