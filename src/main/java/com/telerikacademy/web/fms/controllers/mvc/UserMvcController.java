package com.telerikacademy.web.fms.controllers.mvc;

import com.telerikacademy.web.fms.exceptions.AuthorizationException;
import com.telerikacademy.web.fms.exceptions.EntityDuplicateException;
import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.fms.helpers.AuthenticationHelper;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.models.dto.UserDTO;
import com.telerikacademy.web.fms.models.validations.UpdateValidationGroup;
import com.telerikacademy.web.fms.services.ModelMapper;
import com.telerikacademy.web.fms.services.contracts.UserServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;

@Controller
@RequestMapping("/users")
public class UserMvcController extends BaseMvcController {

    private final UserServices userServices;
    private final ModelMapper modelMapper;
    private final AuthenticationHelper authenticationHelper;

    public UserMvcController(UserServices userServices, ModelMapper modelMapper, AuthenticationHelper authenticationHelper) {
        this.userServices = userServices;
        this.modelMapper = modelMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public String showAllUsers(Model model) {
        model.addAttribute("users", userServices.getAll());
        return "UsersView";
    }

    @GetMapping("/{id}")
    public String showUser(@PathVariable Long id, Model model, HttpSession session) {
        try {
            User user = userServices.getById(id);
            model.addAttribute("user", user);
            return "UserView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }

    @GetMapping({
            "{id}/update",
            "{id}/update/photo"
    })
    public String showUpdateUserPage(@PathVariable Long id, Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            User user = userServices.getById(id);
            UserDTO userDTO = modelMapper.objectToDto(user);
            model.addAttribute("user", userDTO);
            session.setAttribute("updatedUserIsAdmin", user.getPermission().isAdmin());
            return "UserUpdateView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }

    @PostMapping("{id}/update")
    public String updateUser(@PathVariable Long id,
                             @Validated(UpdateValidationGroup.class) @ModelAttribute("user") UserDTO userDTO,
                             BindingResult bindingResult,
                             Model model,
                             HttpSession session) {
        User currentUser = null;
        try {
            currentUser = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) return "UserUpdateView";

        try {
            User user = modelMapper.dtoToObject(id, userDTO);
            userServices.update(user, currentUser);
            return "redirect:/users/" + user.getId();
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

    // TODO fix/implement
    @PostMapping(path = "{id}/update/photo", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ExceptionHandler(Exception.class)
    public String uploadUserPhoto(@PathVariable Long id, @Validated(UpdateValidationGroup.class) @ModelAttribute("user") UserDTO userDTO,
                                  BindingResult bindingResult, Model model, HttpSession session, @RequestPart(name = "floatingPhoto", required = false) MultipartFile floatingPhoto) throws IOException, SQLException {
        User currentUser = null;
        try {
            currentUser = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) return "UserUpdateView";

        try {
            User user = modelMapper.dtoToObject(id, userDTO);
            userServices.update(user, currentUser);
            return "redirect:/users/" + user.getId();
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
        User currentUser = null;
        try {
            currentUser = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            userServices.delete(id, currentUser);
            if (!currentUser.getPermission().isAdmin()) session.invalidate();
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
