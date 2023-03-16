package com.telerikacademy.web.fms.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginMvcController {

    @GetMapping
    public String login() {
        return "LoginView";
    }
}
