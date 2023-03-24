package com.telerikacademy.web.fms.controllers.mvc;

import com.telerikacademy.web.fms.exceptions.AuthorizationException;
import com.telerikacademy.web.fms.exceptions.EntityDuplicateException;
import com.telerikacademy.web.fms.helpers.AuthenticationHelper;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.models.dto.UserDTO;
import com.telerikacademy.web.fms.models.validations.LoginValidationGroup;
import com.telerikacademy.web.fms.models.validations.CreateValidationGroup;
import com.telerikacademy.web.fms.services.ModelMapper;
import com.telerikacademy.web.fms.services.contracts.UserServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthenticationMvcController extends BaseMvcController {
    private final UserServices userServices;
    private final ModelMapper modelMapper;
    private final AuthenticationHelper authenticationHelper;

    public AuthenticationMvcController(UserServices userServices, ModelMapper modelMapper, AuthenticationHelper authenticationHelper) {
        this.userServices = userServices;
        this.modelMapper = modelMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("user", new UserDTO());
        return "LoginView";
    }

    @PostMapping("/login")
    public String handleLogin(@Validated(LoginValidationGroup.class) @ModelAttribute("user") UserDTO userDTO, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) return "LoginView";

        try {
            User user = authenticationHelper.verifyAuthentication(userDTO.getUsername(), userDTO.getPassword());
            session.setAttribute("userId", user.getId());
            session.setAttribute("user", user);
            session.setAttribute("currentUser", userDTO.getUsername());
            session.setAttribute("isAdmin", user.getPermission().isAdmin());
            session.setAttribute("isBlocked", user.getPermission().isBlocked());
            return "redirect:/";
        } catch (AuthorizationException e) {
            bindingResult.rejectValue("username", "auth_error", e.getMessage());
            return "LoginView";
        }
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new UserDTO());
        return "RegisterView";
    }

    @PostMapping("/register")
    public String handleRegister(@Validated(CreateValidationGroup.class) @ModelAttribute("user") UserDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "RegisterView";

        try {
            User user = modelMapper.dtoToObject(userDTO);
            userServices.create(user);
            return "redirect:/";
        } catch (EntityDuplicateException e) {
            if (e.getErrorType().equals("username")) {
                bindingResult.rejectValue("username", "username_exists", e.getMessage());
            } else if (e.getErrorType().equals("email")) {
                bindingResult.rejectValue("email", "email_exists", e.getMessage());
            }
            return "RegisterView";
        }
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
}
