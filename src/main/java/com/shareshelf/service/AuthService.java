
package com.shareshelf.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shareshelf.dto.request.LoginRequestDto;
import com.shareshelf.dto.request.UserRequestDto;
import com.shareshelf.dto.response.AuthResponseDto;
import com.shareshelf.entity.User;
import com.shareshelf.mapper.UserMapper;
import com.shareshelf.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            UserService userService,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService) {

        this.userRepository = userRepository;
        this.userService = userService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public AuthResponseDto register(UserRequestDto request) {

        userService.register(request);

        User user = userRepository
                .findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new IllegalStateException(
                        "Kayıt sonrası kullanıcı bulunamadı."
                ));

        return createAuthResponse(user);
    }

    @Transactional
    public AuthResponseDto login(LoginRequestDto request) {

        User user = userRepository
                .findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException(
                        "E-posta veya şifre hatalı."
                ));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new IllegalArgumentException(
                    "E-posta veya şifre hatalı."
            );
        }

        return createAuthResponse(user);
    }

    @Transactional
    public AuthResponseDto refresh(String refreshToken) {

        RefreshTokenService.RotatedRefreshToken rotated =
                refreshTokenService.rotateRefreshToken(refreshToken);

        User user = rotated.user();

        AuthResponseDto response = new AuthResponseDto();
        response.setAccessToken(
                jwtService.generateAccessToken(user)
        );
        response.setRefreshToken(rotated.refreshToken());
        response.setUser(userMapper.toResponseDto(user));

        return response;
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.revokeRefreshToken(refreshToken);
    }

    private AuthResponseDto createAuthResponse(User user) {

        AuthResponseDto response = new AuthResponseDto();

        response.setAccessToken(
                jwtService.generateAccessToken(user)
        );

        response.setRefreshToken(
                refreshTokenService.createRefreshToken(user)
        );

        response.setUser(userMapper.toResponseDto(user));

        return response;
    }
}
