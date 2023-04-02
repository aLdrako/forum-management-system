package com.telerikacademy.web.fms.repositories.contracts;

import com.telerikacademy.web.fms.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    Page<Post> findAll(List<Post> allPosts, Pageable pageable, Map<String, String> parameters);
}
