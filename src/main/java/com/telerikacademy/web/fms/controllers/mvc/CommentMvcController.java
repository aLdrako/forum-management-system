package com.telerikacademy.web.fms.controllers.mvc;

import com.telerikacademy.web.fms.exceptions.AuthorizationException;
import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.helpers.AuthenticationHelper;
import com.telerikacademy.web.fms.models.Comment;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.models.dto.CommentDTO;
import com.telerikacademy.web.fms.models.validations.UpdateValidationGroup;
import com.telerikacademy.web.fms.services.ModelMapper;
import com.telerikacademy.web.fms.services.contracts.CommentServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/comments")
public class CommentMvcController extends BaseMvcController {
    private final ModelMapper modelMapper;
    private final CommentServices commentServices;
    private final AuthenticationHelper authenticationHelper;

    public CommentMvcController(ModelMapper modelMapper, CommentServices commentServices, AuthenticationHelper authenticationHelper) {
        this.modelMapper = modelMapper;
        this.commentServices = commentServices;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public String showAllComments(@RequestParam(required = false) Map<String, String> parameters, Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetCurrentAdmin(session);

            int page = Integer.parseInt(parameters.getOrDefault("page", "0"));
            int size = Integer.parseInt(parameters.getOrDefault("size", "8"));
            String sort = parameters.getOrDefault("sort", "dateCreated");
            String order = parameters.getOrDefault("order", "asc");

            Pageable pageable = PageRequest.of(page, size);
            Page<Comment> commentPage = commentServices.findAll(parameters, pageable);

            model.addAttribute("comments", commentPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("sizePage", size);
            model.addAttribute("totalPages", commentPage.getTotalPages());
            model.addAttribute("sort", sort);
            model.addAttribute("order", order);
            return "CommentsView";
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "AccessDeniedView";
        }
    }

    @GetMapping("{id}")
    public String showComment(@PathVariable Long id, Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetCurrentUser(session);
            Comment comment = commentServices.getById(id);
            model.addAttribute("comment", comment);
            return "CommentView";
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }

    @GetMapping("{id}/update")
    public String showUpdateCommentPage(@PathVariable Long id, Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetCurrentUser(session);
            Comment comment = commentServices.getById(id);
            CommentDTO commentDTO = modelMapper.toDto(comment);
            model.addAttribute("comment", commentDTO);
            return "CommentUpdateView";
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }

    @PostMapping("{id}/update")
    public String updateComment(@PathVariable Long id,
                                @Validated(UpdateValidationGroup.class) @ModelAttribute("comment") CommentDTO commentDTO,
                                BindingResult bindingResult,
                                Model model,
                                HttpSession session) {

        if (bindingResult.hasErrors()) return "CommentUpdateView";

        try {
            User currentUser = authenticationHelper.tryGetCurrentUser(session);
            Comment comment = modelMapper.dtoToObject(id, commentDTO);
            commentServices.update(comment, currentUser);
            return "redirect:/comments/" + comment.getId();
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (UnauthorizedOperationException e) {
            bindingResult.rejectValue("content", "comment_content", e.getMessage());
            return "CommentUpdateView";
        }
    }

    @GetMapping("{id}/delete")
    public String deleteComment(@PathVariable Long id, Model model, HttpSession session) {
        try {
            User currentUser = authenticationHelper.tryGetCurrentUser(session);
            commentServices.delete(id, currentUser);
            return "redirect:/";
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
