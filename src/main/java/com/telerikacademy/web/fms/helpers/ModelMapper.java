package com.company.web.forummanagementsystem.helpers;

import com.company.web.forummanagementsystem.models.*;
import com.company.web.forummanagementsystem.models.dto.*;
import com.company.web.forummanagementsystem.service.contracts.CommentServices;
import com.company.web.forummanagementsystem.service.contracts.PostServices;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.company.web.forummanagementsystem.helpers.DateTimeFormat.formatToString;

@Component
public class ModelMapper {

    private final PostServices postServices;
    private final CommentServices commentServices;

    public ModelMapper(PostServices postServices, CommentServices commentServices) {
        this.postServices = postServices;
        this.commentServices = commentServices;
    }

    public User dtoToObject(Long id, UserDTO userDTO) {
        User user = dtoToObject(userDTO);
        user.setId(id);
        return user;
    }

    public User dtoToObject(UserDTO userDTO) {
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setPhoneNumber(userDTO.getPhoneNumber().orElse(null));
        user.setPhoto(userDTO.getPhoto().orElse(null));
        return user;
    }

    public Permission dtoToObject(Long id, PermissionDTO permissionDTO) {
        Permission permission = dtoToObject(permissionDTO);
        permission.setUser_id(id);
        return permission;
    }

    public Permission dtoToObject(PermissionDTO permissionDTO) {
        Permission permission = new Permission();
        permission.setAdmin(permissionDTO.isAdmin());
        permission.setBlocked(permissionDTO.isBlocked());
        return permission;
    }

    public Post dtoToObject(Long id, PostDTO postDTO) {
        Post postFromRepo = postServices.getById(id);
        postFromRepo.setTitle(postDTO.getTitle());
        postFromRepo.setContent(postDTO.getContent());

        return postFromRepo;
    }

    public Post dtoToObject(PostDTO postDTO) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());

        return post;
    }

    public Comment dtoToObject(Long id, CommentDTO commentDTO) {
        Comment comment = commentServices.getById(id);
        comment.setContent(commentDTO.getContent());
        return comment;
    }

    public Comment dtoToObject(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setPostedOn(postServices.getById(commentDTO.getPostId()));
        return comment;
    }

    public List<PostOutputDTO> dtoToObject(List<Post> allPosts) {
        return allPosts.stream().map(this::dtoToObject).collect(Collectors.toList());
    }
    public PostOutputDTO dtoToObject(Post post) {
        PostOutputDTO postOutputDTO = new PostOutputDTO();
        postOutputDTO.setTitle(post.getTitle());
        postOutputDTO.setContent(post.getContent());
        postOutputDTO.setLikes(post.getLikes().size());
        postOutputDTO.setTags(
                post.getTags().stream().map(PostTagRelation::getPostTagId).map(TagId::getTag)
                        .map(Tag::getName).collect(Collectors.toList())
        );
        postOutputDTO.setUserCreated(post.getUserCreated().getUsername());
        postOutputDTO.setDateCreated(formatToString(post.getDateCreated()));

        return postOutputDTO;
    }
}