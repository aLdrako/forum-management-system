package com.telerikacademy.web.fms.repositories.deprecated;

import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.repositories.contracts.PostRepository;
import com.telerikacademy.web.fms.repositories.contracts.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

//@Repository
public class PostRepositoryListImpl implements PostRepository {
    private final List<Post> posts;
    private final UserRepository userRepository;

    public PostRepositoryListImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        List<User> users = this.userRepository.getAll();
        posts = new ArrayList<>();

        AtomicLong counter = new AtomicLong();
        posts.add(new Post(counter.incrementAndGet(), "Old times", "Once upon a time there was a...", users.get(0)));
        posts.add(new Post(counter.incrementAndGet(), "Ice and Fire", "They were destroying everything on their path...", users.get(2)));
        posts.add(new Post(counter.incrementAndGet(), "Self observation", "This practice requires your full attention to details", users.get(2)));
    }

    @Override
    public Post getById(Long id) {
        return posts.stream()
                .filter(post -> Objects.equals(post.getId(), id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Post", id));
    }

    @Override
    public List<Post> getAll(Map<String, String> parameters) {
        return posts;
    }

    @Override
    public Post create(Post post) {
        long currentId = !posts.isEmpty() ? posts.get(posts.size() - 1).getId() : 1L;
        post.setId(currentId + 1);
        userRepository.getById(post.getUserCreated().getId());
        posts.add(post);
        return post;
    }

    @Override
    public void update(Post post) {
        Post postToUpdate = getById(post.getId());
        userRepository.getById(post.getUserCreated().getId());
        postToUpdate.setTitle(post.getTitle());
        postToUpdate.setContent(post.getContent());
        postToUpdate.setLikes(post.getLikes());
        postToUpdate.setUserCreated(post.getUserCreated());
    }

    @Override
    public void delete(Post post) {
        posts.remove(post);
    }

    @Override
    public List<Post> getTopTenMostCommented() {
        return null;
    }

    @Override
    public List<Post> getTopTenMostRecent() {
        return null;
    }

    @Override
    public Post getPostByUserId(Long userId, Long postId) {
        userRepository.getById(userId);
        getById(postId);
        return posts.stream()
                .filter(post -> Objects.equals(post.getUserCreated().getId(), userId))
                .filter(post -> Objects.equals(post.getId(), postId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d does not have post with id %d!", userId, postId)));
    }
    @Override
    public List<Post> search(Optional<String> keyword) {
        return null;
    }

    @Override
    public Page<Post> findAll(List<Post> allPosts, Pageable pageable, Map<String, String> parameters) {
        return null;
    }

}
