package com.TeamProject.TeamProject.SNS;

import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class SMSService {

  private String apiKey = "NCSENQ4RHKHSNKBO";

  private String apiSecret = "G52URTCEZ7TTGWETM0J4MCDJHGJFQPIM";

  public void sendMessage(String phoneNumber, String verificationCodeSMS) {
    Message coolsms = new Message(apiKey, apiSecret);
    HashMap<String, String> params = new HashMap();
    params.put("to", phoneNumber);
    params.put("from", "01083175517");
    params.put("type", "SMS");
    params.put("text", "[팀프로젝트] 인증번호는 " + "["+verificationCodeSMS+"]" + " 입니다.");

    try {
      JSONObject obj = (JSONObject) coolsms.send(params);
    } catch (CoolsmsException e) {
      e.printStackTrace();
    }
  }

}