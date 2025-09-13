package com.tempuro.auth.service;

import java.time.LocalDateTime;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.tempuro.auth.dto.RegisterRequest;
import com.tempuro.auth.model.User;
import com.tempuro.auth.dto.LoginRequest;
import com.tempuro.auth.dto.LoginResponse;
import com.tempuro.auth.repository.RoleRepository;
import com.tempuro.auth.repository.TokenRepository;
import com.tempuro.auth.security.JwtUtils;
import com.tempuro.auth.model.Token;
import lombok.RequiredArgsConstructor;
import com.tempuro.auth.model.Role;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final UserServiceImpl userService;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    /*
    * 1. Creamos un contenedor de Spring con email y contraseña del usuario 
    * 2. LoginController llama a authManager.authenticate(auth) 
    * 3. authenticate() delega en DaoAuthenticationProvider de Spring
    * 4. DaoAuthenticationProvider necesita un UserDetailsService, así que busca un bean que lo implemente
    * 5. Spring encuentra UserServiceImpl y llama a loadUserByUsername(email)
    * 6. Tu método busca el usuario en la DB y devuelve un User con contraseña y roles
    * 7. DaoAuthenticationProvider compara la contraseña enviada con la contraseña encriptada usando PasswordEncoder
    * 8. Si coincide → devuelve Authentication autenticado; si no → lanza BadCredentialsException
    * 9. Creamos el token con una clase JwtUtil
    */
    @Transactional
    public LoginResponse Login(LoginRequest loginRequest){

        Authentication auth = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        authManager.authenticate(auth);

        String accessJwt = jwtUtils.generateAccessToken(loginRequest.getEmail());
        String refreshJwt = jwtUtils.generateExpirationToken(loginRequest.getEmail());
         
        User user = userService.findByEmail(loginRequest.getEmail());
        
        Token tokenEntity = new Token();
        tokenEntity.setToken(refreshJwt);
        tokenEntity.setUser(user);
        tokenEntity.setExpiryDate(LocalDateTime.now().plusDays(30));
        tokenEntity.setCreatedAt(LocalDateTime.now());
        tokenEntity.setRevoked(false);
        tokenRepository.save(tokenEntity);

        return new LoginResponse(accessJwt, refreshJwt);

    }

    @Transactional
    public void register(RegisterRequest request) {

        if (userService.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        Role clientRole = roleRepository.findById(2L).orElseThrow(() -> new RuntimeException("ROLE_CLIENT no existe en la base de datos"));
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(now);
        user.setIsEnabled(true);
        user.setUpdatedAt(now);
        
        Set<Role> roles = new HashSet<>();
        roles.add(clientRole);
        user.setRoles(roles);

        userService.saveUser(user);

    }

    @Transactional(readOnly = true)
    public LoginResponse refresh(String refreshToken){
        
        Token authToken = tokenRepository.findByToken(refreshToken).orElseThrow(() -> new RuntimeException("Refresh token no encontrado"));

        if (authToken.isRevoked() || authToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token inválido o caducado");
        }

        String accessJwt = jwtUtils.generateAccessToken(authToken.getUser().getEmail());

        return new LoginResponse(accessJwt, null);
    }

}
