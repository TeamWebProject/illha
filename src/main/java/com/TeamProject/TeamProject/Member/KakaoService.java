package com.TeamProject.TeamProject.Member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KakaoService {
  private final KakaoRepository kakaoRepository;

  @Autowired
  public KakaoService(KakaoRepository kakaoRepository) {
    this.kakaoRepository = kakaoRepository;
  }


  public void saveOrUpdateKakao(String kakaoId, String email, String nickname) {

    // Kakao 엔티티를 생성하거나 기존 정보를 업데이트합니다.
    Kakao kakao = kakaoRepository.findById(kakaoId).orElse(new Kakao());
    kakao.setKakaoId(kakaoId);
    kakao.setNickname(nickname);

    // Kakao 엔티티를 저장합니다.
    kakaoRepository.save(kakao);

  }
}
