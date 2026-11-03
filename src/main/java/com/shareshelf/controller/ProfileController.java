package com.shareshelf.controller;

import com.shareshelf.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String profile(
            @AuthenticationPrincipal User currentUser,
            Model model) {

        model.addAttribute("user", currentUser);
        model.addAttribute("isAuthenticated", true);

        return "profile";
    }
}