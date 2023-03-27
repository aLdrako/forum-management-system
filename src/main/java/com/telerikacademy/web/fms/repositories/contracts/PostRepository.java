package com.telerikacademy.web.fms.repositories.contracts;

import com.telerikacademy.web.fms.models.Post;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PostRepository {
    Post getById(Long id);
    List<Post> getAll(Map<String, String> parameters);
    Post create(Post post);
    void update(Post post);
    void delete(Post post);
    List<Post> getTopTenMostCommented();
    List<Post> getTopTenMostRecent();
    Post getPostByUserId(Long userId, Long postId);

    List<Post> search(Optional<String> keyword);
}
