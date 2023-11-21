package com.TeamProject.TeamProject.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Kakao {

  @Id
  private String kakaoId;
  private String email;
  private String nickname;

  // getters and setters


}
