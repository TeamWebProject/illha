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
            .oauth2Login((oauth2Login) -> oauth2Login
                    .loginPage("/member/login")
                    .userInfoEndpoint(userInfoEndpoint ->
                            userInfoEndpoint
                                    .userService(socialOAuth2UserService))
            );

    return http.build();
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
