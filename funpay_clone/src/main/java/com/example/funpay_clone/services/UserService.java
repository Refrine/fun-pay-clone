package com.example.funpay_clone.services;

import com.example.funpay_clone.models.User;
import com.example.funpay_clone.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     
     * @param username имя пользователя
     * @param email email
     * @param password пароль 
     * @param roles набор ролей
     * @return зарегистрированный пользователь
     * @throws UserAlreadyExistsException если пользователь с таким именем или email уже существует
     * @throws IllegalArgumentException если данные некорректны
     */
    @Transactional
    public User registerUser(String username, String email, String password, Set<String> roles) {
        validateUserData(username, email, password, roles);
        
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        log.info("Пользователь {} успешно зарегистрирован (ID: {})", username, savedUser.getId());
        return savedUser;
    }

    
    private void validateUserData(String username, String email, String password, Set<String> roles) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }
        
        if (email == null || !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Некорректный email");
        }
        
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Пароль должен содержать минимум 6 символов");
        }
        
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("У пользователя должна быть хотя бы одна роль");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("Пользователь с именем '" + username + "' уже существует");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Пользователь с email '" + email + "' уже существует");
        }
    }

    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    
    public static class UserRegistrationException extends RuntimeException {
        public UserRegistrationException(String message) {
            super(message);
        }
        public UserRegistrationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }
}