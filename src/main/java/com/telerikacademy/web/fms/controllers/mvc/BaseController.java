package com.telerikacademy.web.fms.controllers.mvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class BaseController {
    @ModelAttribute
    public void isAuthenticated(HttpSession session, HttpServletRequest request, Model model) {
        boolean isAuthenticated = session.getAttribute("currentUser") != null;
        session.setAttribute("isAuthenticated" , isAuthenticated);
        model.addAttribute("requestURI", request.getRequestURI());
    }
}
