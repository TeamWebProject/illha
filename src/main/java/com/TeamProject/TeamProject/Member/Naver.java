package com.TeamProject.TeamProject.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Naver {
    @Id
    private String naverId;

    private String email;
    private String nickname;
    private String name;

}



