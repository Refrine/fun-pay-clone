package com.example.funpay_clone.controllers;

import com.example.funpay_clone.services.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Slf4j
@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String registerJson(@Valid @RequestBody RegisterRequest request, RedirectAttributes redirectAttributes) {
        return processRegistration(request, redirectAttributes);
    }

    
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String registerForm(@Valid RegisterRequestForm request, RedirectAttributes redirectAttributes) {
        RegisterRequest jsonRequest = new RegisterRequest();
        jsonRequest.setUsername(request.getUsername());
        jsonRequest.setEmail(request.getEmail());
        jsonRequest.setPassword(request.getPassword());
        jsonRequest.setRole(request.getRole());
        return processRegistration(jsonRequest, redirectAttributes);
    }

    private String processRegistration(RegisterRequest request, RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                Set.of(request.getRole())
            );
            log.info("Пользователь {} успешно зарегистрирован", request.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Регистрация прошла успешно!");
            return "redirect:/"; 
        } catch (Exception e) {
            log.error("Ошибка регистрации: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/auth/register"; 
        }
    }

   
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
}