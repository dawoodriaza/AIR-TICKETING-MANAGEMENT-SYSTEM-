package com.ga.airticketmanagement.service;

import com.ga.airticketmanagement.exceptions.ExpiredVerificationTokenException;
import com.ga.airticketmanagement.model.User;
import com.ga.airticketmanagement.model.token.TokenType;
import com.ga.airticketmanagement.model.token.UserToken;
import com.ga.airticketmanagement.repository.UserTokenRepository;
import com.ga.airticketmanagement.util.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserTokenService {

    private UserTokenRepository userTokenRepository;
    @Value("${app.frontend.base-url}")
    private String baseUrl;

    @Autowired
    public void setUserTokenRepository(UserTokenRepository userTokenRepository) {
        this.userTokenRepository = userTokenRepository;
    }

    public UserToken createToken(User user, TokenType type, Duration ttl) {
        Optional<UserToken> oldToken = userTokenRepository.findByTokenAndType(user.getEmailAddress(), type);
        oldToken.ifPresent(token -> userTokenRepository.delete(token));
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

        if(userToken.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new ExpiredVerificationTokenException();
        }

        return userToken;
    }

    public boolean isExpired(String email, TokenType type) {
        UserToken userToken = userTokenRepository.findFirstByEmailAndTypeOrderByCreatedAtDesc(email, type)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No tokens found"));
        return userToken.getExpiresAt().isBefore(LocalDateTime.now());
    }

    public void markUsed(UserToken userToken){
        userToken.setUsedAt(LocalDateTime.now());
        userTokenRepository.save(userToken);
    }

    public String getTokenLink(String email, TokenType type){
        UserToken userToken = userTokenRepository.findFirstByEmailAndTypeOrderByCreatedAtDesc(email, type)
                .orElseThrow(() -> new RuntimeException("No tokens found"));

        StringBuilder link = new StringBuilder(baseUrl);

        switch(type){
            case EMAIL_VERIFICATION ->
                  link.append("/auth/users/verify?token=" + userToken.getToken());
            case PASSWORD_RESET ->
                  link.append("/?token=" + userToken.getToken());
        }

        return link.toString();
    }

}
