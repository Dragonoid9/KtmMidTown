package com.rac.ktm.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("isLoggedIn")
    public boolean addLoggedInAttribute(HttpSession session) {
        // This will ensure that 'isLoggedIn' is available to all views and is set properly
        return session.getAttribute("isLoggedIn") != null && (Boolean) session.getAttribute("isLoggedIn");
    }

    @ModelAttribute("currentUser")
    public String addCurrentUserAttribute(HttpSession session) {
        // Optionally, you can also add other session attributes globally
        return (String) session.getAttribute("user");
    }
}