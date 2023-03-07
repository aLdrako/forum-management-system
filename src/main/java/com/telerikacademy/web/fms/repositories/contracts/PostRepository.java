package com.telerikacademy.web.fms.repositories.contracts;

import com.telerikacademy.web.fms.models.Like;
import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.models.User;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Post getById(Long id);
    List<Post> getAll(Optional<Long> userId, Optional<String> title,
                      Optional<String> sortBy, Optional<String> orderBy);
    Post create(Post post);
    Post update(Post post);
    void delete(Long id);
    List<Post> getTopTenMostCommented();
    List<Post> getTopTenMostRecent();
    Post getPostByUserId(Long userId, Long postId);

    void addLikeToPost(Like like);
    void removeLikeFromPost(Like like);
}
