package com.TeamProject.TeamProject.Member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)  // 중복 X
    private String memberId; //아이디

    @Column(unique = true)
    private String nickname;

    private String password; // 비번

    private String email;

    private String  resetPasswordToken;

    private LocalDateTime signUpDate;

}
