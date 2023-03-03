package com.company.web.forummanagementsystem.helpers;

import com.company.web.forummanagementsystem.models.Post;
import com.company.web.forummanagementsystem.models.PostDTO;
import com.company.web.forummanagementsystem.models.User;
import com.company.web.forummanagementsystem.service.PostServices;
import com.company.web.forummanagementsystem.service.UserServices;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {
    private final UserServices userServices;
    private final PostServices postServices;

    public PostMapper(UserServices userServices, PostServices postServices) {
        this.userServices = userServices;
        this.postServices = postServices;
    }

    public Post dtoToObject(Long id, PostDTO postDTO) {
        Post post = dtoToObject(postDTO);
        Post postFromRepo = postServices.getById(id);
        post.setId(id);
        post.setUserCreated(postFromRepo.getUserCreated());
        return post;
    }

    public Post dtoToObject(PostDTO postDTO) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());

        return post;
    }
}
