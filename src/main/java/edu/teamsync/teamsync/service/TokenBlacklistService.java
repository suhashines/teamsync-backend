package edu.teamsync.teamsync.service;

import edu.teamsync.teamsync.entity.BlacklistedToken;
import edu.teamsync.teamsync.repository.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Transactional
    public void blacklistToken(String token) {
        if (token != null && !token.trim().isEmpty()) {
            BlacklistedToken blacklistedToken = BlacklistedToken.builder()
                    .token(token)
                    .blacklistedAt(LocalDateTime.now())
                    .build();

            blacklistedTokenRepository.save(blacklistedToken);
            log.info("Token blacklisted successfully");
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }
}