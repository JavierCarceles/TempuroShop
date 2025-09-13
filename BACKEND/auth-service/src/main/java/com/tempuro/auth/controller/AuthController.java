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
import com.tempuro.auth.service.AuthService;
import com.tempuro.auth.util.AuthCookieUtil;

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

    /*
    * El ResponseEntity permite poner codigos de ok, error y muchos tipos, es util gestionarlo con el "?" 
    * para poder devolver diferentes tipos de objetos dependiendo del caso
    */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try {
            LoginResponse loginResponse = authService.Login(loginRequest);
            ResponseCookie refreshCookie = AuthCookieUtil.buildRefreshCookie(loginResponse.getRefreshToken());

            logger.info("Usuario {} ha iniciado sesión correctamente", loginRequest.getEmail());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(loginResponse);

        } catch (BadCredentialsException | UsernameNotFoundException e) {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        } catch (DisabledException e) {
            return ResponseEntity.status(403).body("Usuario deshabilitado");
        } catch (LockedException e) {
            return ResponseEntity.status(423).body("Usuario bloqueado");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(value="refresh_token", required = false) String refreshToken){

        if (refreshToken == null) {
            return ResponseEntity.status(401).body("No se encontró refresh token en la cookie");
        }

        try{
            LoginResponse loginResponse = authService.refresh(refreshToken);
            ResponseCookie refreshCookie = AuthCookieUtil.buildRefreshCookie(loginResponse.getRefreshToken());

            logger.info("Refresh token usado correctamente para generar nuevo access token");

            return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                        .body(loginResponse);

        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            authService.register(registerRequest);
            logger.info("Usuario {} registrado correctamente", registerRequest.getEmail());
            return ResponseEntity.ok("Usuario registrado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body("Error al registrar usuario: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al registrar usuario {}", registerRequest.getEmail(), e);
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }
}
