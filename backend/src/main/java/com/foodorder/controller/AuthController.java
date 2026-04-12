package com.foodorder.controller;

import com.foodorder.dto.LoginRequest;
import com.foodorder.dto.LoginResponse;
import com.foodorder.dto.RegisterRequest;
import com.foodorder.model.User;
import com.foodorder.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        validateLoginRequest(request);

        return userService.authenticate(request.email(), request.password())
                .map(user -> ResponseEntity.ok(toLoginResponse(user, "Đăng nhập thành công")))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(null, null, null, null, null, null,
                                "Email hoặc mật khẩu không đúng")));
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest request) {
        validateRegisterRequest(request);

        if (userService.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email đã tồn tại");
        }

        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPassword(request.password());
        user.setPhone(request.phone() == null ? null : request.phone().trim());
        user.setAddress(request.address() == null ? null : request.address().trim());
        user.setRole(User.Role.USER);

        User savedUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toLoginResponse(savedUser, "Đăng ký thành công"));
    }

    private void validateLoginRequest(LoginRequest request) {
        if (request == null || !StringUtils.hasText(request.email()) || !StringUtils.hasText(request.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email và mật khẩu không được để trống");
        }
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (request == null
                || !StringUtils.hasText(request.fullName())
                || !StringUtils.hasText(request.email())
                || !StringUtils.hasText(request.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Họ tên, email và mật khẩu không được để trống");
        }
    }

    private LoginResponse toLoginResponse(User user, String message) {
        return new LoginResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                user.getPhone(),
                user.getAddress(),
                message
        );
    }
}
