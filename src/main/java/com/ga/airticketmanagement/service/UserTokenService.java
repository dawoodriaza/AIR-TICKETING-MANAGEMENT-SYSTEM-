package com.ga.airticketmanagement.service;

import com.ga.airticketmanagement.exception.ExpiredVerificationTokenException;
import com.ga.airticketmanagement.exception.InformationNotFoundException;
import com.ga.airticketmanagement.exception.ValidationException;
import com.ga.airticketmanagement.model.User;
import com.ga.airticketmanagement.model.token.TokenType;
import com.ga.airticketmanagement.model.token.UserToken;
import com.ga.airticketmanagement.repository.UserTokenRepository;
import com.ga.airticketmanagement.util.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public UserToken createToken(User user, String token, TokenType type, Duration ttl) {
        invalidateToken(user, type);

        UserToken userToken = new UserToken();
        userToken.setType(type);
        userToken.setToken(token);
        userToken.setEmail(user.getEmailAddress());
        userToken.setExpiresAt(LocalDateTime.now().plus(ttl));

        return userTokenRepository.save(userToken);
    }

    public void invalidateToken(User user, TokenType type){
        Optional<UserToken> oldToken = userTokenRepository.findByEmailAndType(user.getEmailAddress(), type);
        oldToken.ifPresent(t -> userTokenRepository.delete(t));
    }

    public UserToken validateToken(String token, TokenType type) {

        String hashedToken = TokenGenerator.hash(token);
        UserToken userToken = userTokenRepository.findByTokenAndType(hashedToken, type)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if(userToken.getUsedAt() != null){
            throw new ValidationException("Invalid Token");
        }

        if(userToken.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new ExpiredVerificationTokenException();
        }

        return userToken;
    }

    public boolean isExpired(String email, TokenType type) {
        UserToken userToken = userTokenRepository.findFirstByEmailAndTypeOrderByCreatedAtDesc(email, type)
                .orElseThrow(() -> new InformationNotFoundException("Not Found"));
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
