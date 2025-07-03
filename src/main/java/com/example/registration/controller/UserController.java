package com.example.registration.controller;

import com.example.registration.entity.User;
import com.example.registration.repository.UserRepository;
import com.example.registration.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.util.Optional;


@Controller
public class UserController {

    @Autowired
    private UserRepository userRepo;    
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; 
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("user") User user, 
                                      BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "redirect:/error";  
        }

        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("emailError", "Email is already in use. Please use a different email.");
            return "redirect:/register";  
        }


        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user); 


        return "redirect:/login"; 
    }



    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/admin-login")
    public String showAdminLoginForm() {
        return "admin-login";
    }

    @PostMapping("/admin-login")
    public String processAdminLogin(@RequestParam String email, 
                                    @RequestParam String password, 
                                    Model model, HttpSession session) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            model.addAttribute("error", "All fields are required.");
            return "admin-login";
        }
        
        Optional<User> optionalUser = userRepo.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            if (passwordEncoder.matches(password, user.getPassword())) {
                session.setAttribute("user", user); 
                return "redirect:/welcome";
            }
        }
        model.addAttribute("error", "Invalid email or password.");
        return "error";
    }
    
    @GetMapping("/welcome")
    public String welcomePage(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return "redirect:/admin-login";  
        }
        return "welcome";
    }

    
    @PostMapping("/login")
    public String processLogin(@RequestParam String email, 
                               @RequestParam String password, 
                               Model model, 
                               HttpSession session) {

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            model.addAttribute("error", "All fields are required.");
            return "login";  
        }

        try {
            User user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("Invalid email or password."));
            
            if (passwordEncoder.matches(password, user.getPassword())) {
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getId());
                return "redirect:/welcome";
            }

            model.addAttribute("error", "Invalid email or password.");
            return "login";
        } catch (UserNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }
}