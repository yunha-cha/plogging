package com.website.user;

import com.website.entity.User;
import com.website.repository.UserRepository;
import com.website.user.dto.SignupRequestDto;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * 회원 가입 메서드
     * @param req 회원 가입 필요 정보
     * @return 클라이언트에 return할 메세지
     */
    public String signup(SignupRequestDto req) {
        if(req.getUserId()==null || req.getNickname()==null || req.getPassword()==null){
            throw new IllegalArgumentException("입력이 비어있습니다.");
        }
        if(userRepository.existsByUserId(req.getUserId())){
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if(userRepository.existsByNickname(req.getNickname())){
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        User newUser = new User(
                req.getUserId(),
                req.getNickname(),
                bCryptPasswordEncoder.encode(req.getPassword()),
                "ROLE_USER",    //기본 권한 일반 유저로 설정
                LocalDateTime.now(),
                true    //기본 유저 로그인 가능 활성화 여부
        );
        userRepository.save(newUser);
        return "회원가입에 성공했습니다!";
    }
}
