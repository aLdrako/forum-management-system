package com.company.web.forummanagementsystem.controllers;

import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.helpers.PostMapper;
import com.company.web.forummanagementsystem.models.Post;
import com.company.web.forummanagementsystem.models.PostDTO;
import com.company.web.forummanagementsystem.service.PostServices;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PostController {
    private final PostServices postServices;
    private final PostMapper postMapper;

    public PostController(PostServices postServices, PostMapper postMapper) {
        this.postServices = postServices;
        this.postMapper = postMapper;
    }

    @GetMapping("/posts")
    public List<Post> getAll() {
        return postServices.getAll();
    }

    @GetMapping("/posts/{id}")
    public Post getById(@PathVariable Long id) {
        return postServices.getById(id);
    }

    @PostMapping("/posts")
    public Post create(@Valid @RequestBody PostDTO postDTO) {
        try {
            Post post = postMapper.dtoToObject(new Post(), postDTO);
            return postServices.create(post);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    @GetMapping("/posts/search")
    public List<Post> searchByTitle(@RequestParam String title) {
        return postServices.searchByTitle(title);
    }

    @PutMapping("/posts/{id}")
    public Post update(@PathVariable Long id, @Valid @RequestBody PostDTO postDTO) {
        try {
            Post post = postMapper.dtoToObject(id, postDTO);
            return postServices.update(post);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/posts/{id}")
    public void delete(@PathVariable Long id) {
        try {
            postServices.delete(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/users/{userId}/posts")
    public List<Post> getPostsByUserId(@PathVariable Long userId) {
        try {
            return postServices.getPostsByUserId(userId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/users/{userId}/posts/{postId}")
    public Post getPostByUserId(@PathVariable Long userId, @PathVariable Long postId) {
        try {
            return postServices.getPostByUserId(userId, postId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
