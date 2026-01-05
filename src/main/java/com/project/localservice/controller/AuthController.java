package com.project.localservice.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.project.localservice.model.User;
import com.project.localservice.repository.UserRepository;

@Controller
public class AuthController {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {

        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(Model model, @ModelAttribute User user) {

        user.setPassword(encoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDate.now());
        user.setEnabled(true);
        repo.save(user);
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {

        UserDetails user = (UserDetails) auth.getPrincipal();

        // If user is blocked
        if (!user.isEnabled()) {
            return "redirect:/block";
        }

        // Role based redirect
        if (hasRole(user, "ADMIN")) {
            return "redirect:/admin/dashboard";
        }

        if (hasRole(user, "HELPER")) {
            return "redirect:/helper/dashboard";
        }

        // Default USER
        return "redirect:/user/dashboard";
    }

    private boolean hasRole(UserDetails user, String role) {
        return user.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    @GetMapping("/block")
    public String getBlock(Model model) {

        model.addAttribute("message", "You Have Blocked From Here!!");

        return "block";
    }

}
