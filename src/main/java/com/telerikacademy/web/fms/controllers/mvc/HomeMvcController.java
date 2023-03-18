package com.telerikacademy.web.fms.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeMvcController extends BaseController {

    @GetMapping
    public String showHomePage() {
        return "index";
    }
}
