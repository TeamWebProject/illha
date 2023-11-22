package com.TeamProject.TeamProject.Member;

import com.TeamProject.TeamProject.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {

  public final MemberRepository memberRepository;
  public final PasswordEncoder passwordEncoder;

          public Member create(String memberId, String password, String nickname, String email) {
            Member member = new Member();
            member.setMemberId(memberId);
            member.setPassword(passwordEncoder.encode(password));
            member.setNickname(nickname);
            member.setEmail(email);

            this.memberRepository.save(member);
            return member;
          }

          public Member getMember(String memberId) {
            Optional<Member> member = this.memberRepository.findByMemberId(memberId);
            if (member.isPresent()) {
              return member.get();
            } else {
              throw new DataNotFoundException("회원이 없습니다.");
            }
          }

          public String findIdByEmail(String email) {
            // email을 기반으로 회원을 찾는 로직
            Optional<Member> member = memberRepository.findByEmail(email);
        // 회원이 존재하면 아이디 반환, 없으면 null 또는 예외 처리
            return member.map(Member::getId)
                    .map(Object::toString) // (옵션) Long,Interger 타입이면 문자열로 변환
                    .orElse(null);
          }
}
