package com.telerikacademy.web.fms.controllers.mvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class BaseMvcController {
    @ModelAttribute
    public void populateModels(HttpSession session, HttpServletRequest request, Model model) {
        boolean isAuthenticated = session.getAttribute("currentUser") != null;
        session.setAttribute("isAuthenticated" , isAuthenticated);
        session.setAttribute("isAdmin", session.getAttribute("isAdmin"));
        model.addAttribute("requestURI", request.getRequestURI());
    }
}
