package com.TeamProject.TeamProject.Member;


import com.TeamProject.TeamProject.DataNotFoundException;
import com.TeamProject.TeamProject.DuplicateMemberIdException;
import com.TeamProject.TeamProject.IdorPassword.EmailService;
import com.TeamProject.TeamProject.SNS.SMSService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@RequestMapping("/member")
public class MemberController {
  private final MemberService memberService;
  @Autowired
  private JavaMailSender javaMailSender;

  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private EmailService emailService;
  @Autowired
  private SMSService smsService;

  private FindIdParam findIdParam;


  @GetMapping("/signup")
  public String signup(MemberCreateForm memberCreateForm) {
    return "signup_form";
  }

  @PostMapping("/signup")
  public String signup(@Valid MemberCreateForm memberCreateForm, BindingResult bindingResult) {

    String errorReturnPage = "signup_form";

    if (bindingResult.hasErrors()) {
      return errorReturnPage;
    }

    if (!memberCreateForm.getPassword1().equals(memberCreateForm.getPassword2())) {
      bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
      return errorReturnPage;
    }

    try {
      this.memberService.create(memberCreateForm.getMemberId(), memberCreateForm.getPassword1(), memberCreateForm.getNickname(), memberCreateForm.getEmail(), String.valueOf(memberCreateForm.getSignUpDate()), memberCreateForm.getPhone());
    } catch (DuplicateMemberIdException e) {
      bindingResult.reject("duplicateMemberId", "이미 존재하는 아이디입니다.");
      return errorReturnPage;
    }

    return "redirect:/";
  }

  @GetMapping("/sendVerificationCodeSign")
  public ResponseEntity<String> sendVerificationCodeSign(@RequestParam String phoneNumber, HttpSession session) {
    try {
      // 휴대폰 인증 성공 시 SMS 전송

      String storedVerificationCode = String.valueOf((int) (Math.random() * 9000) + 1000);
      emailService.sendVerificationCode(phoneNumber, storedVerificationCode);

      // 생성된 인증 코드는 세션에 저장됩니다
      session.setAttribute("verificationCodeSMS", storedVerificationCode);

      return ResponseEntity.ok("Verification code sent successfully");
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending verification code");
    }
  }


  @GetMapping("/login")
  private String login(Model model, @RequestParam(defaultValue = "false") boolean modal) {

    model.addAttribute("modal", modal);
    return "login_form";
  }

  @GetMapping("/findIdTaTal")
  private String findIdTaTal() {

    return "find-id-total";
  }


  @GetMapping("/findId")
  public String findId(Model model) {
    FindIdParam findIdParam1 = memberService.getDefaultParam();
    //MemberService.FindIdParam findIdParam = memberService.getDefaultParam();
    model.addAttribute("paramForFindId", findIdParam1);

    return "find-id-form";
  }

  @PostMapping("/findId")
  public String findId(@RequestParam("email") String email, @RequestParam("inputVerificationCode") String inputVerificationCode, HttpSession session,
                       Model model) {

    // 세션에서 저장된 이메일 가져오기
    String userEmail = (String) session.getAttribute("userEmail");
    String storedVerificationCode = (String) session.getAttribute("storedVerificationCode");
    //MemberService.FindIdParam findIdParam = memberService.getParamForFindId(storedVerificationCode, inputVerificationCode, userEmail);
    FindIdParam findIdParam1 = memberService.getParamForFindId(storedVerificationCode,inputVerificationCode,userEmail);
    model.addAttribute("paramForFindId", findIdParam1);

    return "find-id-form";
  }

  @PostMapping("/sendVerificationCode")
  public String sendVerificationCode(@RequestParam("email") String email, @RequestParam(value = "inputVerificationCode", required = false) String inputVerificationCode, Model model, HttpSession session) {

    String storedVerificationCode = String.valueOf((int) (Math.random() * 9000) + 1000);

    emailService.sendVerificationCode(email, storedVerificationCode);
    session.setAttribute("userEmail", email);
    session.setAttribute("storedVerificationCode", storedVerificationCode);

    FindIdParam findIdParam1 = memberService.getParamForSendVerification(storedVerificationCode,email);
   // MemberService.FindIdParam findIdParam = memberService.getParamForSendVerification(storedVerificationCode, email);
    model.addAttribute("paramForFindId", findIdParam1);

    // 이메일 입력 폼으로 리다이렉트
    return "find-id-form";
  }

  @GetMapping("/findIdPhone")
  public String findIdPhone(Model model) {

    FindIdParam findIdParam1 = memberService.getDefaultParam();
    model.addAttribute("paramForFindIdPhone", findIdParam1);
    return "find-id-phone";
  }

  @PostMapping("/findIdPhone")
  public String findIdPhone(@RequestParam("phone") String phone, @RequestParam("inputVerificationCode") String inputVerificationCode, HttpSession session,
                            Model model) {
    // 세션에서 저장된 이메일 가져오기
    String userPhone = (String) session.getAttribute("userPhone");
    String storedVerificationCode = (String) session.getAttribute("storedVerificationCode");
    FindIdParam findIdParam1 = memberService.getParamForFindIdPhone(storedVerificationCode,inputVerificationCode,userPhone);
    model.addAttribute("paramForFindIdPhone", findIdParam1);

    return "find-id-phone";
  }

  @PostMapping("/sendVerificationCodeSMS")
  public String sendVerificationCodeSMS(@RequestParam("phone") String phone, @RequestParam(value = "inputVerificationCode", required = false) String inputVerificationCode, Model model, HttpSession session) {

    String storedVerificationCode = String.valueOf((int) (Math.random() * 9000) + 1000);

    emailService.sendVerificationCode(phone, storedVerificationCode);
    session.setAttribute("userPhone", phone);
    session.setAttribute("storedVerificationCode", storedVerificationCode);

    FindIdParam findIdParam1 = memberService.getParamForSendVerificationPhone(storedVerificationCode,phone);
    // MemberService.FindIdParam findIdParam = memberService.getParamForSendVerification(storedVerificationCode, email);
    model.addAttribute("paramForFindIdPhone", findIdParam1);

    // 휴대전화 입력 폼으로 리다이렉트
    return "find-id-phone";
  }

  //토탈로 묵어서 이부분은 모달로 자유롭게사용하셔도 무방합니다.
  @GetMapping("/findPasswordToTal")
  private String findPasswordTaTal() {

    return "find-password-total";
  }

  // 폰으로 패스워드 찾기 메서드 시작입니다.총 3가지 메서드
  @GetMapping("/findPassword")
  public String findPassword(@RequestParam(value = "resetSuccess", required = false) String resetSuccess,
                             Model model) {

    model.addAttribute("resetSuccess", resetSuccess);

    return "find-password-form";
  }

  @PostMapping("/findPassword")
  public String findPassword(@RequestParam("memberId") String memberId, Model model,
                             @RequestParam(name = "inputVerificationCode", defaultValue = "") String inputVerificationCode,
                             @RequestParam(name = "verificationCodeSent", defaultValue = "false") boolean verificationCodeSent,
                             @RequestParam(name = "verificationCode", defaultValue = "") String verificationCode) {
    try {
      Member member = memberService.getMember(memberId);

      model.addAttribute("memberId", memberId);

      // 첫 시도 -> 인증코드 보낸 적 없음
      if (!verificationCodeSent) {
        String userEmail = member.getEmail();
        String temporaryPassword = memberService.generateTemporaryPassword();
        emailService.sendVerificationCode(userEmail, temporaryPassword);
        model.addAttribute("verificationCode", temporaryPassword);
        model.addAttribute("verificationCodeSent", true);
        model.addAttribute("userEmail", userEmail); // 이메일 정보를 모델에 추가
        model.addAttribute("message", "임시번호가 이메일로 전송되었습니다.");

        // JavaScript로 확인 메시지를 보여주는 스크립트 추가
        model.addAttribute("showConfirmationScript", true);
        return "find-password-form";
      }
      // 이건 인증코드와 사용자가 입력한 코드가 일치하는조건을 불린으로 변수를만들어서 사용해줬습니다.
      //inputVerificationCode == 사용자가 입력하는코드
      boolean matched = verificationCode.equals(inputVerificationCode);//두개가 동일할때 조건

      // 위에 matched 조건이 성립할때
      if (matched) {
        model.addAttribute("verificationCodeValid", true);
        model.addAttribute("userEmail", member.getEmail()); // 여기에 userEmail 추가
      } else {
        model.addAttribute("message", "인증번호가 틀렸습니다.");
        model.addAttribute("verificationCodeValid", false);
        model.addAttribute("verificationCodeSent", true);
        model.addAttribute("verificationCode", verificationCode);
      }
      return "find-password-form";
    } catch (DataNotFoundException e) {
      model.addAttribute("message", "존재하지 않는 아이디입니다.");
      model.addAttribute("memberId", memberId);
    }

    return "find-password-form";


  }

  @PostMapping("/resendVerificationCode")//이메일로 찾기 인증번호 재전송메서드
  private String resendVerificationCode(Model model, @RequestParam("memberId") String memberId) {

    Member member = memberService.getMember(memberId);
    String userEmail = member.getEmail();

    String temporaryPassword = memberService.generateTemporaryPassword();
    emailService.sendVerificationCode(userEmail, temporaryPassword);
    model.addAttribute("verificationCode", temporaryPassword);
    model.addAttribute("verificationCodeSent", true);
    model.addAttribute("userEmail", userEmail); // 이메일 정보를 모델에 추가
    model.addAttribute("memberId", memberId); // 이메일 정보를 모델에 추가


    model.addAttribute("showConfirmationScript", true);
    model.addAttribute("message", "인증번호 재전송 성공");

    return "find-password-form";

  }

  @PostMapping("/passwordReset")//이메일로 찾기 비밀번호 재설정  메서드
  public String passwordReset(@RequestParam("memberId") String memberId,
                              @RequestParam("verificationCode") String verificationCode,
                              @RequestParam("verificationCodeValid") String verificationCodeValid,
                              @RequestParam("newPassword") String newPassword,
                              @RequestParam("newPassword1") String newPassword1,
                              Model model) {

    //실행테스트 출력코드
    System.out.println(memberId);
    System.out.println(newPassword);
    System.out.println(newPassword1);
    System.out.println(verificationCodeValid);

    if (!newPassword.equals(newPassword1)) {
      // 비밀번호 확인이 일치하지 않을 때
      model.addAttribute("verificationCode", verificationCode);
      model.addAttribute("verificationCodeValid", verificationCodeValid);
      model.addAttribute("newPassword", newPassword);
      model.addAttribute("newPassword1", newPassword1);
      model.addAttribute("memberId", memberId);
      return "find-password-form";
    }
    try {
      Member member = memberService.getMember(memberId);
      // 여기서 updatePassword 메서드를 호출할 때, 현재 비밀번호를 가진 Member 객체를 전달해야 합니다.
      Member updatedMember = memberService.updatePassword(member, newPassword);
      model.addAttribute("message", "비밀번호가 재설정되었습니다.");

      // 비밀번호 재설정이 성공했을 때 alert를 띄우고 페이지를 이동
      return "redirect:/member/findPasswordPhone?resetSuccess=true";
    } catch (DataNotFoundException e) {
      // 존재하지 않는 아이디에 대한 예외 처리
      model.addAttribute("message", "존재하지 않는 아이디입니다.");
      return "find-password-form";
    } catch (Exception e) {
      // 기타 예외 처리
      model.addAttribute("message", "비밀번호 업데이트에 실패했습니다.");
      return "find-password-form";


    }

  }
  // 폰으로 패스워드 찾기 메서드 시작입니다.총 3가지 메서드

  @GetMapping("/findPasswordPhone")
  public String findPasswordPhone(@RequestParam(value = "resetSuccess", required = false) String resetSuccess,
                                  Model model) {

    model.addAttribute("resetSuccess", resetSuccess);

    return "find-password-phone";
  }

  @PostMapping("/findPasswordPhone")//폰으로 비밀번호 찾기메서드 상당히 복잡하게 코드를짜서 좀 ㅠㅠ 변수명이 길어서 ㅠㅠ
  public String findPasswordPhone(@RequestParam("memberId") String memberId, Model model,
                                  @RequestParam(name = "inputVerificationCodePhone", defaultValue = "") String inputVerificationCodePhone,
                                  @RequestParam(name = "verificationCodeSentPhone", defaultValue = "false") boolean verificationCodeSentPhone,
                                  @RequestParam(name = "verificationCodePhone", defaultValue = "") String verificationCodePhone) {
    try {
      Member member = memberService.getMember(memberId);// 멤버서비스에서 아이디 정보가져오기

      model.addAttribute("memberId", memberId);//뷰로뿌려줘야하겠죠?

      // 첫 시도 -> 인증코드 보낸 적 없음
      if (!verificationCodeSentPhone) {
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
        return "find-password-phone";
      }

      boolean matched = verificationCodePhone.equals(inputVerificationCodePhone);

      if (matched) {
        model.addAttribute("verificationCodeValidPhone", true);//두번쨰 폼이 트루일때 조건
        model.addAttribute("userPhone", member.getPhone()); //폰 끌어와서 추가
      } else {
        model.addAttribute("message", "인증번호가 틀렸습니다.");
        model.addAttribute("verificationCodeValidPhone", false);
        model.addAttribute("verificationCodeSentPhone", true);
        model.addAttribute("verificationCodePhone", verificationCodePhone);
      }
      return "find-password-phone";
    } catch (DataNotFoundException e) {
      model.addAttribute("message", "존재하지 않는 아이디입니다.");
      model.addAttribute("memberId", memberId);
    }
    return "find-password-phone";
  }

  @PostMapping("/resendVerificationCodePhone")//폰인증번호 재전송 메서드
  private String resendVerificationCodePhone(Model model, @RequestParam("memberId") String memberId) {


    // 인증 코드 생성 (여기에서는 간단하게 난수로 생성)
    String verificationCodeSMS = String.valueOf((int) (Math.random() * 9000) + 1000);

    Member member = memberService.getMember(memberId);

    String userPhone = member.getPhone();


    smsService.sendMessage(userPhone, verificationCodeSMS);// 서비스에서 메세지 보내는거 끌어와주기
    model.addAttribute("verificationCodePhone", verificationCodeSMS);//인증코드
    model.addAttribute("verificationCodeSentPhone", true);//폼이 트루일때
    model.addAttribute("userPhone", userPhone); // 폰 정보를 모델에 추가
    model.addAttribute("memberId", memberId); //아이디정보를 모델에 추가


    model.addAttribute("showConfirmationScript", true);
    model.addAttribute("message", "인증번호 재전송 성공");

    return "find-password-phone";

  }

  @PostMapping("/passwordResetPhone")//폰으로찾기해서 비밀번호 재설정하는 메서드
  public String passwordResetPhone(@RequestParam("memberId") String memberId,
                                   @RequestParam("verificationCodePhone") String verificationCodePhone,
                                   @RequestParam("verificationCodeValidPhone") String verificationCodeValidPhone,
                                   @RequestParam("newPassword") String newPassword,
                                   @RequestParam("newPassword1") String newPassword1,
                                   Model model) {

    //실행테스트 출력코드 지워도 됩니다.
    System.out.println(memberId);
    System.out.println(newPassword);
    System.out.println(newPassword1);
    System.out.println(verificationCodeValidPhone);

    if (!newPassword.equals(newPassword1)) {
      // 비밀번호 확인이 일치하지 않을 때
      model.addAttribute("verificationCode", verificationCodePhone);
      model.addAttribute("verificationCodeValidPhone", verificationCodeValidPhone);
      model.addAttribute("newPassword", newPassword);
      model.addAttribute("newPassword1", newPassword1);
      model.addAttribute("memberId", memberId);
      return "find-password-phone";
    }
    try {
      Member member = memberService.getMember(memberId);
      // 여기서 updatePassword 메서드를 호출할 때, 현재 비밀번호를 가진 Member 객체를 전달해야 합니다.
      Member updatedMember = memberService.updatePassword(member, newPassword);
      model.addAttribute("message", "비밀번호가 재설정되었습니다.");

      // 비밀번호 재설정이 성공했을 때 alert를 띄우고 페이지를 이동
      return "redirect:/member/findPasswordPhone?resetSuccess=true";
    } catch (DataNotFoundException e) {
      // 존재하지 않는 아이디에 대한 예외 처리
      model.addAttribute("message", "존재하지 않는 아이디입니다.");
      return "find-password-phone";
    } catch (Exception e) {
      // 기타 예외 처리
      model.addAttribute("message", "비밀번호 업데이트에 실패했습니다.");
      return "find-password-phone";
    }
  }
}






