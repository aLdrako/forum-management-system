package com.telerikacademy.web.fms.services;

import com.telerikacademy.web.fms.models.*;
import com.telerikacademy.web.fms.models.dto.*;
import com.telerikacademy.web.fms.services.contracts.CommentServices;
import com.telerikacademy.web.fms.services.contracts.PostServices;
import com.telerikacademy.web.fms.services.contracts.UserServices;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ModelMapper {

    private final UserServices userServices;
    private final PostServices postServices;
    private final CommentServices commentServices;
    private final BCryptPasswordEncoder passwordEncoder;

    public ModelMapper(UserServices userServices, PostServices postServices, CommentServices commentServices, BCryptPasswordEncoder passwordEncoder) {
        this.userServices = userServices;
        this.postServices = postServices;
        this.commentServices = commentServices;
        this.passwordEncoder = passwordEncoder;
    }

    public User dtoToObject(Long id, UserDTO userDTO) {
        User user = userServices.getById(id);
        if (userDTO.getFirstName() != null) user.setFirstName(userDTO.getFirstName());
        if (userDTO.getLastName() != null) user.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
        if (userDTO.getFirstName() != null) user.setFirstName(userDTO.getFirstName());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank())
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        if (userDTO.getPhoneNumber() != null && !userDTO.getPhoneNumber().isBlank())
            user.setPhoneNumber(userDTO.getPhoneNumber());
        if (userDTO.getPhoto() != null) user.setPhoto(userDTO.getPhoto());
        user.getPermission().setAdmin(userDTO.isAdmin());
        user.getPermission().setBlocked(userDTO.isBlocked());
        return user;
    }

    public User dtoToObject(UserDTO userDTO) {
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        if (userDTO.getPhoneNumber() != null && !userDTO.getPhoneNumber().isBlank())
            user.setPhoneNumber(userDTO.getPhoneNumber());
        if (userDTO.getPhoto() != null) user.setPhoto(userDTO.getPhoto());
        return user;
    }

    public Permission dtoToObject(Long id, PermissionDTO permissionDTO) {
        Permission permission = dtoToObject(permissionDTO);
        permission.setUserId(id);
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

    public CommentOutputDTO objectToDto(Comment comment) {
        CommentOutputDTO commentOutputDTO = new CommentOutputDTO();
        commentOutputDTO.setContent(comment.getContent());
        commentOutputDTO.setCreatedBy(comment.getCreatedBy().getUsername());
        commentOutputDTO.setPostedOn(comment.getPostedOn().getTitle());
        commentOutputDTO.setDateCreated(comment.getDateCreated());
        return commentOutputDTO;
    }

    public List<PostOutputDTO> objectToDto(List<Post> allPosts) {
        return allPosts.stream().map(this::objectToDto).collect(Collectors.toList());
    }

    public PostOutputDTO objectToDto(Post post) {
        PostOutputDTO postOutputDTO = new PostOutputDTO();
        postOutputDTO.setTitle(post.getTitle());
        postOutputDTO.setContent(post.getContent());
        postOutputDTO.setLikes(post.getLikes().size());
        postOutputDTO.setTags(
                post.getTags().stream().map(Tag::getName).collect(Collectors.toList())
        );
        postOutputDTO.setUserCreated(post.getUserCreated().getUsername());
        postOutputDTO.setDateCreated(post.getDatecreated());

        return postOutputDTO;
    }

    public UserDTO objectToDto(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setPhoto(user.getPhoto());
        userDTO.setAdmin(user.getPermission().isAdmin());
        userDTO.setBlocked(user.getPermission().isBlocked());
        return userDTO;
    }

    public CommentDTO toDto(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent(comment.getContent());
        commentDTO.setPostId(comment.getPostedOn().getId());
        return commentDTO;
    }

    public PostDTO toDto(Post post) {
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle(post.getTitle());
        postDTO.setContent(post.getContent());
        postDTO.setTags(post.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
        return postDTO;
    }

    public Map<String, String> dtoToMap(FilterPostsDto filterDto) {
        Map<String, String> map = new HashMap<>();

        Optional.ofNullable(filterDto.getTitle()).ifPresent(v -> map.put("title", filterDto.getTitle()));
        Optional.ofNullable(filterDto.getContent()).ifPresent(v -> map.put("content", filterDto.getContent()));
        Optional.ofNullable(filterDto.getTag()).ifPresent(v -> map.put("tag", filterDto.getTag()));
        Optional.ofNullable(filterDto.getTag()).ifPresent(v -> map.put("username", filterDto.getUsername()));
        Optional.ofNullable(filterDto.getSort()).ifPresent(v -> map.put("sort", filterDto.getSort()));
        Optional.ofNullable(filterDto.getOrder()).ifPresent(v -> map.put("order", filterDto.getOrder()));

        return map;
    }

}
