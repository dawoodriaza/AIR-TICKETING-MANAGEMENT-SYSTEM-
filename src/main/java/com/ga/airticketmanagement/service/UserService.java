package com.ga.airticketmanagement.service;

import com.ga.airticketmanagement.event.EmailVerificationRequestedEvent;
import com.ga.airticketmanagement.exception.AccountNotVerifiedException;
import com.ga.airticketmanagement.exception.EmailUsedException;
import com.ga.airticketmanagement.exception.ExpiredVerificationTokenException;
import com.ga.airticketmanagement.exception.InvalidCredentialsException;
import com.ga.airticketmanagement.model.User;
import com.ga.airticketmanagement.dto.request.LoginRequest;
import com.ga.airticketmanagement.dto.response.LoginResponse;
import com.ga.airticketmanagement.model.token.TokenType;
import com.ga.airticketmanagement.model.token.UserToken;
import com.ga.airticketmanagement.repository.UserRepository;
import com.ga.airticketmanagement.security.JWTUtils;
import com.ga.airticketmanagement.security.MyUserDetails;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@Service
public class UserService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private MyUserDetails myUserDetails;
    private final UserTokenService userTokenService;

    public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder,
                       JWTUtils jwtUtils,
                       @Lazy AuthenticationManager authenticationManager,
                       @Lazy MyUserDetails myUserDetails,
                       UserTokenService userTokenService,
                       ApplicationEventPublisher applicationEventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.myUserDetails = myUserDetails;
        this.userTokenService = userTokenService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public User createUser(User userObject) {
        System.out.println("Calling createUser from the Service ==>");
        if (!userRepository.existsByEmailAddress(userObject.getEmailAddress())) {
            userObject.setPassword(passwordEncoder.encode(userObject.getPassword()));
            User newUser = userRepository.save(userObject);
            UserToken userToken = userTokenService.createToken(
                    newUser,
                    TokenType.EMAIL_VERIFICATION,
                    Duration.ofHours(24)
            );
            applicationEventPublisher.publishEvent(new EmailVerificationRequestedEvent(newUser, userToken.getToken()));
            return newUser;
        } else {
            throw new EmailUsedException("User with email address already exists");
        }
    }

    public ResponseEntity<?> loginUser(LoginRequest loginRequest) {

        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                        )
                    );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException();
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        myUserDetails = (MyUserDetails) authentication.getPrincipal();
        User user = myUserDetails.getUser();

        if (!user.isEmailVerified()) {

            boolean tokenExpired = userTokenService.isExpired(
                    myUserDetails.getUser().getEmailAddress(),
                    TokenType.EMAIL_VERIFICATION
                );

            if (tokenExpired) {
                throw new ExpiredVerificationTokenException();
            }

            throw new AccountNotVerifiedException();
        }

        final String JWT = jwtUtils.generateJwtToken(myUserDetails);
        return ResponseEntity.ok(new LoginResponse(JWT));
    }


    public User findUserByEmailAddress(String email){
        return userRepository.findUserByEmailAddress(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );
    }


    @Transactional
    public void verifyUser(String token) {

        UserToken userToken = userTokenService.validateToken(token, TokenType.EMAIL_VERIFICATION);
        userRepository.findUserByEmailAddress(userToken.getEmail()).ifPresent(
                user -> {
                    if(!user.isEmailVerified()){
                        user.setEmailVerified(true);
                        userRepository.save(user);
                    }
                }
        );
        userTokenService.markUsed(userToken);
    }

    public void resendVerification(String email) {

        userRepository.findUserByEmailAddress(email)
                .filter(user -> !user.isEmailVerified())
                .ifPresent(
                        user ->  userTokenService.createToken(user, TokenType.EMAIL_VERIFICATION, Duration.ofHours(24))
                );

    }

}
