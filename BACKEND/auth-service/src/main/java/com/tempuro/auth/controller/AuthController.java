package com.tempuro.auth.controller;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tempuro.auth.dto.LoginRequest;
import com.tempuro.auth.dto.LoginResponse;
import com.tempuro.auth.dto.RegisterRequest;
import com.tempuro.auth.exception.InvalidRefreshTokenException;
import com.tempuro.auth.exception.JwtGenerationException;
import com.tempuro.auth.exception.RefreshTokenNotFoundException;
import com.tempuro.auth.service.AuthService;
import com.tempuro.auth.util.AuthCookieUtil;
import io.opentelemetry.context.Scope;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
* Controlador REST para manejar las llamadas relacionadas con login y autenticación
*/
@RestController
/*
 * Todas las rutas de este controlador tendrán el prefijo /auth
 * Ejemplo: /auth/login, /auth/register, etc.
 */
@RequestMapping("/auth")
/*
 * Habilita que otro dominio pueda hacer peticiones a este backend
 * En este caso, permite que http://localhost:5173 acceda a las rutas
 */
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("auth-service");

    /*
     * El objeto Span representa una operación o unidad de trabajo que queremos
     * medir/trazar con OpenTelemetry. Cada span puede tener un nombre, atributos,
     * eventos y un estado (normal o error). En este caso, "login-operation" es
     * el nombre del span que medirá todo lo que ocurre dentro del login.
     *
     * Scope es un mecanismo de OpenTelemetry que define el contexto activo del
     * span.
     * Al usar try-with-resources con `Scope scope = span.makeCurrent()`, estamos:
     * 1. Haciendo que este span sea el span "actual" en el contexto de ejecución.
     * 2. Permitendo que cualquier operación que se ejecute dentro del try (llamadas
     * a servicios, repositorios, etc.) herede automáticamente este span como
     * padre si crea nuevos spans.
     * 3. Garantizando que al cerrar el try, el scope se cierra y el span deja de
     * ser
     * el activo, evitando fugas de contexto.
     *
     * En resumen:
     * - span.startSpan(): crea el span.
     * - span.makeCurrent(): devuelve un Scope que pone ese span como activo.
     * - try (Scope scope = ...): asegura que el span será el actual solo dentro del
     * bloque.
     * - span.end(): cierra el span y envía los datos a tu backend (OTLP/Jaeger).
     */

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {
        Span span = tracer.spanBuilder("login-operation").startSpan();
        try (Scope scope = span.makeCurrent()) {
            LoginResponse loginResponse = authService.login(loginRequest);
            ResponseCookie refreshCookie = AuthCookieUtil.buildRefreshCookie(loginResponse.getRefreshToken());

            logger.info("Usuario con email: {} ha iniciado sesión correctamente", loginRequest.getEmail());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(loginResponse);

        } catch (BadCredentialsException | UsernameNotFoundException e) {
            span.setStatus(StatusCode.ERROR, "Credenciales incorrectas");
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        } catch (DisabledException e) {
            span.setStatus(StatusCode.ERROR, "Usuario deshabilitado");
            return ResponseEntity.status(403).body("Usuario deshabilitado");
        } catch (LockedException e) {
            span.setStatus(StatusCode.ERROR, "Usuario bloqueado");
            return ResponseEntity.status(423).body("Usuario bloqueado");
        } finally {
            span.end();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refresh(@CookieValue(value = "refresh_token", required = false) String refreshToken) {
        Span span = tracer.spanBuilder("refresh-operation").startSpan();
        try (Scope scope = span.makeCurrent()) {

            if (refreshToken == null) {
                span.setStatus(StatusCode.ERROR, "No refresh token in cookie");
                return ResponseEntity.status(401).body("No se encontró refresh token en la cookie");
            }

            try {
                LoginResponse loginResponse = authService.refresh(refreshToken);
                ResponseCookie refreshCookie = AuthCookieUtil.buildRefreshCookie(loginResponse.getRefreshToken());

                logger.info("Refresh token usado correctamente para generar nuevo access token");

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                        .body(loginResponse);

            } catch (RefreshTokenNotFoundException | InvalidRefreshTokenException | JwtGenerationException e) {
                span.setStatus(StatusCode.ERROR, e.getMessage());
                return ResponseEntity.status(401).body(e.getMessage());
            }

        } finally {
            span.end();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequest registerRequest) {
        Span span = tracer.spanBuilder("register-operation").startSpan();
        try (Scope scope = span.makeCurrent()) {

            try {
                authService.register(registerRequest);
                logger.info("Usuario {} registrado correctamente", registerRequest.getEmail());
                return ResponseEntity.ok("Usuario registrado correctamente");

            } catch (IllegalArgumentException e) {
                span.setStatus(StatusCode.ERROR, e.getMessage());
                return ResponseEntity.status(400).body("Error al registrar usuario: " + e.getMessage());

            } catch (Exception e) {
                span.setStatus(StatusCode.ERROR, e.getMessage());
                logger.error("Error inesperado al registrar usuario {}", registerRequest.getEmail(), e);
                return ResponseEntity.status(500).body("Error interno del servidor");
            }

        } finally {
            span.end();
        }
    }
}
