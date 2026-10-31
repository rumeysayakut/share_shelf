
package com.shareshelf.controller;

import com.shareshelf.dto.request.LoginRequestDto;
import com.shareshelf.dto.request.UserRequestDto;
import com.shareshelf.dto.response.AuthResponseDto;
import com.shareshelf.service.AuthService;
import com.shareshelf.service.RefreshTokenService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.access-cookie-name:SHARE_SHELF_ACCESS}")
    private String accessCookieName;

    @Value("${jwt.refresh-cookie-name:SHARE_SHELF_REFRESH}")
    private String refreshCookieName;

    @Value("${app.security.cookie-secure:false}")
    private boolean cookieSecure;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(
            @Valid @RequestBody UserRequestDto request,
            HttpServletResponse response) {

        AuthResponseDto authResponse = authService.register(request);

        setAccessCookie(response, authResponse.getAccessToken());
        setRefreshCookie(response, authResponse.getRefreshToken());

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @Valid @RequestBody LoginRequestDto request,
            HttpServletResponse response) {

        AuthResponseDto authResponse = authService.login(request);

        setAccessCookie(response, authResponse.getAccessToken());
        setRefreshCookie(response, authResponse.getRefreshToken());

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {

        String refreshToken = extractCookie(request, refreshCookieName);

        AuthResponseDto authResponse = authService.refresh(refreshToken);

        setAccessCookie(response, authResponse.getAccessToken());
        setRefreshCookie(response, authResponse.getRefreshToken());

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        String refreshToken = extractCookie(request, refreshCookieName);

        refreshTokenService.revokeRefreshToken(refreshToken);

        clearCookie(response, accessCookieName);
        clearCookie(response, refreshCookieName);

        return ResponseEntity.noContent().build();
    }

    private void setAccessCookie(HttpServletResponse response, String token) {

        Cookie cookie = new Cookie(accessCookieName, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge(15 * 60);

        response.addCookie(cookie);
    }

    private void setRefreshCookie(HttpServletResponse response, String token) {

        Cookie cookie = new Cookie(refreshCookieName, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(cookie);
    }

    private String extractCookie(HttpServletRequest request, String name) {

        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    private void clearCookie(HttpServletResponse response, String name) {

        Cookie cookie = new Cookie(name, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath(name.equals(accessCookieName) ? "/" : "/api/auth");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }
}
