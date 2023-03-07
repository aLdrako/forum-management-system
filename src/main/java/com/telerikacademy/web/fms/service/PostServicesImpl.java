package com.telerikacademy.web.fms.service;

import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.models.*;
import com.telerikacademy.web.fms.repositories.contracts.LikeRepository;
import com.telerikacademy.web.fms.repositories.contracts.PostRepository;
import com.telerikacademy.web.fms.repositories.contracts.PostTagRelationRepository;
import com.telerikacademy.web.fms.repositories.contracts.UserRepository;
import com.telerikacademy.web.fms.service.contracts.PostServices;
import com.telerikacademy.web.fms.service.contracts.TagServices;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PostServicesImpl implements PostServices {
    private static final String UNAUTHORIZED_MESSAGE = "Only the user that created the post or an admin can update/delete a post.";
    private static final String UNAUTHORIZED_MESSAGE_BLOCKED = "User is blocked";
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final TagServices tagServices;
    private final PostTagRelationRepository postTagRelationRepository;

    public PostServicesImpl(PostRepository postRepository, UserRepository userRepository,
                            LikeRepository likeRepository, TagServices tagServices,
                            PostTagRelationRepository postTagRelationRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.tagServices = tagServices;
        this.postTagRelationRepository = postTagRelationRepository;
    }

    @Override
    public Post getById(Long id) {
        return postRepository.getById(id);
    }

    @Override
    public List<Post> getAll(Optional<Long> userId, Optional<String> title,
                             Optional<String> sortBy, Optional<String> orderBy) {
        userId.ifPresent(postRepository::getById);
        return postRepository.getAll(userId, title, sortBy, orderBy);
    }

    @Override
    public Post create(Post post, User user) {
        checkAuthorizedPermissions(post, user);
        return postRepository.create(post);
    }

    @Override
    public Post update(Post post, User user) {
        checkAuthorizedPermissions(post, user);
        return postRepository.update(post);
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
        postRepository.delete(id);
    }


    @Override
    public Post getPostByUserId(Long userId, Long postId) {
        userRepository.getById(userId);
        return postRepository.getPostByUserId(userId, postId);
    }

    @Override
    public void changePostLikes(Long id, User user) {
        postRepository.getById(id);
        if (user.getPermission().isBlocked()) {
            throw new UnauthorizedOperationException("User is blocked!");
        }
        Like like = likeRepository.getById(id, user.getId());

        if (like == null) {
            Like newLike = new Like(new LikeId(id, user.getId()));
            postRepository.addLikeToPost(newLike);
        } else {
            postRepository.removeLikeFromPost(like);
        }
    }

    @Override
    public Post addTagsToPost(List<String> tags, Post post) {
        if (tags == null) {
            return post;
        }

        for (String tagsName:tags) {
            Tag tag = tagServices.createTag(tagsName);
            PostTagRelation postTagRelation = postTagRelationRepository.getRelationById(post.getId(),
                    tag.getId());
            if (postTagRelation == null) {
                postTagRelationRepository.createRelation(new PostTagRelation(new TagId(post.getId(), tag)));
            } else {
                postTagRelationRepository.deleteRelation(postTagRelation);
            }
        }
        return getById(post.getId());
    }


}
