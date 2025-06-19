package com.website.user.dto;

import lombok.*;

//회원가입 요청 때 사용할 dto 입니다.
@AllArgsConstructor
@NoArgsConstructor
@Getter@Setter
@ToString
public class SignupRequestDto {
    private String userId;
    private String nickname;
    private String password;
}
