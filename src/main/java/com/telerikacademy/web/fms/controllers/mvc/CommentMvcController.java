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
import com.telerikacademy.web.fms.services.contracts.PostServices;
import com.telerikacademy.web.fms.services.contracts.UserServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public String showAllComments(Model model) {
        model.addAttribute("comments", commentServices.getAll());
        return "CommentsView";
    }

    @GetMapping("{id}")
    public String showComment(@PathVariable Long id, Model model, HttpSession session) {
        try {
            Comment comment = commentServices.getById(id);
            model.addAttribute("comment", comment);
            return "CommentView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }

    @GetMapping("{id}/update")
    public String showUpdateCommentPage(@PathVariable Long id, Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Comment comment = commentServices.getById(id);
            CommentDTO commentDTO = modelMapper.toDto(comment);
            model.addAttribute("comment", commentDTO);
            return "CommentUpdateView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }

    @PostMapping("{id}/update")
    @ExceptionHandler(Exception.class)
    public String updateComment(@PathVariable Long id,
                                @Validated(UpdateValidationGroup.class) @ModelAttribute("comment") CommentDTO commentDTO,
                                BindingResult bindingResult,
                                Model model,
                                HttpSession session) {
        User currentUser = null;
        try {
            currentUser = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) return "CommentUpdateView";

        try {
            Comment comment = modelMapper.dtoToObject(id, commentDTO);
            commentServices.update(comment, currentUser);
            return "redirect:/comments/" + comment.getId();
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
        User currentUser = null;
        try {
            currentUser = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            commentServices.delete(id, currentUser);
            return "redirect:/";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "AccessDeniedView";
        }
    }
}
