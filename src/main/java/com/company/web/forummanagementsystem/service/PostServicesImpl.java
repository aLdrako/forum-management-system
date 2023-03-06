package com.company.web.forummanagementsystem.service;

import com.company.web.forummanagementsystem.exceptions.UnauthorizedOperationException;
import com.company.web.forummanagementsystem.models.Like;
import com.company.web.forummanagementsystem.models.LikeId;
import com.company.web.forummanagementsystem.models.Post;
import com.company.web.forummanagementsystem.models.User;
import com.company.web.forummanagementsystem.repositories.LikeRepository;
import com.company.web.forummanagementsystem.repositories.PostRepository;
import com.company.web.forummanagementsystem.repositories.UserRepository;
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

    public PostServicesImpl(PostRepository postRepository, UserRepository userRepository,
                            LikeRepository likeRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
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

}
