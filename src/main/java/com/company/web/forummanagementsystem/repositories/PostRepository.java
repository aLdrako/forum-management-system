package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.models.Post;

import java.util.List;

public interface PostRepository {
    List<Post> getAll();
    Post getById(Long id);
    Post searchByTitle(String title);
    void create(Post post);
    void update(Post post);
    void delete(Long id);
    List<Post> getPostsByUserId(Long userId);
    Post getPostByUserId(Long userId, Long postId);
}
