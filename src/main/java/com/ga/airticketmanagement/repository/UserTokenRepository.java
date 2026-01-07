package com.ga.airticketmanagement.repository;

import com.ga.airticketmanagement.model.token.TokenType;
import com.ga.airticketmanagement.model.token.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken,Long> {
    Optional<UserToken> findByTokenAndType(String token, TokenType type);
}
