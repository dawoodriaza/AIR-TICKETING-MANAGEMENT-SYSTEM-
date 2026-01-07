package com.ga.airticketmanagement.service;

import com.ga.airticketmanagement.model.User;
import com.ga.airticketmanagement.model.token.TokenType;
import com.ga.airticketmanagement.model.token.UserToken;
import com.ga.airticketmanagement.repository.UserTokenRepository;
import com.ga.airticketmanagement.util.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class UserTokenService {

    private UserTokenRepository userTokenRepository;

    @Autowired
    public void setUserTokenRepository(UserTokenRepository userTokenRepository) {
        this.userTokenRepository = userTokenRepository;
    }

    public UserToken createToken(User user, TokenType type, Duration ttl) {
        UserToken userToken = new UserToken();
        userToken.setType(type);
        userToken.setToken(TokenGenerator.generateToken());
        userToken.setEmail(user.getEmailAddress());
        userToken.setExpiresAt(LocalDateTime.now().plus(ttl));

        return userTokenRepository.save(userToken);
    }

    public UserToken validateToken(String token, TokenType type) {

        UserToken userToken = userTokenRepository.findByTokenAndType(token, type)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if(userToken.getUsedAt() != null){
            throw new RuntimeException("Token is already used");
        }

        if(userToken.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Token is expired");
        }

        return userToken;
    }

    public void markUsed(UserToken userToken){
        userToken.setUsedAt(LocalDateTime.now());
        userTokenRepository.save(userToken);
    }
}
