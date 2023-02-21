package com.company.web.forummanagementsystem.service;

import com.company.web.forummanagementsystem.models.Post;
import com.company.web.forummanagementsystem.repositories.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServicesImpl implements PostServices {
    private final PostRepository postRepository;

    public PostServicesImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<Post> getAll() {
        return postRepository.getAll();
    }

    @Override
    public Post getById(Long id) {
        return postRepository.getById(id);
    }

    @Override
    public Post searchByTitle(String title) {
        return postRepository.searchByTitle(title);
    }

    @Override
    public void create(Post post) {
        postRepository.create(post);
    }

    @Override
    public void update(Post post) {
        postRepository.update(post);
    }

    @Override
    public void delete(Long id) {
        postRepository.delete(id);
    }

    @Override
    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.getPostsByUserId(userId);
    }

    @Override
    public Post getPostByUserId(Long userId, Long postId) {
        return postRepository.getPostByUserId(userId, postId);
    }
}
