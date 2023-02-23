package com.company.web.forummanagementsystem.service;

import com.company.web.forummanagementsystem.models.Post;
import com.company.web.forummanagementsystem.repositories.PostRepository;
import com.company.web.forummanagementsystem.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServicesImpl implements PostServices {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostServicesImpl(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
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
    public List<Post> searchByTitle(String title) {
        return postRepository.searchByTitle(title);
    }

    @Override
    public Post create(Post post) {
        return postRepository.create(post);
    }

    @Override
    public Post update(Post post) {
        return postRepository.update(post);
    }

    @Override
    public void delete(Long id) {
        postRepository.delete(id);
    }

    @Override
    public List<Post> getPostsByUserId(Long userId) {
        userRepository.getById(userId);
        return postRepository.getPostsByUserId(userId);
    }

    @Override
    public Post getPostByUserId(Long userId, Long postId) {
        userRepository.getById(userId);
        return postRepository.getPostByUserId(userId, postId);
    }
}
