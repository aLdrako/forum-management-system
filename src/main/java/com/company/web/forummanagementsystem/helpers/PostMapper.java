package com.company.web.forummanagementsystem.helpers;

import com.company.web.forummanagementsystem.models.Post;
import com.company.web.forummanagementsystem.models.PostDTO;
import com.company.web.forummanagementsystem.service.UserServices;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {
    private final UserServices userServices;

    public PostMapper(UserServices userServices) {
        this.userServices = userServices;
    }

    public Post dtoToObject(Long id, PostDTO postDTO) {
        Post post = new Post();
        post.setId(id);
        return dtoToObject(post, postDTO);
    }

    public Post dtoToObject(Post post, PostDTO postDTO) {
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setUserId(postDTO.getUserId());
        return post;
    }
}
