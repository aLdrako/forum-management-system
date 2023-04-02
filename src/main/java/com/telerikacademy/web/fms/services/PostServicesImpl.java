package com.telerikacademy.web.fms.services;

import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.repositories.contracts.PostRepository;
import com.telerikacademy.web.fms.repositories.contracts.UserRepository;
import com.telerikacademy.web.fms.services.contracts.PostServices;
import com.telerikacademy.web.fms.services.contracts.TagServices;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class PostServicesImpl implements PostServices {
    private static final String UNAUTHORIZED_MESSAGE = "Only the user that created the post or an admin can update/delete a post.";
    private static final String UNAUTHORIZED_MESSAGE_BLOCKED = "User is blocked and cannot create/update/delete posts!";
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagServices tagServices;

    public PostServicesImpl(PostRepository postRepository, UserRepository userRepository,
                            TagServices tagServices) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.tagServices = tagServices;
    }

    @Override
    public Post getById(Long id) {
        return postRepository.getById(id);
    }

    @Override
    public List<Post> getAll(Map<String, String> parameters) {
        if (parameters.containsKey("userId")) userRepository.getById(Long.valueOf(parameters.get("userId")));
        return postRepository.getAll(parameters);
    }

    @Override
    public Post create(Post post, User user) {
        checkAuthorizedPermissions(post, user);
        return postRepository.create(post);
    }

    @Override
    public void update(Post post, User user) {
        checkAuthorizedPermissions(post, user);
        postRepository.update(post);
    }

    private void checkAuthorizedPermissions(Post post, User user) {
        if (user.getPermission().isBlocked()) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_MESSAGE_BLOCKED);
        }

        if (!Objects.equals(post.getUserCreated(), user) && !user.getPermission().isAdmin()) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_MESSAGE);
        }
    }

    @Override
    public void delete(Long id, User user) {
        Post post = postRepository.getById(id);
        checkAuthorizedPermissions(post, user);
        postRepository.delete(post);
    }

    @Override
    public Post getPostByUserId(Long userId, Long postId) {
        userRepository.getById(userId);
        return postRepository.getPostByUserId(userId, postId);
    }

    @Override
    public void changePostLikes(Long id, User user) {
        Post post = postRepository.getById(id);
        if (user.getPermission().isBlocked()) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_MESSAGE_BLOCKED);
        }
        if (post.getLikes().contains(user)) {
            post.removeLike(user);
        } else {
            post.addLike(user);
        }
        postRepository.update(post);
    }

    @Override
    public void updateTagsInPost(List<String> tags, Post post) {
        post.clearTags();
        tags.stream().map(tagServices::createTag).forEach(post::addTag);
        postRepository.update(post);
    }

    @Override
    public List<Post> search(Optional<String> keyword) {
        return postRepository.search(keyword);
    }

    @Override
    public List<Post> getMostCommented() {
        return postRepository.getTopTenMostCommented();
    }

    @Override
    public List<Post> getMostRecent() {
        return postRepository.getTopTenMostRecent();
    }

    @Override
    public Page<Post> findAll(List<Post> allPosts, Pageable pageable, Map<String, String> parameters) {
        return postRepository.findAll(allPosts, pageable, parameters);
    }
}
