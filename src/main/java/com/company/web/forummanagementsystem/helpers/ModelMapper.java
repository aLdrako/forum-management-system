package com.company.web.forummanagementsystem.helpers;

import com.company.web.forummanagementsystem.models.*;
import com.company.web.forummanagementsystem.service.PostServices;
import com.company.web.forummanagementsystem.service.UserServices;
import org.springframework.stereotype.Component;

@Component
public class ModelMapper {

    private final UserServices userServices;
    private final PostServices postServices;

    public ModelMapper(UserServices userServices, PostServices postServices) {
        this.userServices = userServices;
        this.postServices = postServices;
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
        post.setLikes(0);

        return post;
    }

    public Comment dtoToObject(Long id, CommentDTO commentDTO) {
        Comment comment = dtoToObject(commentDTO);
        comment.setId(id);
        return comment;
    }

    public Comment dtoToObject(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setCreatedBy(userServices.getById(commentDTO.getUserId()));
        comment.setPostedOn(postServices.getById(commentDTO.getPostId()));
        return comment;
    }
}
