package com.TeamProject.TeamProject.Member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoogleService {
  private final GoogleRepository googleRepository;

  @Autowired
  public GoogleService(GoogleRepository googleRepository) {
    this.googleRepository = googleRepository;
    //코드에서 GoogleService가 GoogleRepository를 생성자를 통해 주입받고있습니다.
  }

  public Google saveOrUpdateGoogle(String googleId, String email, String nickname) {
    // 구글 로그인 정보를 저장 또는 업데이트
    Google google = googleRepository.findById(googleId)
                .orElse(new Google());
    //엔티티정보를 다끌어서 저장후에 업데이트하기위해서 이렇게 했습니다.
                      google.setGoogleId(googleId);
                      google.setEmail(email);
                      google.setNickname(nickname);

                      return googleRepository.save(google);
  }
}
