package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.models.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Post getById(Long id);
    List<Post> search(Optional<Long> userId, Optional<String> title,
                      Optional<String> sortBy, Optional<String> orderBy);
    Post create(Post post);
    Post update(Post post);
    void delete(Long id);
    List<Post> getTopTenMostCommented();
    List<Post> getTopTenMostRecent();
    Post getPostByUserId(Long userId, Long postId);
}
