package com.TeamProject.TeamProject.Member;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
        private final SocialRepository socialRepository;
  @Autowired
  public UserService(SocialRepository socialRepository) {
    this.socialRepository = socialRepository;
    //코드에서 GoogleService가 GoogleRepository를 생성자를 통해 주입받고있습니다.
  }
  public User saveOrUpdateGoogle(String googleId, String email, String nickname) {
    // 구글 로그인 정보를 저장 또는 업데이트
    User user = socialRepository.findById(googleId)
            .orElse(new User());
    //엔티티정보를 다끌어서 저장후에 업데이트하기위해서 이렇게 했습니다.
    user.setSocialId(googleId);
    user.setEmail(email);
    user.setNickname(nickname);
    return socialRepository.save(user);
  }
  public User saveOrUpdateKakao(String kakaoId, String email, String nickname) {

    // Kakao 엔티티를 생성하거나 기존 정보를 업데이트합니다.
    User user = socialRepository.findById(kakaoId).orElse(new Kakao());
    user.setSocialId(kakaoId);
    user.setNickname(nickname);

    // Kakao 엔티티를 저장합니다.

    return socialRepository.save(user);
  }
  public User saveOrUpdateNaver(String naverId, String email, String nickname, String name) {
    // 네이버 로그인 정보를 저장 또는 업데이트
    User user = socialRepository.findById(naverId)
            .orElse(new Naver());

    user.setSocialId(naverId);
    user.setEmail(email);
    user.setNickname(nickname);
    user.setName(name);
    return socialRepository.save(user);
  }

}
