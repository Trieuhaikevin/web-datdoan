package com.foodorder.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.foodorder.model.User;
import com.foodorder.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        return userRepository.findById(id);
    }

    public Optional<User> authenticate(String email, String rawPassword) {
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(rawPassword, "Password cannot be null");
        String normalizedEmail = normalizeEmail(email);

        return userRepository.findByEmail(normalizedEmail)
                .filter(user -> passwordMatchesAndUpgrade(user, rawPassword));
    }

    public User createUser(User user) {
        Objects.requireNonNull(user, "User cannot be null");

        if (!StringUtils.hasText(user.getEmail())) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
        if (!StringUtils.hasText(user.getPassword())) {
            throw new IllegalArgumentException("Password cannot be blank");
        }

        user.setEmail(normalizeEmail(user.getEmail()));
        
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }
        
        if (user.getRole() == null) {
            user.setRole(User.Role.USER);
        }

        if (!isBcryptHash(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        userRepository.deleteById(id);
    }

    public User updateUser(User user) {
        Objects.requireNonNull(user, "User cannot be null");
        Objects.requireNonNull(user.getId(), "User ID cannot be null");

        User existing = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (StringUtils.hasText(user.getFullName())) {
            existing.setFullName(user.getFullName().trim());
        }

        if (StringUtils.hasText(user.getPhone())) {
            existing.setPhone(user.getPhone().trim());
        }

        if (StringUtils.hasText(user.getAddress())) {
            existing.setAddress(user.getAddress().trim());
        }

        return userRepository.save(existing);
    }

    public boolean existsByEmail(String email) {
        Objects.requireNonNull(email, "Email cannot be null");
        return userRepository.existsByEmail(normalizeEmail(email));
    }

    private boolean passwordMatchesAndUpgrade(User user, String rawPassword) {
        String storedPassword = user.getPassword();
        if (!StringUtils.hasText(storedPassword)) {
            return false;
        }

        if (isBcryptHash(storedPassword)) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }

        if (storedPassword.equals(rawPassword)) {
            user.setPassword(passwordEncoder.encode(rawPassword));
            userRepository.save(user);
            return true;
        }

        return false;
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private boolean isBcryptHash(String value) {
        return value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$");
    }
}
