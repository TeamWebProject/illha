package com.TeamProject.TeamProject.Member;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FindIDPhoneParam {
  private String verificationCodeSMS;
  private String userPhone;
  private Boolean verificationCodeFormSMS;
  private Boolean verificationCodeMismatchSMS;
  private Boolean sendAlert;
  private List<Member> members;
  private String errorMessage1;

}
