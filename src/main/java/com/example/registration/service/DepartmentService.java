package com.example.registration.service;

import com.example.registration.entity.Department;
import com.example.registration.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;
    
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }
    
    @Transactional
    public void saveDepartment(Department department) {
        departmentRepository.save(department);
    }

    public Department getDepartmentByEmail(String email) {
        return departmentRepository.findByEmail(email); 
    }
    
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id).orElse(null);
    }
    
    public Department getAuthenticatedDepartment(Long departmentId, String username) {
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new IllegalArgumentException("Department not found"));

        if (!department.getUser().getEmail().equals(username)) {
            throw new SecurityException("You do not have access to this department.");
        }

        return department;
    }


}
