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
  public String sendVerificationCodeSMS(String phone) {
    // 인증 코드 생성 (여기에서는 간단하게 난수로 생성)
    String verificationCodeSMS = String.valueOf((int) (Math.random() * 9000) + 1000);

    // 이 부분을 변경하여 콘솔에 출력하거나 로깅할 수 있습니다.
    System.out.println("Verification code for phone number " + phone + ": " + verificationCodeSMS);

    // 이메일 전송 로직은 생략합니다.
    return verificationCodeSMS;
  }
}
