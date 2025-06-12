package edu.teamsync.teamsync.repository;

import edu.teamsync.teamsync.entity.PasswordResetToken;
import edu.teamsync.teamsync.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);

    @Modifying
    @Query("UPDATE PasswordResetToken prt SET prt.used = true WHERE prt.user = :user")
    void markAllTokensAsUsedByUser(@Param("user") Users user);

    @Modifying
    @Query("DELETE FROM PasswordResetToken prt WHERE prt.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(prt) FROM PasswordResetToken prt WHERE prt.user = :user AND prt.used = false AND prt.expiresAt > :now")
    int countValidTokensByUser(@Param("user") Users user, @Param("now") LocalDateTime now);
}
