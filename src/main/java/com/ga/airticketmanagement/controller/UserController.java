package com.ga.airticketmanagement.controller;

import com.ga.airticketmanagement.dto.request.*;
import com.ga.airticketmanagement.dto.response.AuthenticatedUserResponse;
import com.ga.airticketmanagement.dto.response.ForgotPasswordResponse;
import com.ga.airticketmanagement.dto.response.ResetPasswordByTokenResponse;
import com.ga.airticketmanagement.dto.response.ResetPasswordResponse;
import com.ga.airticketmanagement.model.User;
import com.ga.airticketmanagement.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/auth/users")
public class UserController {

    private UserService userService;
    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/register")
    public User createUser(@RequestBody User userObject){
        log.debug("Calling createUser from the controller ==>");
        return userService.createUser(userObject);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest, HttpServletRequest response){
        log.debug("Calling LoginUser from the controller ==>");
        return userService.loginUser(loginRequest);

    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam String token) {

        userService.verifyUser(token);
        URI redirectUri = URI.create(frontendBaseUrl + "/login?verified=true");
        return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody EmailVerificationRequest request) {

        userService.resendVerification(request);
        return ResponseEntity.ok("If you are registered, the verification email has been sent.");
    }

    @PostMapping("/forgot-password")
    public ForgotPasswordResponse requestResetPassword(@RequestBody EmailPasswordResetRequest request) {

        userService.requestResetPassword(request);
        return new ForgotPasswordResponse("If your email exists, an email has been sent to reset your password.");
    }

    @PostMapping("/reset-password/token")
    public ResetPasswordByTokenResponse resetPasswordByToken(@RequestBody PasswordResetTokenRequest request) {
        userService.resetPasswordByToken(request);
        return new ResetPasswordByTokenResponse("Password reset successful.");
    }

    @PatchMapping("/change-password")
    public ResetPasswordResponse resetPasword(@RequestBody PasswordResetRequest request) {
        userService.resetPassword(request);
        return new ResetPasswordResponse("Password change successful.");
    }

    @GetMapping("/me")
    public AuthenticatedUserResponse getCurrentUser() {

        return userService.getCurrentUser();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser(HttpServletResponse response) {
        userService.logout(response);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> softDeleteUser(@PathVariable Long userId) {
        User deletedUser = userService.softDeleteUser(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User deactivated successfully");
        response.put("userId", deletedUser.getId());
        response.put("email", deletedUser.getEmailAddress());
        response.put("active", deletedUser.isActive());

        return ResponseEntity.ok(response);
    }

    /**
     * Reactivate a soft-deleted user (set active back to true)
     * Only ADMIN can reactivate users
     */
    @PatchMapping("/{userId}/reactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> reactivateUser(@PathVariable Long userId) {
        User reactivatedUser = userService.reactivateUser(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User reactivated successfully");
        response.put("userId", reactivatedUser.getId());
        response.put("email", reactivatedUser.getEmailAddress());
        response.put("active", reactivatedUser.isActive());

        return ResponseEntity.ok(response);
    }


}
