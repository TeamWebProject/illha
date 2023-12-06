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
      private String email; // 구글에서 가져올수있는 엔티티가 한정적이므로 이정도가 최선이였습니다.
      private String nickname;

      // 생성자, 게터, 세터 등은 필요에 따라 추가
}