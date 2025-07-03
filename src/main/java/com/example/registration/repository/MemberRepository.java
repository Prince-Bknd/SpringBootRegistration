package com.example.registration.repository;

import com.example.registration.entity.Department;
import com.example.registration.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	List<Member> findByDepartment(Department department);
    List<Member> findByDepartmentId(Long departmentId);

    Member findByEmail(String email);
}
