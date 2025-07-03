package com.example.registration.service;

import com.example.registration.dto.MemberFormDTO;
import com.example.registration.entity.Member;
import com.example.registration.entity.Department;
import com.example.registration.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Member createMember(MemberFormDTO memberForm, Department department) {
        Member existingMember = memberRepository.findByEmail(memberForm.getEmail());
        if (existingMember != null) {
            throw new IllegalArgumentException("Email already exists!");
        }

        Member member = new Member();
        member.setName(memberForm.getName());
        member.setEmail(memberForm.getEmail());
        member.setPassword(passwordEncoder.encode(memberForm.getPassword()));  // Hashing the password
        member.setDepartment(department);

        return memberRepository.save(member);
    }



    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    public List<Member> getMembersByDepartmentId(Long departmentId) {
        return memberRepository.findByDepartmentId(departmentId);
    }

    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
    
    public List<Member> getMembersByDepartment(Department department) {
        return memberRepository.findByDepartment(department);
    }

}
