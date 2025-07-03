package com.example.registration.repository;

import com.example.registration.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Department findByEmail(String email); 

}
