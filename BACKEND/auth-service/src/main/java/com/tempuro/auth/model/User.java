package com.tempuro.auth.model;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "enabled")
    private Boolean isEnabled;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    /*
     * Relación ManyToMany entre User y Role
     * 1. FetchType.EAGER: siempre carga los roles junto al usuario
     * 2. @JoinTable indica la tabla intermedia que relaciona users y roles
     * 3. joinColumns: columna de user_roles que apunta a User
     * 4. inverseJoinColumns: columna de user_roles que apunta a Role
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private transient Set<Role> roles;

    /*
     * Relación OneToMany entre User y Token
     * 1. mappedBy="user": indica que la relación se define en el atributo 'user' de
     * la clase Token
     * 2. Permite obtener todos los tokens asociados a un usuario
     */
    @OneToMany(mappedBy = "user")
    private transient Set<Token> tokens;

    /*
     * Implementación de UserDetails requerida por Spring Security
     * 1. getAuthorities(): devuelve los roles del usuario como GrantedAuthority
     * 2. Cada role se transforma en SimpleGrantedAuthority usando su nombre
     * 3. Collectors.toSet(): devuelve un Set de authorities para Spring Security
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }

    /*
     * Metodos de UserDetails
     */
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

}
