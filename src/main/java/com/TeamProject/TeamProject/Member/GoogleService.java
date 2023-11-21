package com.TeamProject.TeamProject.Member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoogleService {
  private final GoogleRepository googleRepository;

  @Autowired
  public GoogleService(GoogleRepository googleRepository) {
    this.googleRepository = googleRepository;
  }

  public Google saveOrUpdateGoogle(String googleId, String email, String nickname) {
    // 구글 로그인 정보를 저장 또는 업데이트
    Google google = googleRepository.findById(googleId)
                .orElse(new Google());

                      google.setGoogleId(googleId);
                      google.setEmail(email);
                      google.setNickname(nickname);

                      return googleRepository.save(google);
  }
}
