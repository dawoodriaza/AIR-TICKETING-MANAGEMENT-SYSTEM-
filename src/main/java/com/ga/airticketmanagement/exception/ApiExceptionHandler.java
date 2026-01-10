package com.ga.airticketmanagement.exceptions;

import com.ga.airticketmanagement.dto.response.ApiErrorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @Value("${app.frontend.base-url}")
    private String baseUrl;

    @ExceptionHandler(EmailUsedException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailUsedException(EmailUsedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ApiErrorResponse("EMAIL_ALREADY_USED", ex.getMessage())
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiErrorResponse("INVALID_CREDENTIALS", e.getMessage()));
    }

    @ExceptionHandler(AccountNotVerifiedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountNotVerifiedException() {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ApiErrorResponse(
                        "ACCOUNT_NOT_VERIFIED",
                        "Please verify your email to be able to log in."));
    }

    @ExceptionHandler(ExpiredVerificationTokenException.class)
    public ResponseEntity<ApiErrorResponse> handleExpiredVerificationTokenException(ExpiredVerificationTokenException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiErrorResponse(
                "VERIFICATION_TOKEN_EXPIRED",
                "Request resend verification email.",
                Map.of("resend_verification", baseUrl + "/auth/users/resend-verification"))
        );
    }
}
