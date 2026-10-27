package com.bryan.dompetku.service;

import com.bryan.dompetku.dto.AuthResponse;
import com.bryan.dompetku.dto.LoginRequest;
import com.bryan.dompetku.dto.RegisterRequest;
import com.bryan.dompetku.dto.UserResponse;
import com.bryan.dompetku.entity.Category;
import com.bryan.dompetku.entity.CategoryType;
import com.bryan.dompetku.entity.User;
import com.bryan.dompetku.exception.ConflictException;
import com.bryan.dompetku.exception.InvalidCredentialsException;
import com.bryan.dompetku.exception.ResourceNotFoundException;
import com.bryan.dompetku.repository.CategoryRepository;
import com.bryan.dompetku.repository.UserRepository;
import com.bryan.dompetku.security.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private record DefaultCategory(String name, CategoryType type) {
    }

    private static final List<DefaultCategory> DEFAULT_CATEGORIES = List.of(
            new DefaultCategory("Gaji", CategoryType.INCOME),
            new DefaultCategory("Makan & Minum", CategoryType.EXPENSE),
            new DefaultCategory("Transportasi", CategoryType.EXPENSE),
            new DefaultCategory("Belanja", CategoryType.EXPENSE),
            new DefaultCategory("Tagihan", CategoryType.EXPENSE),
            new DefaultCategory("Hiburan", CategoryType.EXPENSE)
    );

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository,
                       CategoryRepository categoryRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email " + email + " sudah terdaftar");
        }

        String hashedPassword = passwordEncoder.encode(request.password());
        User user = userRepository.save(new User(request.name().trim(), email, hashedPassword));

        for (DefaultCategory defaultCategory : DEFAULT_CATEGORIES) {
            categoryRepository.save(new Category(defaultCategory.name(), defaultCategory.type(), user));
        }

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(normalizeEmail(request.email()))
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return buildAuthResponse(user);
    }

    public UserResponse getCurrentUser(Long userId) {
        return userRepository.findById(userId)
                .map(UserResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = tokenService.generateToken(user);
        return new AuthResponse(token, "Bearer", tokenService.getExpirationSeconds(), UserResponse.from(user));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
