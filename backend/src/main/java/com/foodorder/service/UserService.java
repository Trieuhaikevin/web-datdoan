package com.foodorder.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.foodorder.model.User;
import com.foodorder.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        Objects.requireNonNull(user, "User cannot be null");
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        userRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        Objects.requireNonNull(email, "Email cannot be null");
        return userRepository.existsByEmail(email);
    }
}