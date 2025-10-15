package com.tempuro.auth.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tempuro.auth.dto.LoginRequest;
import com.tempuro.auth.dto.LoginResponse;
import com.tempuro.auth.dto.RegisterRequest;
import com.tempuro.auth.exception.AuthenticationFailedException;
import com.tempuro.auth.exception.EmailAlreadyRegisteredException;
import com.tempuro.auth.exception.InvalidRefreshTokenException;
import com.tempuro.auth.exception.JwtGenerationException;
import com.tempuro.auth.exception.RefreshTokenNotFoundException;
import com.tempuro.auth.exception.TokenPersistenceException;
import com.tempuro.auth.exception.UserNotFoundException;
import com.tempuro.auth.exception.UserPersistenceException;
import com.tempuro.auth.model.Role;
import com.tempuro.auth.model.Token;
import com.tempuro.auth.model.User;
import com.tempuro.auth.repository.RoleRepository;
import com.tempuro.auth.repository.TokenRepository;
import com.tempuro.auth.security.JwtUtils;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final UserServiceImpl userService;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    private final Tracer tracer = GlobalOpenTelemetry.getTracer("auth-service");

    @Transactional
    public LoginResponse login(LoginRequest loginRequest)
            throws UserNotFoundException, JwtGenerationException, AuthenticationFailedException {

        Span span = tracer.spanBuilder("login-service").startSpan();
        try (Scope scope = span.makeCurrent()) {

            Span authSpan = tracer.spanBuilder("authenticate-user").startSpan();
            try (Scope authScope = authSpan.makeCurrent()) {
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword());
                authManager.authenticate(auth);
            } catch (Exception e) {
                authSpan.setStatus(StatusCode.ERROR, "Error autenticando usuario");
                throw new AuthenticationFailedException("Error autenticando usuario", e);
            } finally {
                authSpan.end();
            }

            Span tokenSpan = tracer.spanBuilder("generate-jwt-tokens").startSpan();
            String accessJwt;
            String refreshJwt;
            try (Scope tokenScope = tokenSpan.makeCurrent()) {
                accessJwt = jwtUtils.generateAccessToken(loginRequest.getEmail());
                refreshJwt = jwtUtils.generateExpirationToken(loginRequest.getEmail());
            } catch (Exception e) {
                tokenSpan.setStatus(StatusCode.ERROR, "Error generando JWT");
                throw new JwtGenerationException("Error generando JWT", e);
            } finally {
                tokenSpan.end();
            }

            Span userSpan = tracer.spanBuilder("fetch-user-from-db").startSpan();
            User user;
            try (Scope userScope = userSpan.makeCurrent()) {
                user = userService.findByEmail(loginRequest.getEmail());
                if (user == null) {
                    throw new UserNotFoundException("Usuario no encontrado");
                }
            } catch (Exception e) {
                userSpan.setStatus(StatusCode.ERROR, "Error buscando usuario en DB");
                throw e;
            } finally {
                userSpan.end();
            }

            Span tokenEntitySpan = tracer.spanBuilder("save-refresh-token").startSpan();
            try (Scope tokenEntityScope = tokenEntitySpan.makeCurrent()) {
                Token tokenEntity = new Token();
                tokenEntity.setTokenValue(refreshJwt);
                tokenEntity.setUser(user);
                tokenEntity.setExpiryDate(LocalDateTime.now().plusDays(30));
                tokenEntity.setCreatedAt(LocalDateTime.now());
                tokenEntity.setRevoked(false);
                tokenRepository.save(tokenEntity);
            } catch (Exception e) {
                tokenEntitySpan.setStatus(StatusCode.ERROR, "Error guardando refresh token");
                throw new TokenPersistenceException("Error guardando refresh token", e);
            } finally {
                tokenEntitySpan.end();
            }

            return new LoginResponse(accessJwt, refreshJwt);

        } finally {
            span.end();
        }
    }

    @Transactional
    public void register(RegisterRequest request)
            throws EmailAlreadyRegisteredException, UserPersistenceException {

        Span span = tracer.spanBuilder("register-service").startSpan();
        try (Scope scope = span.makeCurrent()) {

            Span existsSpan = tracer.spanBuilder("check-email-existence").startSpan();
            try (Scope existsScope = existsSpan.makeCurrent()) {
                if (userService.existsByEmail(request.getEmail())) {
                    throw new EmailAlreadyRegisteredException("El email ya está registrado");
                }
            } finally {
                existsSpan.end();
            }

            Span roleSpan = tracer.spanBuilder("fetch-client-role").startSpan();
            Role clientRole;
            try (Scope roleScope = roleSpan.makeCurrent()) {
                clientRole = roleRepository.findById(2L)
                        .orElseThrow(() -> new RuntimeException("ROLE_CLIENT no existe en la base de datos"));
            } finally {
                roleSpan.end();
            }

            LocalDateTime now = LocalDateTime.now();

            Span userSpan = tracer.spanBuilder("create-user-entity").startSpan();
            User user;
            try (Scope userScope = userSpan.makeCurrent()) {
                user = new User();
                user.setUsername(request.getUsername());
                user.setEmail(request.getEmail());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setCreatedAt(now);
                user.setIsEnabled(true);
                user.setUpdatedAt(now);

                Set<Role> roles = new HashSet<>();
                roles.add(clientRole);
                user.setRoles(roles);
            } finally {
                userSpan.end();
            }

            Span saveUserSpan = tracer.spanBuilder("save-user-to-db").startSpan();
            try (Scope saveScope = saveUserSpan.makeCurrent()) {
                userService.saveUser(user);
            } catch (Exception e) {
                saveUserSpan.setStatus(StatusCode.ERROR, "Error guardando usuario en DB");
                throw new UserPersistenceException("Error guardando usuario en DB", e);
            } finally {
                saveUserSpan.end();
            }

        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public LoginResponse refresh(String refreshToken)
            throws RefreshTokenNotFoundException, InvalidRefreshTokenException, JwtGenerationException {

        Span span = tracer.spanBuilder("refresh-token-service").startSpan();
        try (Scope scope = span.makeCurrent()) {

            Span findTokenSpan = tracer.spanBuilder("find-refresh-token").startSpan();
            Token authToken;
            try (Scope findScope = findTokenSpan.makeCurrent()) {
                authToken = tokenRepository.findByToken(refreshToken)
                        .orElseThrow(() -> {
                            findTokenSpan.setStatus(StatusCode.ERROR, "Refresh token no encontrado");
                            return new RefreshTokenNotFoundException("Refresh token no encontrado");
                        });
            } finally {
                findTokenSpan.end();
            }

            if (authToken.isRevoked() || authToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                throw new InvalidRefreshTokenException("Refresh token inválido o caducado");
            }

            Span generateAccessSpan = tracer.spanBuilder("generate-access-jwt").startSpan();
            String accessJwt;
            try (Scope accessScope = generateAccessSpan.makeCurrent()) {
                accessJwt = jwtUtils.generateAccessToken(authToken.getUser().getEmail());
            } catch (Exception e) {
                generateAccessSpan.setStatus(StatusCode.ERROR, "Error generando access token");
                throw new JwtGenerationException("Error generando access token", e);
            } finally {
                generateAccessSpan.end();
            }

            return new LoginResponse(accessJwt, null);

        } finally {
            span.end();
        }
    }

}
