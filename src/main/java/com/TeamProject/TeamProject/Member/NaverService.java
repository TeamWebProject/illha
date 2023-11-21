package com.TeamProject.TeamProject.Member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NaverService {

      private final NaverRepository naverRepository;

      @Autowired
      public NaverService(NaverRepository naverRepository) {
        this.naverRepository = naverRepository;
      }

      public Naver saveOrUpdateNaver(String naverId, String email, String nickname, String name) {
        // 네이버 로그인 정보를 저장 또는 업데이트
        Naver naver = naverRepository.findById(naverId)
                .orElse(new Naver());

        naver.setNaverId(naverId);
        naver.setEmail(email);
        naver.setNickname(nickname);
        naver.setName(name);


        return naverRepository.save(naver);
      }

}
