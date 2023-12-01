package com.TeamProject.TeamProject.Member;

import com.TeamProject.TeamProject.DataNotFoundException;
import com.TeamProject.TeamProject.IdorPassword.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MemberService {

  public final MemberRepository memberRepository;
  public final PasswordEncoder passwordEncoder;
  private final EmailService emailService;

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


  public List<String> findIdByEmail(String email) {
    List<Member> members = this.memberRepository.findByEmail(email);

    // 이메일에 해당하는 회원이 없을 경우
    if (members.isEmpty()) {
      throw new DataNotFoundException("이메일에 해당하는 회원이 없습니다.");
    }

    // 중복된 이메일을 가진 모든 회원의 아이디를 반환
    return members.stream()
            .map(Member::getMemberId)
            .collect(Collectors.toList());
  }

  public String findPasswordByMemberId(String username) {
    Optional<Member> memberOptional = memberRepository.findByMemberId(username);
    return memberOptional.map(Member::getPassword).orElse(null);
  }

  public String generateTemporaryPassword() {
    // 임시 비밀번호를 랜덤으로 생성 (이 부분을 보안적으로 강화할 수 있음)
    int length = 10;
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    StringBuilder temporaryPassword = new StringBuilder();

    Random random = new Random();
    for (int i = 0; i < length; i++) {
      temporaryPassword.append(characters.charAt(random.nextInt(characters.length())));
    }

    return temporaryPassword.toString();
  }



  public Member updateTemporayPassword(Member member, String temporaryPassword) {
    // 임시비밀번호 업데이트
    member.setPassword(passwordEncoder.encode(temporaryPassword));
    return memberRepository.save(member);
  }

  public Member updatePassword(Member member,String newPassword) {
    // 비밀번호 업데이트 로직을 여기에 구현
    // 일반적으로는 Member 엔터티의 비밀번호 필드를 업데이트하고 저장하는 로직이 들어갑니다.
    member.setPassword(passwordEncoder.encode(newPassword)); // 새 비밀번호를 암호화하여 저장
    memberRepository.save(member); // 업데이트된 회원 정보 저장
    return memberRepository.save(member);
  }
  // 새로운 메서드 추가: 인증번호 재전송
  public void resendVerificationCode(String memberId) throws DataNotFoundException {
    Optional<Member> memberOptional = memberRepository.findByMemberId(memberId);

    if (memberOptional.isPresent()) {
      Member member = memberOptional.get();
      String temporaryPassword = generateTemporaryPassword();
      updateTemporayPassword(member, temporaryPassword);

      // 이메일 전송 로직을 호출
      String userEmail = member.getEmail();
      emailService.sendVerificationCode(userEmail, temporaryPassword);
    } else {
      throw new DataNotFoundException("존재하지 않는 아이디입니다.");
    }
  }



}