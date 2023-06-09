package com.telerikacademy.web.fms.controllers.mvc;

import com.telerikacademy.web.fms.exceptions.AuthorizationException;
import com.telerikacademy.web.fms.exceptions.EntityDuplicateException;
import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.helpers.AuthenticationHelper;
import com.telerikacademy.web.fms.models.Comment;
import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.models.dto.UserDTO;
import com.telerikacademy.web.fms.models.validations.UpdateValidationGroup;
import com.telerikacademy.web.fms.services.ModelMapper;
import com.telerikacademy.web.fms.services.contracts.CommentServices;
import com.telerikacademy.web.fms.services.contracts.PostServices;
import com.telerikacademy.web.fms.services.contracts.UserServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/users")
public class UserMvcController extends BaseMvcController implements HandlerExceptionResolver {
    private final UserServices userServices;
    private final PostServices postServices;
    private final CommentServices commentServices;
    private final ModelMapper modelMapper;
    private final AuthenticationHelper authenticationHelper;

    public UserMvcController(ModelMapper modelMapper, UserServices userServices, PostServices postServices, CommentServices commentServices, AuthenticationHelper authenticationHelper) {
        this.modelMapper = modelMapper;
        this.userServices = userServices;
        this.postServices = postServices;
        this.commentServices = commentServices;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public String showAllUsers(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetCurrentAdmin(session);

            Pageable pageable = PageRequest.of(page, size);
            Page<User> userPage = userServices.findAll(pageable);

            model.addAttribute("users", userPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("sizePage", size);
            model.addAttribute("totalPages", userPage.getTotalPages());
            return "UsersView";
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "AccessDeniedView";
        }
    }

    @GetMapping(value = "/search")
    public String search(@RequestParam Map<String, String> parameter, Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetCurrentAdmin(session);
            model.addAttribute("search", parameter.get("q"));
            List<User> users = userServices.search(String.valueOf(parameter.entrySet().iterator().next()));
            model.addAttribute("users", users);
            return "UsersView";
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("users", List.of());
            return "UsersView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "AccessDeniedView";
        }
    }

    @GetMapping("/{id}")
    public String showUser(@PathVariable Long id, @RequestParam(required = false) Map<String, String> parameters, Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetCurrentUser(session);
            User user = userServices.getById(id);
            parameters.put("userId", String.valueOf(id));
            List<Post> postsByUserId = postServices.getAll(parameters);
            List<Comment> commentsByUserId = commentServices.getCommentsByUserId(id, parameters);
            model.addAttribute("comments", commentsByUserId);
            model.addAttribute("posts", postsByUserId);
            model.addAttribute("user", user);
            return "UserView";
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }

    @GetMapping({
            "{id}/update",
            "{id}/update/photo",
            "{id}/update/permissions"
    })
    public String showUpdateUserPage(@PathVariable Long id, Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetCurrentUser(session);
            User user = userServices.getById(id);
            UserDTO userDTO = modelMapper.objectToDto(user);
            model.addAttribute("user", userDTO);
            session.setAttribute("updatedUserIsAdmin", user.getPermission().isAdmin());
            session.setAttribute("updatedUserUsername", user.getUsername());
            return "UserUpdateView";
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }

    @PostMapping({
            "{id}/update",
            "{id}/update/photo",
            "{id}/update/permissions"
    })
    public String updateUser(@PathVariable Long id,
                             @Validated(UpdateValidationGroup.class) @ModelAttribute("user") UserDTO userDTO,
                             BindingResult bindingResult,
                             Model model,
                             HttpSession session,
                             HttpServletRequest request,
                             @RequestParam(name = "photo", required = false) MultipartFile photo) throws IOException {

        if (photo != null && !photo.isEmpty()) {
            userDTO.setPhoto(Base64.getEncoder().encodeToString(photo.getBytes()));
        }

        if (bindingResult.hasErrors() && (photo == null || photo.isEmpty())) {
            return "UserUpdateView";
        }

        if (!request.getRequestURI().contains("photo") &&
                !request.getRequestURI().contains("permissions") &&
                userDTO.getPassword() != null && userDTO.getPasswordConfirm() != null &&
                !userDTO.getPassword().equals(userDTO.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "password_error", "Password confirmation should match password");
            return "UserUpdateView";
        }

        try {
            User currentUser = authenticationHelper.tryGetCurrentUser(session);
            User user = modelMapper.dtoToObject(id, userDTO);

            if (request.getRequestURI().contains("permissions")) {
                userServices.updatePermissions(user.getPermission(), currentUser);
            } else {
                userServices.update(user, currentUser);
            }
            return "redirect:/users/" + user.getId();
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (EntityDuplicateException e) {
            bindingResult.rejectValue("email", "email_exists", e.getMessage());
            return "UserUpdateView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "AccessDeniedView";
        }
    }

    @GetMapping("{id}/delete")
    public String deleteUser(@PathVariable Long id, Model model, HttpSession session) {
        try {
            User currentUser = authenticationHelper.tryGetCurrentUser(session);
            userServices.delete(id, currentUser);
            if (!currentUser.getPermission().isAdmin()) session.invalidate();
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

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView modelAndView = new ModelAndView("AccessDeniedView");
        modelAndView.getModel().put("user", new UserDTO());
        if (ex instanceof MaxUploadSizeExceededException) {
            modelAndView.getModel().put("error", "File size exceeds limit of 64KB!");
        }
        return modelAndView;
    }
}
