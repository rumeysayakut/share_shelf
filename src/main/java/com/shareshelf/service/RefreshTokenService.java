
package com.shareshelf.service;

import com.shareshelf.entity.RefreshToken;
import com.shareshelf.entity.User;
import com.shareshelf.repository.RefreshTokenRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class RefreshTokenService {

    private static final long REFRESH_TOKEN_EXPIRATION_DAYS = 7;

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public String createRefreshToken(User user) {

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashToken(rawToken));
        refreshToken.setExpiresAt(
                LocalDateTime.now().plusDays(
                        REFRESH_TOKEN_EXPIRATION_DAYS
                )
        );

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    @Transactional
    public RotatedRefreshToken rotateRefreshToken(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException(
                    "Refresh token geçersiz."
            );
        }

        RefreshToken oldToken = refreshTokenRepository
                .findByTokenHash(hashToken(rawToken))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Refresh token geçersiz veya süresi dolmuş."
                ));

        if (oldToken.getRevokedAt() != null
                || !oldToken.getExpiresAt().isAfter(LocalDateTime.now())
                || oldToken.getUser().getDeletedAt() != null) {

            throw new IllegalArgumentException(
                    "Refresh token geçersiz veya süresi dolmuş."
            );
        }

        User user = oldToken.getUser();

        oldToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(oldToken);

        String newRawToken = createRefreshToken(user);

        return new RotatedRefreshToken(user, newRawToken);
    }

    @Transactional
    public void revokeRefreshToken(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            return;
        }

        refreshTokenRepository
                .findByTokenHash(hashToken(rawToken))
                .filter(token -> token.getRevokedAt() == null)
                .ifPresent(token -> {
                    token.setRevokedAt(LocalDateTime.now());
                    refreshTokenRepository.save(token);
                });
    }

    @Transactional
    public void revokeAllRefreshTokens(User user) {

        LocalDateTime now = LocalDateTime.now();

        refreshTokenRepository
                .findAllByUserAndRevokedAtIsNull(user)
                .forEach(token -> {
                    token.setRevokedAt(now);
                    refreshTokenRepository.save(token);
                });
    }

    private String hashToken(String rawToken) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    rawToken.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "Token özeti oluşturulamadı.",
                    exception
            );
        }
    }

    public record RotatedRefreshToken(
            User user,
            String refreshToken
    ) {
    }
}
