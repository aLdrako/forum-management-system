package com.telerikacademy.web.fms.controllers.mvc;

import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.services.contracts.UserServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserMvcController {

    private final UserServices userServices;

    public UserMvcController(UserServices userServices) {
        this.userServices = userServices;
    }

    @GetMapping
    public String showAllUsers(Model model) {
        model.addAttribute("users", userServices.getAll());
        return "UsersView";
    }

    @GetMapping("/{id}")
    public String showUser(@PathVariable Long id, Model model) {
        try {
            User user = userServices.getById(id);
            model.addAttribute("user", user);
            return "UserView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }
}
