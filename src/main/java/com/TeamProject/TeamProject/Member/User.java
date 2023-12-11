package com.TeamProject.TeamProject.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class User {
  @Id
  private String socialId;
  private String email;
  private String nickname;
  private String name;

}
