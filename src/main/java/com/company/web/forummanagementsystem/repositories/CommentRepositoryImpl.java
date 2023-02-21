package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.models.Comment;
import com.company.web.forummanagementsystem.models.Post;
import com.company.web.forummanagementsystem.models.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class CommentRepositoryImpl implements CommentRepository {
    private final List<Comment> comments;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentRepositoryImpl(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        List<User> users = this.userRepository.getAll();
        List<Post> posts = this.postRepository.getAll();
        comments = new ArrayList<>();

        AtomicLong counter = new AtomicLong();
        comments.add(new Comment(counter.incrementAndGet(), "This is my first comment", posts.get(0).getId(), users.get(1).getId()));
        comments.add(new Comment(counter.incrementAndGet(), "Great idea, thanks", posts.get(1).getId(), users.get(1).getId()));
        comments.add(new Comment(counter.incrementAndGet(), "Some random comment here", posts.get(1).getId(), users.get(2).getId()));
    }

    @Override
    public List<Comment> getAll() {
        return new ArrayList<>(comments);
    }

    @Override
    public Comment getById(Long id) {
        return comments.stream()
                .filter(comment -> Objects.equals(comment.getId(), id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Comment", id));
    }

    @Override
    public void create(Comment comment) {
        long currentId = !comments.isEmpty() ? comments.get(comments.size() - 1).getId() + 1 : 1L;
        comment.setId(currentId);
        userRepository.getById(comment.getUserId());
        postRepository.getById(comment.getPostId());
        comments.add(comment);
    }

    @Override
    public void update(Comment comment) {
        Comment commentToUpdate = getById(comment.getId());
        userRepository.getById(comment.getUserId());
        postRepository.getById(comment.getPostId());
        commentToUpdate.setContent(comment.getContent());
        commentToUpdate.setPostId(comment.getPostId());
        commentToUpdate.setUserId(comment.getUserId());
    }

    @Override
    public void delete(Long id) {
        Comment commentToDelete = getById(id);
        comments.remove(commentToDelete);
    }

    @Override
    public List<Comment> getCommentsByUserId(Long userId) {
        userRepository.getById(userId);
        return comments.stream()
                .filter(comment -> Objects.equals(comment.getUserId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public Comment getCommentByUserId(Long userId, Long commentId) {
        userRepository.getById(userId);
        getById(commentId);
        return comments.stream()
                .filter(comment -> Objects.equals(comment.getUserId(), userId))
                .filter(comment -> Objects.equals(comment.getId(), commentId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d does not have comment with id %d!", userId, commentId)));
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        postRepository.getById(postId);
        return comments.stream()
                .filter(comment -> Objects.equals(comment.getPostId(), postId))
                .collect(Collectors.toList());
    }

    @Override
    public Comment getCommentByPostId(Long postId, Long commentId) {
        postRepository.getById(postId);
        getById(commentId);
        return comments.stream()
                .filter(comment -> Objects.equals(comment.getPostId(), postId))
                .filter(comment -> Objects.equals(comment.getId(), commentId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post with id %d does not have comment with id %d!", postId, commentId)));
    }
}
