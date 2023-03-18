package com.telerikacademy.web.fms.controllers.mvc;

import com.telerikacademy.web.fms.exceptions.EntityDuplicateException;
import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
import com.telerikacademy.web.fms.models.User;
import com.telerikacademy.web.fms.models.dto.UserDTO;
import com.telerikacademy.web.fms.models.validations.UpdateValidationGroup;
import com.telerikacademy.web.fms.services.ModelMapper;
import com.telerikacademy.web.fms.services.contracts.UserServices;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserMvcController extends BaseController {

    private final UserServices userServices;

    private final ModelMapper modelMapper;

    public UserMvcController(UserServices userServices, ModelMapper modelMapper) {
        this.userServices = userServices;
        this.modelMapper = modelMapper;
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

    @GetMapping("{id}/update")
    public String showUpdateUserPage(@PathVariable Long id, Model model) {
        try {
            User user = userServices.getById(id);
            UserDTO userDTO = modelMapper.objectToDto(user);
            model.addAttribute("userId", id);
            model.addAttribute("user", userDTO);
            model.addAttribute("phone", userDTO.getPhoneNumber());
            return "UpdateUserView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }

    // TODO implement MCV authentication
    @PostMapping("{id}/update")
    public String updateUser(@PathVariable Long id, @Validated(UpdateValidationGroup.class) @ModelAttribute("user") UserDTO userDTO, BindingResult bindingResult, Model model, HttpServletRequest request) {

        if (bindingResult.hasErrors()) return "UpdateUserView";

        try {
            User user = modelMapper.dtoToObject(id, userDTO);
            userServices.update(user);
            return "redirect:/users/" + user.getId();
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (EntityDuplicateException e) {
            bindingResult.rejectValue("username", "username.exists", e.getMessage());
            bindingResult.rejectValue("email", "email.exists", e.getMessage());
            return "UpdateUserView";
        }
    }
}
