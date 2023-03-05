package com.company.web.forummanagementsystem.helpers;

import com.company.web.forummanagementsystem.models.Post;
import com.company.web.forummanagementsystem.models.PostDTO;
import com.company.web.forummanagementsystem.models.User;
import com.company.web.forummanagementsystem.service.PostServices;
import com.company.web.forummanagementsystem.service.UserServices;
import org.springframework.stereotype.Component;

import static com.company.web.forummanagementsystem.helpers.DateTimeFormat.formatToLocalDateTime;

@Component
public class PostMapper {
    private final UserServices userServices;
    private final PostServices postServices;

    public PostMapper(UserServices userServices, PostServices postServices) {
        this.userServices = userServices;
        this.postServices = postServices;
    }

    public Post dtoToObject(Long id, PostDTO postDTO) {
        //Post post = dtoToObject(postDTO);
        Post postFromRepo = postServices.getById(id);
        //post.setId(id);
        //post.setUserCreated(postFromRepo.getUserCreated());
        //post.setDateCreated(formatToLocalDateTime(postFromRepo.getDateCreated()));
        //post.setLikes(postFromRepo.getLikes());
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
}
