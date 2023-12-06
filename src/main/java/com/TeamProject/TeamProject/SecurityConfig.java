package com.TeamProject.TeamProject;

import com.TeamProject.TeamProject.Member.SocialOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Autowired
  private SocialOAuth2UserService socialOAuth2UserService;



  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                    .requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
            .headers((headers) -> headers
                    .addHeaderWriter(new XFrameOptionsHeaderWriter(
                            XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
            .formLogin((formLogin) -> formLogin
                    .loginPage("/member/login")
                    .defaultSuccessUrl("/"))
            .logout((logout) -> logout
                    .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                    .logoutSuccessUrl("/member/login") // 변경된 부분: 로그아웃 시 로그인 페이지로 이동
                    .invalidateHttpSession(true)
                    .addLogoutHandler((request, response, authentication) -> {
                      // 추가적인 로그아웃 처리를 수행할 수 있음

                      // 여기서 특별한 처리가 필요하지 않으면 추가 코드 없이 진행
                    })
            )
            // 서큐리티도 중요함!. 아웃로그인방식을쓰기에 이렇게해야함! 고정이라고생각하면됨.
            .oauth2Login((oauth2Login) -> oauth2Login //OAuth2 로그인을 설정하는 메서드
                    .loginPage("/member/login")
                    .userInfoEndpoint(userInfoEndpoint ->//OAuth2 로그인이 성공한 후에 호출되는 사용자 정보 엔드포인트를 설정하는 메서드입니다.
                            userInfoEndpoint//ocialOAuth2UserService를 사용하여 사용자 정보를 처리하도록 설정합니다.
                                    .userService(socialOAuth2UserService))
            );

    return http.build();//이것도 고정해야함.
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }


}
