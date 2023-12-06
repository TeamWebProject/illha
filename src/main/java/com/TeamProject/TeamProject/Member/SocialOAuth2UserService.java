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
// 이클래스는 무조건 OAuth2로그인방식으로 써야하므로 제일 중요한 클래스라고해도 과언이아닙니다.
  // 이곳에 구글,카카오,네이버 모든 서비스정보를 끌어와서 생성자를통해 의존성을 추가해주었습니다.
          private final GoogleService googleService;
          private final KakaoService kakaoService;
          private final NaverService naverMemberService;


          @Autowired
          public SocialOAuth2UserService(GoogleService googleService, KakaoService kakaoService
                  , NaverService naverMemberService) {
            this.googleService = googleService;
            this.kakaoService = kakaoService;
            this.naverMemberService = naverMemberService;
            //위에서말씀드린 그대로구요.
          }


          // 이메서드가 제일필요하고중요한부분중하나입니다. 사용자정보 끌어와서 DB에담아야하기ㄸ때문에 꼭중요하고 매서드명은
          // 고정적으로 이렇게 가져가야합니다. 크게 수정하지않고 이대로사용해서 바로사용가능할것입니다.
          // 굳이 소셜로그인 엔티티를 수정하지않는이상 기능구현에있어서 큰 오류는없을겁니다.
          @Override
          public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
            System.out.println("asdasd");// 제가 이 메서드가 실행되는지 출력문을통해서 확인한거기에 이출력문은 생략하셔도됩니다//
            OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

            String clientId = userRequest.getClientRegistration().getClientId();
            String clientName = userRequest.getClientRegistration().getClientName();

            OAuth2User oAuth2User = delegate.loadUser(userRequest);


            if (clientName.equals("kakao")) {
              System.out.println("kakao");
            } else if (clientName.equals("Google")) {
              System.out.println("google");
            }


           // 여기서부턴 IF문과 ELSE IF문을 활용해서 제가 코드를짜서 구글,카카오,네이버로그인시 사용자 정보를 끌어서
            // 가져오기위해서  각엔티티별로 이렇게 정보를추출해서 DB에담아지게 하였습니다 .각 소셜사이트마다 엔티티도다르고,
            // API도 상이한부분이있기때문에 조금차이가 있을것입니다 네이버와 카카오같은경우는 Map<String, Object> attributes
            //각 소셜 로그인 서비스(provider)는 사용자 정보를 다양한 속성으로 제공합니다.
            // Kakao는 id, kakao_account.email, nickname 등을 제공하고, Naver는 id, response.email, response.nickname, response.name 등 서로상이함.
            // 이러한 다양한 속성을 효과적으로 다루기 위해 맵을 사용했고 구글은 속성자체가 꽤나단순해서 이렇게해줬습니다.
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
              //아마 여기서 맵해쉬를쓴부분은 디버그를사용해서 어디에 엔티티정보가 담겨져있는지 확인후에 NAME이름을 정해줘야하는데
              // 프로퍼티스에담겨있는걸확인해서 네임이름은 저걸로꼭해줘야합니다.
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
