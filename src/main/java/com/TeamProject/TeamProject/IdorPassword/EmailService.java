package com.TeamProject.TeamProject.IdorPassword;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {


  @Autowired
  private JavaMailSender javaMailSender;


  public String sendVerificationCode(String toEmail, String verificationCode) {
    System.out.println("Verification code: " + verificationCode); // 로그로 출력
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(toEmail);
    message.setSubject("이메일 인증번호");
    message.setText("임시 인증번호: " + verificationCode);
    javaMailSender.send(message);
    return verificationCode;
  }
}
