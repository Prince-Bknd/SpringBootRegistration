package com.example.registration.controller;

import com.example.registration.dto.MemberFormDTO;
import com.example.registration.entity.Department;
import com.example.registration.entity.Member;
import com.example.registration.service.DepartmentService;
import com.example.registration.service.MemberService;
import com.example.registration.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MemberController {

    private final MemberService memberService;
    private final DepartmentService departmentService;

    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    public MemberController(MemberService memberService, DepartmentService departmentService) {
        this.memberService = memberService;
        this.departmentService = departmentService;
    }

    @PostMapping("/add-member")
    public String addMember(
            @Valid @ModelAttribute("memberForm") MemberFormDTO memberForm,
            BindingResult result,
            @RequestParam("departmentId") Long departmentId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Department sessionDepartment = (Department) session.getAttribute("department");

        if (sessionDepartment == null || !sessionDepartment.getId().equals(departmentId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied. Please login again.");
            System.out.println("Pass 1");
            return "redirect:/login";
        }

//        if (result.hasErrors()) {
//            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.memberForm", result);
//            redirectAttributes.addFlashAttribute("memberForm", memberForm);
//            return "redirect:/" + departmentId + "/add-member"; // Stay on the same page for corrections
//        }

        Department department = departmentService.getDepartmentById(departmentId);
        memberService.createMember(memberForm, department);

        System.out.println("Successfully created member");
        redirectAttributes.addFlashAttribute("successMessage", "Member added successfully!");
        return "redirect:/" + departmentId + "/dashboard";
    }
    
    @GetMapping("/member-login")
    public String getMemberDashboard(){
    	return "member-login";
    }

    @PostMapping("/member-login")
    public String handleMemberLogin(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model
    ) {
        Member member = memberRepository.findByEmail(email);

        if (member != null && passwordEncoder.matches(password, member.getPassword()) && member.getName().equals(name)) {
            model.addAttribute("name", member.getName());
            return "department-member-dashboard"; 
        } else {
            model.addAttribute("error", "Invalid name, email, or password");
            return "member-login";
        }
    }

    @ExceptionHandler(SecurityException.class)
    public String handleSecurityException(SecurityException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/error";
    }
}
