package com.telerikacademy.web.fms.controllers.mvc;

import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.models.dto.PostDTO;
import com.telerikacademy.web.fms.services.ModelMapper;
import com.telerikacademy.web.fms.services.contracts.PostServices;
import com.telerikacademy.web.fms.services.contracts.UserServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.telerikacademy.web.fms.helpers.DateTimeFormatHelper.dateTimeFormatter;

@Controller
@RequestMapping("/posts")
public class PostMvcController {
    private final PostServices postServices;
    private final UserServices userServices;
    private final ModelMapper modelMapper;

    public PostMvcController(PostServices postServices, UserServices userServices, ModelMapper modelMapper) {
        this.postServices = postServices;
        this.userServices = userServices;
        this.modelMapper = modelMapper;
    }
    @ModelAttribute("users")
    public List<User> populateUsers() {
        return userServices.getAll();
    }

    @ModelAttribute("formatter")
    public DateTimeFormatter formatter() {
        return dateTimeFormatter;
    }

    @GetMapping("/new")
    public String showNewPostPage(Model model) {
        model.addAttribute("post", new PostDTO());
        return "PostCreateView";
    }
    @PostMapping("/new")
    public String createPost(@Valid @ModelAttribute("post") PostDTO postDTO,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "PostCreateView";
        }
        try {
            User user = userServices.getById(1L);
            Post post = modelMapper.dtoToObject(postDTO);
            post.setUserCreated(user);
            postServices.create(post, user);
            return "redirect:/posts/" + post.getId();
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }

    @GetMapping("/{id}/update")
    public String showUpdatePostPage(@PathVariable Long id, Model model) {
        try {
            Post post = postServices.getById(id);
            PostDTO postDTO = modelMapper.toDto(post);
            model.addAttribute("postId", id);
            model.addAttribute("post", postDTO);
            return "PostUpdateView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }
    @PostMapping("{id}/update")
    public String updatePost(@PathVariable Long id, @Valid @ModelAttribute PostDTO post,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "PostUpdateView";
        }
        try {
            User user = userServices.getById(1L);
            Post newPost = modelMapper.dtoToObject(id, post);
            postServices.update(newPost, user);
            return "redirect:/posts";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }
    @GetMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id, Model model) {
        try {
            User user = userServices.getById(1L);
            postServices.delete(id, user);
            return "redirect:/posts";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }
    @GetMapping("/{id}/like")
    public String changeLikes(@PathVariable Long id, Model model) {
        try {
            User user = userServices.getById(1L);
            postServices.changePostLikes(id, user);
            return "redirect:/posts/" + id;
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }
    @GetMapping
    public String showAllPosts(Model model) {
        model.addAttribute("posts", postServices.getAll(Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty()));
        return "PostsView";
    }
    @GetMapping("/{id}")
    public String showSinglePost(@PathVariable Long id, Model model) {
        try {
            Post post = postServices.getById(id);
            model.addAttribute("post", post);
            return "PostViewNew";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }
}
