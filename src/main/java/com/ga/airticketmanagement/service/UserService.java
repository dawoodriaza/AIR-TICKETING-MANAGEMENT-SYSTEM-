package com.ga.airticketmanagement.service;

import com.ga.airticketmanagement.dto.mapper.PageMetaFactory;
import com.ga.airticketmanagement.dto.request.*;
import com.ga.airticketmanagement.dto.response.AuthenticatedUserResponse;
import com.ga.airticketmanagement.dto.response.ListResponse;
import com.ga.airticketmanagement.dto.response.PageMeta;
import com.ga.airticketmanagement.event.EmailPasswordResetEvent;
import com.ga.airticketmanagement.event.EmailVerificationRequestedEvent;
import com.ga.airticketmanagement.exception.*;
import com.ga.airticketmanagement.model.Role;
import com.ga.airticketmanagement.model.User;
import com.ga.airticketmanagement.dto.response.LoginResponse;
import com.ga.airticketmanagement.model.UserProfile;
import com.ga.airticketmanagement.model.token.TokenType;
import com.ga.airticketmanagement.model.token.UserToken;
import com.ga.airticketmanagement.repository.UserProfileRepository;
import com.ga.airticketmanagement.repository.UserRepository;
import com.ga.airticketmanagement.security.AuthenticatedUserProvider;
import com.ga.airticketmanagement.security.JWTUtils;
import com.ga.airticketmanagement.security.MyUserDetails;
import com.ga.airticketmanagement.specification.UserSpecification;
import com.ga.airticketmanagement.util.TokenGenerator;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@Service
public class UserService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final UserProfileRepository userProfileRepository;
    private MyUserDetails myUserDetails;
    private final UserTokenService userTokenService;

    public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder,
                       JWTUtils jwtUtils,
                       @Lazy AuthenticationManager authenticationManager,
                       @Lazy MyUserDetails myUserDetails,
                       UserTokenService userTokenService,
                       ApplicationEventPublisher applicationEventPublisher, AuthenticatedUserProvider authenticatedUserProvider, UserProfileRepository userProfileRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.myUserDetails = myUserDetails;
        this.userTokenService = userTokenService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public User createUser(User userObject) {
        log.debug("Calling createUser from the Service ==>");
        if (userObject.getEmailAddress() == null || userObject.getEmailAddress().isBlank()) {
            throw new ValidationException("Email address is required");
        }
        if (!userRepository.existsByEmailAddress(userObject.getEmailAddress())) {
            UserProfile userProfile = new UserProfile();
            userProfile.setUser(userObject);
            userObject.setUserProfile(userProfile);
            userObject.setRole(Role.CUSTOMER);
            userObject.setPassword(passwordEncoder.encode(userObject.getPassword()));
            userObject.setRole(Role.CUSTOMER);
            User newUser = userRepository.save(userObject);

            String newToken = TokenGenerator.generateToken();

            userTokenService.createToken(
                    newUser,
                    newToken,
                    TokenType.EMAIL_VERIFICATION,
                    Duration.ofHours(24)
            );

            applicationEventPublisher.publishEvent(
                    new EmailVerificationRequestedEvent(newUser, newToken)
            );

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

        ResponseCookie cookie = ResponseCookie.from("access_token",  JWT)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofHours(24))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponse(JWT, user.getId(), user.getRole(), "Login successful"));
    }

    public User findUserByEmailAddress(String email){
        return userRepository.findUserByEmailAddress(email).orElseThrow(
                () -> new ValidationException("Incorrect email or password.")
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

    @Transactional
    public void resendVerification(EmailVerificationRequest request) {
        userRepository.findUserByEmailAddress(request.email())
                .filter(user -> !user.isEmailVerified() && userTokenService.isExpired(request.email(), TokenType.EMAIL_VERIFICATION))
                .ifPresent(
                        user ->  {
                            String newToken = TokenGenerator.generateToken();
                            userTokenService.createToken(user, newToken, TokenType.EMAIL_VERIFICATION, Duration.ofHours(24));
                            applicationEventPublisher.publishEvent(new EmailVerificationRequestedEvent(user, newToken));
                        }
                );
    }

    @Transactional
    public void requestResetPassword(EmailPasswordResetRequest request) {
        userRepository.findUserByEmailAddress(request.email()).ifPresent( user -> {
                    String newToken = TokenGenerator.generateToken();
                    userTokenService.createToken(user, newToken, TokenType.PASSWORD_RESET, Duration.ofHours(24));
                    applicationEventPublisher.publishEvent(new EmailPasswordResetEvent(user, newToken));
                }
        );
    }

    @Transactional
    public void resetPasswordByToken(PasswordResetTokenRequest request) {

        UserToken userToken = userTokenService.validateToken(request.token(), TokenType.PASSWORD_RESET);

        if(!request.newPassword().equals(request.newPasswordConfirmation())){
            throw new ValidationException("Passwords do not match");
        }

        userRepository.findUserByEmailAddress(userToken.getEmail()).ifPresent(user -> {
            user.setPassword(passwordEncoder.encode(request.newPassword()));
            userRepository.save(user);
            userTokenService.markUsed(userToken);
        });
    }

    @Transactional
    public void resetPassword(PasswordResetRequest request) {

        User user = authenticatedUserProvider.getAuthenticatedUser();

        if(!passwordEncoder.matches(request.oldPassword(), user.getPassword())){
            throw new ValidationException("Invalid old password");
        }

        if(!request.newPassword().equals(request.newPasswordConfirmation())){
            throw new ValidationException("Passwords do not match");
        }
        
        if(request.oldPassword().equals(request.newPasswordConfirmation())){
            throw new ValidationException("New password must not be the same as your old password");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    public AuthenticatedUserResponse getCurrentUser() {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        UserProfile profile = user.getUserProfile();

        // add profile to existing user without profile
        if(profile == null){
            UserProfile userProfile = new UserProfile();
            userProfile.setUser(user);
            user.setUserProfile(userProfile);
            userRepository.save(user);
        }

        return new AuthenticatedUserResponse(
                user.getId(),
                user.getRole(),
                user.isEmailVerified(),
                user.getEmailAddress(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getProfileImg()
            );
    }

    public void logout(HttpServletResponse response) {
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
    }

    @Transactional
    public User softDeleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InformationNotFoundException("User not found with id: " + userId));

        user.setActive(false);

        return userRepository.save(user);
    }

    /**
     * Reactivate a soft-deleted user
     */
    @Transactional
    public User reactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InformationNotFoundException("User not found with id: " + userId));

        user.setActive(true);

        return userRepository.save(user);
    }

    public ListResponse<User> searchUsers(Long id, String email, String search, Pageable pageable) {
        Specification<User> spec;
        
        boolean hasSpecificCriteria = id != null || 
            (email != null && !email.trim().isEmpty());
        
        if (hasSpecificCriteria) {
            spec = UserSpecification.withSearchCriteria(id, email);
            if (search != null && !search.trim().isEmpty()) {
                spec = spec.and(UserSpecification.withGeneralSearch(search));
            }
        } else if (search != null && !search.trim().isEmpty()) {
            spec = UserSpecification.withGeneralSearch(search);
        } else {
            spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
        
        Page<User> page = userRepository.findAll(spec, pageable);

        PageMeta meta = PageMetaFactory.from(page);

        return new ListResponse<>(page.getContent(), meta);
    }

}
