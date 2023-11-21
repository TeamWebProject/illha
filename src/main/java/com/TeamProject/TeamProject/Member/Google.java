package com.TeamProject.TeamProject.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Google {

      @Id
      private String googleId; // 구글 로그인 시 사용되는 ID

      private String email;
      private String nickname;

      // 생성자, 게터, 세터 등은 필요에 따라 추가
}