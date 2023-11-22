package com.TeamProject.TeamProject.Member;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

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
                this.memberService.create(memberCreateForm.getMemberId(), memberCreateForm.getPassword1(), memberCreateForm.getNickname() , memberCreateForm.getEmail());
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
        private String login(){
          return "login_form";
        }

        @GetMapping("/findId")
        public String findId(){
            return "find-id-form";
        }


}
