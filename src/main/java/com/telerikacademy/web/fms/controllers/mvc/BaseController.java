package com.telerikacademy.web.fms.controllers.mvc;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class BaseController {

    @ModelAttribute
    public void isAuthenticated(HttpSession session) {
        boolean isAuthenticated = session.getAttribute("currentUser") != null;
        Long userId = (Long) session.getAttribute("userId");
        session.setAttribute("userId", userId);
        session.setAttribute("isAuthenticated" , isAuthenticated);
    }
}
