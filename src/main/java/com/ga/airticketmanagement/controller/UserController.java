package com.ga.airticketmanagement.controller;

import com.ga.airticketmanagement.dto.request.*;
import com.ga.airticketmanagement.dto.response.ForgotPasswordResponse;
import com.ga.airticketmanagement.dto.response.ResetPasswordByTokenResponse;
import com.ga.airticketmanagement.dto.response.ResetPasswordResponse;
import com.ga.airticketmanagement.model.User;
import com.ga.airticketmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/auth/users")
public class UserController {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/register")
    public User createUser(@RequestBody User userObject){
        System.out.println("Calling the createUser from the controller ==>");
        return userService.createUser(userObject);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest){
        System.out.println("Calling LoginUser from the controller ==>");
        return userService.loginUser(loginRequest);

    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam String token) {

        userService.verifyUser(token);
        return ResponseEntity.ok("Account verified");
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

    @PostMapping("/reset-password")
    public ResetPasswordResponse resetPasword(@RequestBody PasswordResetRequest request) {
        userService.resetPassword(request);
        return new ResetPasswordResponse("Password reset successful.");
    }


}
