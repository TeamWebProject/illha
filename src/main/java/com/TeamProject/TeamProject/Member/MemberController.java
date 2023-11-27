package com.TeamProject.TeamProject.Member;


import com.TeamProject.TeamProject.DataNotFoundException;
import com.TeamProject.TeamProject.IdorPassword.EmailService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
  private EmailService emailService;

  @GetMapping("/signup")
  public String signup(MemberCreateForm memberCreateForm) {
    return "signup_form";
  }

  @PostMapping("/signup")
  public String signup(@Valid MemberCreateForm memberCreateForm, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "signup_form";
    }
    if (!memberCreateForm.getPassword1().equals(memberCreateForm.getPassword2())) {
      bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");

      return "redirect:/";
    }
    try {
      this.memberService.create(memberCreateForm.getMemberId(), memberCreateForm.getPassword1(), memberCreateForm.getNickname(), memberCreateForm.getEmail());
    } catch (DataIntegrityViolationException e) {
      e.printStackTrace();
      bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
      return "signup_form";
    } catch (Exception e) {
      e.printStackTrace();
      bindingResult.reject("signupFailed", e.getMessage());
      return "signup_form";
    }
    return "redirect:/";
  }

  @GetMapping("/login")
  private String login() {
    return "login_form";
  }

  @GetMapping("/findId")
  public String findId() {
    return "find-id-form";
  }

  @PostMapping("/findId")
  public String findId(@RequestParam("verificationCode") String verificationCode, HttpSession session,
                       Model model) {
    // 세션에서 저장된 이메일 가져오기
    String userEmail = (String) session.getAttribute("userEmail");
    String storedVerificationCode = (String) session.getAttribute("verificationCode");

    // ... (이전 코드 생략)

    if (verificationCode.equals(storedVerificationCode)) {
      // 인증 코드 일치: 아이디 찾기 기능 실행
      String foundId = memberService.findIdByEmail(userEmail).toString();
      if (foundId != null) {
        // 아이디를 찾았을 경우
        model.addAttribute("foundId", foundId);
      } else {
        // 아이디를 찾지 못한 경우
        model.addAttribute("notFound", true);
      }
    } else {
      // 인증 코드 불일치 처리 (예: 에러 메시지 전달)
      model.addAttribute("verificationCodeMismatch", true);
    }

    return "find-id-form";
  }


  @PostMapping("/sendVerificationCode")
  public String sendVerificationCode(@RequestParam("email") String email, Model model, HttpSession session) {

    // 인증 코드 생성 (여기에서는 간단하게 난수로 생성)
    String verificationCode = String.valueOf((int) (Math.random() * 9000) + 1000);

    // 이메일로 인증 코드 전송
    emailService.sendVerificationCode(email, verificationCode);

    // 세션에 이메일과 인증 코드 저장
    session.setAttribute("userEmail", email);
    session.setAttribute("verificationCode", verificationCode);

    // 모델에 인증 코드 저장 (후에 확인을 위해)
    model.addAttribute("verificationCode", verificationCode);
    model.addAttribute("email", email);
    // 인증 코드 입력 폼을 보여주기 위해 "verificationCodeForm" 속성 추가
    model.addAttribute("verificationCodeForm", true);

    // 이메일 입력 폼으로 리다이렉트
    return "find-id-form";
  }


  @GetMapping("/findPassword")
  public String findPassword(@RequestParam(value = "resetSuccess",required = false)String resetSuccess,
                             Model model) {

    model.addAttribute("resetSuccess", resetSuccess);

    return "find-password-form";
  }

  @PostMapping("/findPassword")
  public String findPassword(@RequestParam("memberId") String memberId, Model model,
                             @RequestParam(name = "inputVerificationCode", defaultValue = "") String inputVerificationCode,
                             @RequestParam(name = "verificationCodeSent", defaultValue = "false") boolean verificationCodeSent,
                             @RequestParam(name = "verificationCode", defaultValue = "") String verificationCode,
                             HttpSession session) {

    try {
      Member member = memberService.getMember(memberId);
      String temporaryPassword = memberService.generateTemporaryPassword();
      memberService.updateTemporayPassword(member, temporaryPassword);

      // 첫 시도 -> 인증코드 보낸 적 없음
      if (!verificationCodeSent) {
        String tmp = emailService.sendVerificationCode(member.getEmail(), temporaryPassword);
        model.addAttribute("verificationCode", tmp);
        model.addAttribute("verificationCodeSent", true);
        model.addAttribute("message", "임시비밀번호가 이메일로 전송되었습니다.");
        return "find-password-form";
      }

      boolean matched = verificationCode.equals(inputVerificationCode);

      if (matched) {
        model.addAttribute("verificationCodeValid", true);

      } else {
        model.addAttribute("message", "임시비밀번호가 틀렸습니다.");
        model.addAttribute("verificationCodeValid", false);
        model.addAttribute("verificationCodeSent", true);
        model.addAttribute("verificationCode", verificationCode);
      }

      return "find-password-form";

    } catch (DataNotFoundException e) {
      model.addAttribute("message", "존재하지 않는 아이디입니다.");
    }


    return "find-password-form";
  }


  @GetMapping("/passwordReset")
  private String passwordReset(Model model) {
    model.addAttribute("verificationCode", "");
    model.addAttribute("newPassword", "");
    model.addAttribute("message", ""); // 메시지 초기화
    return "find-password-form";
  }

  @PostMapping("/passwordReset")
  public String passwordReset(@RequestParam("memberId") String memberId,
                              @RequestParam("verificationCode") String verificationCode,
                              @RequestParam("newPassword") String newPassword,
                              Model model) {
    System.out.println(memberId);
    System.out.println(newPassword);
    try {
      Member member = memberService.getMember(memberId);
      // 여기서 updatePassword 메서드를 호출할 때, 현재 비밀번호를 가진 Member 객체를 전달해야 합니다.
      Member updatedMember = memberService.updatePassword(member, newPassword);
      model.addAttribute("message", "비밀번호가 재설정되었습니다.");

      // 비밀번호 재설정이 성공했을 때 alert를 띄우고 페이지를 이동
      return "redirect:/member/findPassword?resetSuccess=true";
    } catch (DataNotFoundException e) {
      // 존재하지 않는 아이디에 대한 예외 처리
      model.addAttribute("message", "존재하지 않는 아이디입니다.");
      return "find-password-form";
    } catch (Exception e) {
      // 기타 예외 처리
      model.addAttribute("message", "비밀번호 업데이트에 실패했습니다.");
      return "find-password-form";


//    try {
//      // memberId가 누락되었을 때 예외 처리 추가
//      if (StringUtils.isBlank(memberId)) {
//        model.addAttribute("message", "아이디를 입력해주세요.");  // 클라이언트에게 메시지 전달
//        return "find-password-form";
//      }
//
//      Member member = memberService.getMember(memberId);
//      // 이미 인증번호가 올바르게 입력되었는지 확인
//      Boolean verificationCodeValid = (Boolean) session.getAttribute("verificationCodeValid");
//
//      if (verificationCodeValid) {
//        // 비밀번호 업데이트
//        memberService.updatePassword(member, newPassword);
//        model.addAttribute("newPassword", true); // 비밀번호 재설정 기능인 경우
//        model.addAttribute("message", "비밀번호가 성공적으로 변경되었습니다.");
//      } else {
//        model.addAttribute("message", "인증번호가 올바르지 않습니다.");
//      }
//    } catch (DataNotFoundException e) {
//      model.addAttribute("message", "존재하지 않는 아이디입니다.");
//    }
//
//
//    // 초기화된 상태의 폼을 보여주기 위해 passwordReset 메서드로 리다이렉트
//    return "redirect:/member/login";


    }

  }
}






