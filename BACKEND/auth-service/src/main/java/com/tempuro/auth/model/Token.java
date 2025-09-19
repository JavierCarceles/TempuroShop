package com.tempuro.auth.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /*
     * userId
     * Como el user_id está dentro de User, y ya tienes un User,
     * mejor marcar este user_id como read-only para que JPA no dé conflicto
     * intentando mantenerlos sincronizados al insertar o actualizar
     */
    @Column(name = "user_id", updatable = false, insertable = false)
    private Long userId;

    @Column(name = "token")
    private String tokenValue;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    /*
     * Relación ManyToOne con User
     * Indica que la columna user_id de tokens es la FK de esta tabla
     * JPA sabe cómo relacionar automáticamente tokens con usuarios usando ManyToOne
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
