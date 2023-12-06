package com.TeamProject.TeamProject.Member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GoogleRepository extends JpaRepository<Google, String> {
  // 래파지토리엔 기본ID값을 스트링으로 받아오기때문에 메서드추가할필요가 없었습니다.
  // 그래서 아이디를 서비스메서드에서 찾아올수있어서 메서드를 여기선 추가안했습니다.

}
