package com.TeamProject.TeamProject.Member;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberSecurityService implements UserDetailsService{

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String membername) throws UsernameNotFoundException {
    Optional<Member> _member = this.memberRepository.findBymembername(membername);
    if (_member.isEmpty()) {
      throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
    }
    Member member = _member.get();
    List<GrantedAuthority> authorities = new ArrayList<>();
    if ("admin".equals(membername)) {
      authorities.add(new SimpleGrantedAuthority(MemberRole.ADMIN.getValue()));
    } else {
      authorities.add(new SimpleGrantedAuthority(MemberRole.MEMBER.getValue()));
    }
    return new User(member.getMembername(), member.getPassword(), authorities);
  }
}
