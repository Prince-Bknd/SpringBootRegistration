package com.example.registration.controller;

import com.example.registration.entity.Department;
import com.example.registration.entity.User;
import com.example.registration.service.DepartmentService;
import com.example.registration.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DepartmentController {

    private final DepartmentService departmentService;
    private final UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    public DepartmentController(DepartmentService departmentService, UserService userService) {
        this.departmentService = departmentService;
        this.userService = userService;
    }

    @GetMapping("/department")
    public String showDepartmentDashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Department department = (Department) session.getAttribute("department");

        if (department == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please login first to access the department dashboard.");
            return "redirect:/login";
        }

        model.addAttribute("department", department);
        return "department";  
    }

    @PostMapping("/department-login")
    public String processLogin(
            @RequestParam("department") @NotBlank String departmentName,
            @RequestParam("email") @Email String email,
            @RequestParam("password") @NotBlank String password,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (departmentName.isBlank() || email.isBlank() || password.isBlank()) {
                redirectAttributes.addFlashAttribute("errorMessage", "All fields are required.");
                return "redirect:/login";
            } 
            
            Department department = departmentService.getDepartmentByEmail(email);

            if (department == null || !department.getName().equalsIgnoreCase(departmentName)
                    || !passwordEncoder.matches(password, department.getPassword())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid department name, email, or password.");
                return "redirect:/login";
            }
            
            session.setAttribute("department", department);
            System.out.println("Login For Department Success");
            return "redirect:/department";

        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred during login. Please try again.");
            return "redirect:/login";
        }
    }
    
    @GetMapping("/add-department")
    public String tryAddDepartment() {
        return "login"; 
    }

    @PostMapping("/add-department")
    public String addDepartment(
            @RequestParam("user-id") Long userId,
            @RequestParam("user-email") String userEmail,
            @RequestParam("department-name") String departmentName,
            @RequestParam("department-email") String departmentEmail,
            @RequestParam("department-password") String departmentPassword,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (userId == null || 
                userEmail.isBlank() || 
                departmentName.isBlank() || 
                departmentEmail.isBlank() || 
                departmentPassword.isBlank()) {
                
                redirectAttributes.addFlashAttribute("errorMessage", "All fields are required.");
                return "redirect:/welcome";
            }

            if (!userEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$") || !departmentEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid email format.");
                return "redirect:/welcome";
            }

            User user = userService.getUserById(userId);

            if (user == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
                return "redirect:/welcome";
            }

            if (!user.getEmail().equalsIgnoreCase(userEmail)) {
                redirectAttributes.addFlashAttribute("errorMessage", "User email mismatch.");
                return "redirect:/welcome";
            }
            
            Department department = new Department();
            department.setUser(user);
            department.setName(departmentName);
            department.setEmail(departmentEmail);
            
            department.setPassword(passwordEncoder.encode(departmentPassword));

            departmentService.saveDepartment(department);

            redirectAttributes.addFlashAttribute("successMessage", "Department added successfully!");
            System.out.println("Department Saved Successfully");
            return "redirect:/department";

        } catch (Exception ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add department. Please try again.");
            return "redirect:/welcome";
        }
    }

}
