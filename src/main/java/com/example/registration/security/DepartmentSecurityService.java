package com.example.registration.security;

import com.example.registration.entity.Department;
import com.example.registration.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("departmentSecurityService")
public class DepartmentSecurityService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public boolean isDepartmentAdmin(Authentication authentication, Long departmentId) {
        String username = authentication.getName();
        Department department = departmentRepository.findById(departmentId)
                .orElse(null);
        if (department == null) {
            return false;
        }
        return department.getUser().getEmail().equals(username);
    }
}
