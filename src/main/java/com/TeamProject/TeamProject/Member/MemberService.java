package com.TeamProject.TeamProject.Member;

import com.TeamProject.TeamProject.DataNotFoundException;
import com.TeamProject.TeamProject.DuplicateMemberIdException;
import com.TeamProject.TeamProject.IdorPassword.EmailService;
import com.TeamProject.TeamProject.SNS.SMSService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class MemberService {

  public final MemberRepository memberRepository;
  public final PasswordEncoder passwordEncoder;
  private final EmailService emailService;
  private final SMSService smsService;

  public Member create(String memberId, String password, String nickname, String email, String SignUpDate, String phone) {

    Optional<Member> findResult = this.memberRepository.findByMemberId(memberId);
    if(findResult.isPresent()) {
      throw new DuplicateMemberIdException(" 이미 존재하는 아이디입니다.");
    }

    Member member = new Member();
    member.setMemberId(memberId);
    member.setPassword(passwordEncoder.encode(password));
    member.setNickname(nickname);
    member.setEmail(email);
    member.setSignUpDate(LocalDateTime.now());
    member.setPhone(phone);

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


  public List<Member> findIdByEmail(String email) {
    List<Member> members = this.memberRepository.findByEmail(email);

    // 이메일에 해당하는 회원이 없을 경우
    if (members.isEmpty()) {
      throw new DataNotFoundException("이메일에 해당하는 회원이 없습니다.");
    }

    // 중복된 이메일을 가진 모든 회원의 아이디를 반환
    return members;
  }
  // 추가: 가입일시로 회원 찾기
  public Optional<Member> findBySignUpDate(LocalDateTime signUpDate) {
    return memberRepository.findBySignUpDate(signUpDate);
  }

  public String findPasswordByMemberId(String username) {
    Optional<Member> memberOptional = memberRepository.findByMemberId(username);
    return memberOptional.map(Member::getPassword).orElse(null);
  }

  public List<Member> findIdByPhone(String phone) {
    List<Member> members = this.memberRepository.findByPhone(phone);

    // 휴대폰 번호에 해당하는 회원이 없을 경우
    if (members.isEmpty()) {
      throw new DataNotFoundException("휴대폰 번호에 해당하는 회원이 없습니다.");
    }

    // 중복된 휴대폰 번호를 가진 모든 회원의 아이디를 반환
    return members;
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
    // 임시인증번호 업데이트
    member.setPassword(passwordEncoder.encode(temporaryPassword));
    return memberRepository.save(member);
  }

  public Member updatePassword(Member member,String newPassword) {
    // 비밀번호 재설정 업데이트 로직을 여기에 구현
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

  //비밀번호(findPassword) 찾기 첫시도 메서드
  public void sendTemporaryPassword(Member member, Model model) {
    String userEmail = member.getEmail();
    String temporaryPassword = generateTemporaryPassword();
    emailService.sendVerificationCode(userEmail, temporaryPassword);

    // 모델에 속성 추가
    model.addAttribute("verificationCode", temporaryPassword);
    model.addAttribute("verificationCodeSent", true);
    model.addAttribute("userEmail", userEmail);
    model.addAttribute("message", "임시번호가 이메일로 전송되었습니다.");
    model.addAttribute("showConfirmationScript", true);
  }
  public void sendTemporaryPasswordPhone(Member member, Model model){
    String userPhone = member.getPhone();
    // 인증 코드 생성 (여기에서는 간단하게 난수로 생성)
    String verificationCodeSMS = String.valueOf((int) (Math.random() * 9000) + 1000);

    smsService.sendMessage(userPhone, verificationCodeSMS);//sms서비스에서 인증번호 보내는 로직 가져와서 사용했습니다.
    model.addAttribute("verificationCodePhone", verificationCodeSMS);//임시비밀번호!!
    model.addAttribute("verificationCodeSentPhone", true);//임시비밀번호 첫번째폼
    model.addAttribute("userPhone", userPhone); // 폰 정보를 모델에 추가
    model.addAttribute("message", "임시번호가 휴대전화번호로 전송되었습니다.");
    // JavaScript로 확인 메시지를 보여주는 스크립트 추가
    model.addAttribute("showConfirmationScript", true);

  }



  public FindIdParam getDefaultParam() {
    FindIdParam findIdParam = new FindIdParam();

    findIdParam.setVerificationCode(null);
    findIdParam.setVerificationCodeForm(false);
    findIdParam.setVerificationCodeMismatch(null);
    findIdParam.setSendAlert(false);
    findIdParam.setMembers(null);

    return findIdParam;
  }

  public FindIdParam getParamForSendVerification(String storedVerificationCode, String email) {

    FindIdParam findIdParam = getDefaultParam();

    findIdParam.setEmail(email);
    findIdParam.setVerificationCode(storedVerificationCode);
    findIdParam.setVerificationCodeForm(true);
    findIdParam.setSendAlert(true);

    return findIdParam;
  }

  public FindIdParam getParamForFindId(String storedVerificationCode, String inputVerification, String email) {

    List<Member> members = findIdByEmail(email);

    FindIdParam findIdParam = getParamForSendVerification(storedVerificationCode, email);

    findIdParam.setSendAlert(false);
    findIdParam.setVerificationCodeMismatch(false);
    findIdParam.setMembers(members);

    if(!storedVerificationCode.equals(inputVerification)) {
      findIdParam.setVerificationCodeMismatch(true);
      findIdParam.setErrorMessage("인증번호가 불일치합니다..");
    }

    if(members.isEmpty()) {
      findIdParam.setErrorMessage("이메일에 해당하는 회원이 없습니다.");
      findIdParam.setMembers(null);
    }

    return findIdParam;
  }
  public FindIdParam getParamForFindIdPhone(String storedVerificationCode, String inputVerification, String phone) {

    List<Member> members = findIdByPhone(phone);

    FindIdParam findIdParam = getParamForSendVerification(storedVerificationCode, phone);

    findIdParam.setSendAlert(false);
    findIdParam.setVerificationCodeMismatch(false);
    findIdParam.setMembers(members);

    if(!storedVerificationCode.equals(inputVerification)) {
      findIdParam.setVerificationCodeMismatch(true);
      findIdParam.setErrorMessage("인증번호가 불일치합니다..");
    }

    if(members.isEmpty()) {
      findIdParam.setErrorMessage("휴대전화번호에 해당하는 회원이 없습니다.");
      findIdParam.setMembers(null);
    }

    return findIdParam;
  }
  public FindIdParam getParamForSendVerificationPhone(String storedVerificationCode, String phone) {

    FindIdParam findIdParam = getDefaultParam();

    findIdParam.setEmail(phone);
    findIdParam.setVerificationCode(storedVerificationCode);
    findIdParam.setVerificationCodeForm(true);
    findIdParam.setSendAlert(true);

    return findIdParam;
  }

}