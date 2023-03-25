package com.telerikacademy.web.fms.services;

import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.models.*;
import com.telerikacademy.web.fms.repositories.contracts.PostRepository;
import com.telerikacademy.web.fms.repositories.contracts.UserRepository;
import com.telerikacademy.web.fms.services.contracts.PostServices;
import com.telerikacademy.web.fms.services.contracts.TagServices;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostServicesImpl implements PostServices {
    private static final String UNAUTHORIZED_MESSAGE = "Only the user that created the post or an admin can update/delete a post.";
    private static final String UNAUTHORIZED_MESSAGE_BLOCKED = "User is blocked";
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
    public List<Post> getAll(Optional<Long> userId, Optional<String> title, Optional<String> content,
                             Optional<String> tag,
                             Optional<String> sort, Optional<String> order) {
        userId.ifPresent(userRepository::getById);
        return postRepository.getAll(userId, title, content, tag, sort, order);
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
}
