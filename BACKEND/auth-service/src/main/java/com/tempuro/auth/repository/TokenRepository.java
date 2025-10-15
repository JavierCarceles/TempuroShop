package com.tempuro.auth.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.tempuro.auth.model.Token;
import com.tempuro.auth.model.User;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    List<Token> findAllByUserAndRevokedFalse(User user);

    Optional<Token> findByTokenAndRevokedFalse(String token);

    @Modifying
    @Query("UPDATE Token t SET t.revoked = true WHERE t.user = :user AND t.revoked = false")
    void revokeAllUserTokens(User user);

    List<Token> findAllByExpiryDateBefore(LocalDateTime dateTime);

    Optional<Token> findByToken(String refreshToken);

}
