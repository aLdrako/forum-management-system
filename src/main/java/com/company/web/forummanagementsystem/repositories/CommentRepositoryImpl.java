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

    }

    @Override
    public void update(Comment comment) {

    }

    @Override
    public void delete(Comment comment) {

    }
}
