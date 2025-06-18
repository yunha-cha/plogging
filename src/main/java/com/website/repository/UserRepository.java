package com.website.repository;

import com.website.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUserId(String userId);

    boolean existsByNickname(String nickname);

    User findByUserId(String userId);
}
