package com.website.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Getter@Setter
public class User { //User 테이블
    @Id //PK설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Auto Increment 설정
    @Column(name = "user_code") //컬럼 이름 설정
    private Long userCode;  //이 프로젝트에서 user_code 컬럼을 쓸 이름 결정

    @Column(name = "userId", unique = true) //Unique Key 설정
    private String userId;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Column //없다면 필드 이름 그대로 사용
    private String password;

    @Column
    private String role;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    @Column(name = "profile_img")
    private String profileImage;

    @Column(name = "introduce")
    private String introduce;

    @Column
    private boolean enable;

    //회원 가입용 생성자
    public User(String userId, String nickname, String password, String role, LocalDateTime createAt, boolean enable) {
        this.userId = userId;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
        this.createAt = createAt;
        this.enable = enable;
    }
}
