package com.telerikacademy.web.fms.repositories.contracts;

import com.telerikacademy.web.fms.models.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Post getById(Long id);
    List<Post> getAll(Optional<Long> userId, Optional<String> title,
                      Optional<String> sortBy, Optional<String> orderBy);
    Post create(Post post);
    Post update(Post post);
    void delete(Post post);
    List<Post> getTopTenMostCommented();
    List<Post> getTopTenMostRecent();
    Post getPostByUserId(Long userId, Long postId);
}
