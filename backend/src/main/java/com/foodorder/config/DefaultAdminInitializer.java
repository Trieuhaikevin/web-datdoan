package com.foodorder.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.foodorder.model.User;
import com.foodorder.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DefaultAdminInitializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DefaultAdminInitializer.class);

    private final DefaultAdminConfig config;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!config.enabled()) {
            log.info("Default admin seeding is disabled.");
            return;
        }

        if (!StringUtils.hasText(config.email()) || !StringUtils.hasText(config.password())) {
            log.warn("Default admin seeding skipped because email/password is missing in configuration.");
            return;
        }

        String adminEmail = config.email().trim().toLowerCase();
        User admin = userRepository.findByEmail(adminEmail).orElseGet(User::new);

        if (admin.getId() == null) {
            admin.setEmail(adminEmail);
        }

        admin.setFullName(StringUtils.hasText(config.fullName()) ? config.fullName().trim() : "Admin");
        admin.setPassword(passwordEncoder.encode(config.password()));
        admin.setPhone(StringUtils.hasText(config.phone()) ? config.phone().trim() : null);
        admin.setAddress(StringUtils.hasText(config.address()) ? config.address().trim() : null);
        admin.setRole(User.Role.ADMIN);

        User savedAdmin = userRepository.save(admin);

        List<User> admins = userRepository.findByRole(User.Role.ADMIN);
        if (admins.size() > 1) {
            log.warn("There are currently {} ADMIN accounts. Default admin email: {}. Other ADMIN accounts were left unchanged by configuration.",
                    admins.size(), savedAdmin.getEmail());
        } else {
            log.info("Default admin is ready: {}", savedAdmin.getEmail());
        }
    }
}
