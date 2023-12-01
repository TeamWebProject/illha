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
import org.springframework.web.bind.annotation.*;

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
  private String login(Model model, @RequestParam(defaultValue = "false") boolean modal) {

    model.addAttribute("modal", modal);
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
  public String findPassword(@RequestParam(value = "resetSuccess", required = false) String resetSuccess,
                             Model model) {

    model.addAttribute("resetSuccess", resetSuccess);

    return "find-password-form";
  }

  @PostMapping("/findPassword")
  public String findPassword(@RequestParam("memberId") String memberId, Model model,
                             @RequestParam(name = "inputVerificationCode", defaultValue = "") String inputVerificationCode,
                             @RequestParam(name = "verificationCodeSent", defaultValue = "false") boolean verificationCodeSent,
                             @RequestParam(name = "verificationCode", defaultValue = "") String verificationCode,
                             @RequestParam("modal") boolean modal,
                             @RequestParam(name = "resendVerificationCode", defaultValue = "false") boolean resendVerificationCode,
                             HttpSession session) {


    try {
      Member member = memberService.getMember(memberId);

                  model.addAttribute("memberId", memberId);
          model.addAttribute("modal", true);

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
          return "login_form";
        }

      boolean matched = verificationCode.equals(inputVerificationCode);

      if (matched) {
        model.addAttribute("verificationCodeValid", true);
        model.addAttribute("userEmail", member.getEmail()); // 여기에 userEmail 추가


      } else {
        model.addAttribute("message", "인증번호가 틀렸습니다.");
        model.addAttribute("verificationCodeValid", false);
        model.addAttribute("verificationCodeSent", true);
        model.addAttribute("verificationCode", verificationCode);
      }


      return "login_form";

    } catch (DataNotFoundException e) {
      model.addAttribute("message", "존재하지 않는 아이디입니다.");
      model.addAttribute("memberId", memberId);
      model.addAttribute("modal", true);
    }

    return "login_form";


  }

  @PostMapping("/resendVerificationCode")
  private String resendVerificationCode(Model model, @RequestParam("memberId") String memberId) {

    Member member = memberService.getMember(memberId);
    String userEmail = member.getEmail();

    String temporaryPassword = memberService.generateTemporaryPassword();
    emailService.sendVerificationCode(userEmail, temporaryPassword);
    model.addAttribute("verificationCode", temporaryPassword);
    model.addAttribute("verificationCodeSent", true);
    model.addAttribute("userEmail", userEmail); // 이메일 정보를 모델에 추가
    model.addAttribute("memberId", memberId); // 이메일 정보를 모델에 추가
    model.addAttribute("modal", true); // 이메일 정보를 모델에 추가

    model.addAttribute("showConfirmationScript", true);
    model.addAttribute("message", "인증번호 재전송 성공");

    return "login_form";

  }


  @GetMapping("/passwordReset")
  private String passwordReset(Model model) {
    model.addAttribute("verificationCode", "");
    model.addAttribute("newPassword", "");
    model.addAttribute("newPassword1", "");
    model.addAttribute("message", ""); // 메시지 초기화
    return "find-password-form";
  }

  @PostMapping("/passwordReset")
  public String passwordReset(@RequestParam("memberId") String memberId,
                              @RequestParam("verificationCode") String verificationCode,
                              @RequestParam("verificationCodeValid") String verificationCodeValid,
                              @RequestParam("newPassword") String newPassword,
                              @RequestParam("newPassword1") String newPassword1,
                              @RequestParam("modal") boolean modal,
                              Model model) {
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
      model.addAttribute("modal", modal);
      return "login_form";
    }
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


    }

  }
}






