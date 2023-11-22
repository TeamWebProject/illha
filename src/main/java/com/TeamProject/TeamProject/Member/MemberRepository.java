package com.TeamProject.TeamProject.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByMemberId(String memberId);

    Optional<Member> findByEmail(String email);
}
