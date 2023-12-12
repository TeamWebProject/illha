package com.TeamProject.TeamProject.Member;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FindIdParam {
  private String verificationCode;
  private String email;
  private String phone;
  private Boolean verificationCodeForm;
  private Boolean verificationCodeMismatch;
  private Boolean sendAlert;
  private List<Member> members;
  private String errorMessage;
}
