package com.TeamProject.TeamProject.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByMemberId(String memberId);


    List<Member> findByEmail(String email);
    List<Member> findByPhone(String phone);
    Optional<Member> findBySignUpDate(LocalDateTime signUpDate);
}




