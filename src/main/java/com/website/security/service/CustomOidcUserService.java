package com.website.security.service;

import com.website.entity.User;
import com.website.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomOidcUserService extends OidcUserService {
    private final UserRepository userRepository;

    public CustomOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

        // Spring의 기본 OIDC 처리 호출
        OidcUser oidcUser = super.loadUser(userRequest);
        Map<String, Object> attributes = oidcUser.getAttributes();
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String oauthId;
        String email;
        String nickname;
        String profileImage = null;

        // Provider별 사용자 정보 추출
        if ("google".equals(registrationId)) {
            oauthId = (String) attributes.get("sub");
            email   = (String) attributes.get("email");
            nickname     = (String) attributes.get("name");
            profileImage = (String) attributes.get("picture");
        } else if ("kakao".equals(registrationId)) {
            oauthId = String.valueOf(attributes.get("id"));
            var kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            email = (String) kakaoAccount.get("email");
            var props = (Map<String, Object>) attributes.get("properties");
            nickname     = (String) props.get("nickname");
            profileImage = (String) props.get("profile_image");
        } else {
            throw new OAuth2AuthenticationException(
                    "Unsupported OAuth provider: " + registrationId
            );
        }

        // DB에 기존 계정이 있는지 조회
        Optional<User> optionalUser = userRepository
                .findByOauthProviderAndOauthId(registrationId, oauthId);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            user.setLastLoginTime(LocalDateTime.now());
            user.setEmail(email);
            if (profileImage != null) {
                user.setProfileImage(profileImage);
            }
        } else {
            // 신규 소셜 계정 생성
            user = new User();
            user.setOauthProvider(registrationId);
            user.setOauthId(oauthId);
            user.setEmail(email);
            user.setName(nickname);
            user.setRole("ROLE_USER");
            user.setCreateAt(LocalDateTime.now());
            user.setEnable(true);
            user.setProfileImage(profileImage);
        }
        userRepository.save(user);

        // nameAttributeKey 가져오기 (예: "sub")
        String nameAttributeKey = userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // 권한 설정
        Collection<GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));

        // DefaultOidcUser 생성 시 올바른 nameAttributeKey 전달
        return new DefaultOidcUser(
                authorities,
                oidcUser.getIdToken(),       // ID 토큰
                oidcUser.getUserInfo(),      // UserInfo
                nameAttributeKey             // 올바른 키("sub")
        );
    }
}
