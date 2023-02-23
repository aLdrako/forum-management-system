package com.company.web.forummanagementsystem.service;

import com.company.web.forummanagementsystem.models.Post;

import java.util.List;

public interface PostServices {
    List<Post> getAll();
    Post getById(Long id);
    List<Post> searchByTitle(String title);
    Post create(Post post);
    Post update(Post post);
    void delete(Long id);
    List<Post> getPostsByUserId(Long userId);
    Post getPostByUserId(Long userId, Long postId);
}
