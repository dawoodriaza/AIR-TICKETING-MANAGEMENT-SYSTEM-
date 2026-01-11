package com.ga.airticketmanagement.exception;

import com.ga.airticketmanagement.dto.response.ApiErrorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    @ExceptionHandler(InformationFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleInformationFoundException(InformationFoundException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiErrorResponse(
                "INFORMATION_EXISTS",
                e.getMessage()
        ));
    }

    @ExceptionHandler(InformationNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleInformationNotFoundException(InformationNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse(
                "NOT_FOUND",
                e.getMessage()
        ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {

        String param = e.getName();
        String value = e.getValue().toString();
        String expectedType = e.getRequiredType() != null ?
                e.getRequiredType().getSimpleName()
                : "valid value";

        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",value, param, expectedType);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiErrorResponse("INVALID_ARGUMENT_TYPE", message)
        );
    }
}
