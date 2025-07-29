package com.example.funpay_clone.controllers;

import com.example.funpay_clone.services.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerJson(@Valid @RequestBody RegisterRequest request) {
        return processRegistration(request);
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> registerForm(@Valid RegisterRequestForm request) {
        RegisterRequest jsonRequest = new RegisterRequest();
        jsonRequest.setUsername(request.getUsername());
        jsonRequest.setEmail(request.getEmail());
        jsonRequest.setPassword(request.getPassword());
        jsonRequest.setRole(request.getRole());
        return processRegistration(jsonRequest);
    }

    private ResponseEntity<?> processRegistration(RegisterRequest request) {
        try {
            userService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                Set.of(request.getRole())
            );
            return ResponseEntity.ok(new SuccessResponse("Регистрация успешна"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // DTO для JSON
    @Data
    public static class RegisterRequest {
        @NotBlank @Size(min=3, max=20) private String username;
        @NotBlank @Email private String email;
        @NotBlank @Size(min=6) private String password;
        @NotBlank private String role;
    }

    
    @Data
    public static class RegisterRequestForm {
        private String username;
        private String email;
        private String password;
        private String role;
    }

    @Data
    public static class SuccessResponse {
        private final String message;
        private final boolean success = true;
    }

    @Data
    public static class ErrorResponse {
        private final String message;
        private final boolean success = false;
    }
}