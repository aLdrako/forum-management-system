package com.telerikacademy.web.fms.controllers.mvc;

import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.models.dto.UserDTO;
import com.telerikacademy.web.fms.services.contracts.UserServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/auth")
public class LoginMvcController {
    private static final String USERNAME_PREFIX = "username=";
    private final UserServices userServices;

    public LoginMvcController(UserServices userServices) {
        this.userServices = userServices;
    }

    @GetMapping("/login")
    public String login() {
        return "LoginView";
    }

    @PostMapping("/login")
    public String handleLogin(@Validated(LoginMvcController.class) @ModelAttribute UserDTO userDTO,
                              BindingResult bindingResult, HttpSession session) {
        try {
            User user = userServices.search(USERNAME_PREFIX + userDTO.getUsername()).get(0);
            if (!user.getPassword().equals(userDTO.getPassword())) {
                bindingResult.rejectValue("username", String.valueOf(HttpStatus.UNAUTHORIZED));
                return "LoginView";
            }
        } catch (EntityNotFoundException e) {
            bindingResult.rejectValue("username", String.valueOf(HttpStatus.NOT_FOUND), e.getMessage());
            return "LoginView";
        }

        session.setAttribute("currentUserName", userDTO.getUsername());
        return "redirect:/";
    }
}
