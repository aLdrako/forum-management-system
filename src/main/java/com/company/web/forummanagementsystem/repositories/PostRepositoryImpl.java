package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.models.Post;
import com.company.web.forummanagementsystem.models.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

//@Repository
public class PostRepositoryImpl implements PostRepository {
    private final List<Post> posts;
    private final UserRepository userRepository;

    public PostRepositoryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        List<User> users = this.userRepository.getAll();
        posts = new ArrayList<>();

        AtomicLong counter = new AtomicLong();
        posts.add(new Post(counter.incrementAndGet(), "Old times", "Once upon a time there was a...", 13, users.get(0).getId()));
        posts.add(new Post(counter.incrementAndGet(), "Ice and Fire", "They were destroying everything on their path...", 3, users.get(2).getId()));
        posts.add(new Post(counter.incrementAndGet(), "Self observation", "This practice requires your full attention to details", 20, users.get(2).getId()));
    }

    @Override
    public List<Post> getAll() {
        return new ArrayList<>(posts);
    }

    @Override
    public Post getById(Long id) {
        return posts.stream()
                .filter(post -> Objects.equals(post.getId(), id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Post", id));
    }

    @Override
    public List<Post> searchByTitle(String title) {
        return posts.stream()
                .filter(post -> post.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Post create(Post post) {
        long currentId = !posts.isEmpty() ? posts.get(posts.size() - 1).getId() : 1L;
        post.setId(currentId + 1);
        userRepository.getById(post.getUserId());
        posts.add(post);
        return post;
    }

    @Override
    public void update(Post post) {
        Post postToUpdate = getById(post.getId());
        userRepository.getById(post.getUserId());
        postToUpdate.setTitle(post.getTitle());
        postToUpdate.setContent(post.getContent());
        postToUpdate.setLikes(post.getLikes());
        postToUpdate.setUserId(post.getUserId());
    }

    @Override
    public void delete(Long id) {
        Post postToDelete = getById(id);
        posts.remove(postToDelete);
    }

    @Override
    public List<Post> getPostsByUserId(Long userId) {
        userRepository.getById(userId);
        return posts.stream()
                .filter(post -> Objects.equals(post.getUserId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public Post getPostByUserId(Long userId, Long postId) {
        userRepository.getById(userId);
        getById(postId);
        return posts.stream()
                .filter(post -> Objects.equals(post.getUserId(), userId))
                .filter(post -> Objects.equals(post.getId(), postId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d does not have post with id %d!", userId, postId)));
    }

}
