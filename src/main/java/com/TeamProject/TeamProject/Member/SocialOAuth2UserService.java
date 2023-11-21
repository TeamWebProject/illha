package com.TeamProject.TeamProject.Member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SocialOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final GoogleService googleService;
  private final KakaoService kakaoService;
  private final NaverService naverMemberService;


  @Autowired
  public SocialOAuth2UserService(GoogleService googleService, KakaoService kakaoService
          , NaverService naverMemberService) {
    this.googleService = googleService;
    this.kakaoService = kakaoService;
    this.naverMemberService = naverMemberService;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    System.out.println("asdasd");
    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

    String clientId = userRequest.getClientRegistration().getClientId();
    String clientName = userRequest.getClientRegistration().getClientName();

    OAuth2User oAuth2User = delegate.loadUser(userRequest);


    if (clientName.equals("kakao")) {
      System.out.println("kakao");
    } else if (clientName.equals("Google")) {
      System.out.println("google");
    }


    // Extract relevant information from oAuth2User
// Extract relevant information from oAuth2User
    System.out.println("User Authorities: " + oAuth2User.getAuthorities());
    if (clientName.equals("Google")) {
      // Google 로그인 사용자 정보 추출
      String googleId = oAuth2User.getAttribute("sub");
      String email = oAuth2User.getAttribute("email");
      String nickname = oAuth2User.getAttribute("name");

      // Save or update Google information in the database
      googleService.saveOrUpdateGoogle(googleId, email, nickname);
      System.out.println("Google User Saved: " + googleId + ", " + email + ", " + nickname);

    } else if (clientName.equals("kakao")) {
      // Kakao 로그인 사용자 정보 추출

      Long kakaoId = oAuth2User.getAttribute("id");
      Map<String, Object> attributes = oAuth2User.getAttribute("properties");
      String email = (String)attributes.get("kakao_account.email");
      String nickname = (String) attributes.get("nickname");

      // Save or update Kakao information in the database
      kakaoService.saveOrUpdateKakao(String.valueOf(kakaoId), email, nickname);
      // Save or update Kakao information in the database
      System.out.println("Kakao User Saved: " + kakaoId + ", " + email + ", " + nickname);

    } else if (clientName.equals("Naver")) {
      // 네이버 로그인 사용자 정보 추출
      Long naverId = oAuth2User.getAttribute("id");

      Map<String, Object> attributes = oAuth2User.getAttribute("response");
      String email = (String)attributes.get("email");
      String nickname = (String)attributes.get("nickname");
      String name = (String)attributes.get("name");

      // Save or update Naver information in the database
      naverMemberService.saveOrUpdateNaver(String.valueOf(naverId), email, nickname,name);
      System.out.println("Naver User Saved: " + naverId + ", " + email + ", " + nickname + ","+ name);

    }
    return oAuth2User;
  }
}
