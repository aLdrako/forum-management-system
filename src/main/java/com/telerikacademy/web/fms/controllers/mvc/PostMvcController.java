package com.telerikacademy.web.fms.controllers.mvc;

import com.telerikacademy.web.fms.exceptions.AuthorizationException;
import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.helpers.AuthenticationHelper;
import com.telerikacademy.web.fms.models.Comment;
import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.models.dto.CommentDTO;
import com.telerikacademy.web.fms.models.dto.FilterPostsDto;
import com.telerikacademy.web.fms.models.dto.PostDTO;
import com.telerikacademy.web.fms.models.validations.CreateValidationGroup;
import com.telerikacademy.web.fms.services.ModelMapper;
import com.telerikacademy.web.fms.services.contracts.CommentServices;
import com.telerikacademy.web.fms.services.contracts.PostServices;
import com.telerikacademy.web.fms.services.contracts.UserServices;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/posts")
public class PostMvcController extends BaseMvcController {
    private final PostServices postServices;
    private final UserServices userServices;
    private final CommentServices commentServices;
    private final ModelMapper modelMapper;
    private final AuthenticationHelper authenticationHelper;

    public PostMvcController(PostServices postServices, UserServices userServices, CommentServices commentServices, ModelMapper modelMapper,
                             AuthenticationHelper authenticationHelper) {
        this.postServices = postServices;
        this.userServices = userServices;
        this.commentServices = commentServices;
        this.modelMapper = modelMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @ModelAttribute("users")
    public List<User> populateUsers() {
        return userServices.getAll();
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) Map<String, String> parameters,
                         Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetCurrentUser(session);
            model.addAttribute("search", parameters.getOrDefault("q", ""));
            List<Post> allPosts = parameters.get("q").isEmpty() ?
                    List.of() : postServices.search(parameters.get("q").describeConstable());
            model.addAttribute("posts", allPosts);

            int page = Integer.parseInt(parameters.getOrDefault("page", "0"));
            int size = Integer.parseInt(parameters.getOrDefault("size", "8"));
            Pageable pageable = PageRequest.of(page, size);

            Page<Post> postPage = postServices.findAll(allPosts, pageable, parameters);
            model.addAttribute("currentPage", page);
            model.addAttribute("sizePage", size);
            model.addAttribute("totalPages", postPage.getTotalPages());
            model.addAttribute("q", parameters.getOrDefault("q", ""));
            model.addAttribute("posts", postPage.getContent());

            return "PostsResultSearch";
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "AccessDeniedView";
        }
    }

    @GetMapping("/new")
    public String showNewPostPage(Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
        model.addAttribute("post", new PostDTO());
        return "PostCreateView";
    }

    @PostMapping("/new")
    public String createPost(@Valid @ModelAttribute("post") PostDTO postDTO,
                             BindingResult bindingResult, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
        if (bindingResult.hasErrors()) {
            return "PostCreateView";
        }
        try {
            Post post = modelMapper.dtoToObject(postDTO);
            post.setUserCreated(user);
            postServices.create(post, user);
            return "redirect:/posts/" + post.getId();
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "AccessDeniedView";
        }
    }

    @GetMapping("/{id}/update")
    public String showUpdatePostPage(@PathVariable Long id, Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
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
    public String updatePost(@PathVariable Long id, @Valid @ModelAttribute("post") PostDTO postDto,
                             BindingResult bindingResult, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
        if (bindingResult.hasErrors()) {
            return "PostUpdateView";
        }
        try {
            Post newPost = modelMapper.dtoToObject(id, postDto);
            postServices.update(newPost, user);
            postServices.updateTagsInPost(postDto.getTags(), newPost);
            return "redirect:/posts/" + newPost.getId();
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "AccessDeniedView";
        }
    }

    @GetMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
        try {
            postServices.delete(id, user);
            return "redirect:/posts";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "AccessDeniedView";
        }
    }

    @GetMapping("/{id}/like")
    public String changeLikes(@PathVariable Long id, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
        try {
            postServices.changePostLikes(id, user);
            return "redirect:/posts/" + id;
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "AccessDeniedView";
        }
    }

    @GetMapping
    public String showAllPosts(@RequestParam(required = false) Map<String, String> parameters,
                               @ModelAttribute("filterPostOptions") FilterPostsDto filterDto, Model model,
                               HttpSession session) {
        try {
            authenticationHelper.tryGetCurrentUser(session);
            int page = Integer.parseInt(parameters.getOrDefault("page", "0"));
            int size = Integer.parseInt(parameters.getOrDefault("size", "8"));
            Pageable pageable = PageRequest.of(page, size);
            List<Post> allPosts = postServices.getAll(parameters);

            String sort = parameters.getOrDefault("sort", "dateCreated");
            String order = parameters.getOrDefault("order", "asc");

            Page<Post> postPage = postServices.findAll(allPosts, pageable, parameters);
            model.addAttribute("currentPage", page);
            model.addAttribute("sizePage", size);
            model.addAttribute("totalPages", postPage.getTotalPages());
            model.addAttribute("sort", sort);
            model.addAttribute("order", order);
            model.addAttribute("title", parameters.getOrDefault("title", ""));
            model.addAttribute("content", parameters.getOrDefault("content", ""));
            model.addAttribute("tag", parameters.getOrDefault("tag", ""));
            model.addAttribute("username", parameters.getOrDefault("username", ""));

            model.addAttribute("posts", postPage.getContent());

            return "PostsView";
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "AccessDeniedView";
        }
    }

    @GetMapping("/{id}")
    public String showSinglePost(@PathVariable Long id, @RequestParam(required = false) Map<String, String> parameters, Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetCurrentUser(session);
            Post post = postServices.getById(id);
            List<Comment> commentsByPostId = commentServices.getCommentsByPostId(id, parameters);
            model.addAttribute("comment", new CommentDTO());
            model.addAttribute("comments", commentsByPostId);
            model.addAttribute("post", post);
            return "PostView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
    }

    @PostMapping("{id}/reply")
    public String createComment(@PathVariable Long id, @Validated(CreateValidationGroup.class) @ModelAttribute("comment") CommentDTO commentDTO,
                                BindingResult bindingResult,
                                Model model,
                                HttpSession session) {

        if (bindingResult.hasErrors()) return "CommentCreateView";

        try {
            User currentUser = authenticationHelper.tryGetCurrentUser(session);
            commentDTO.setPostId(id);
            Comment comment = modelMapper.dtoToObject(commentDTO);
            comment.setCreatedBy(currentUser);
            commentServices.create(comment, currentUser);
            return "redirect:/posts/" + id;
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "AccessDeniedView";
        }
    }
}
