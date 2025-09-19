package com.tempuro.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

/* 
 * 1. Marca esta clase como clase de configuración de Spring.
 * 2. Spring buscará aquí métodos anotados con @Bean para registrarlos en el ApplicationContext.
 */
@Configuration
/*
 * 1. Activa la configuración de seguridad de Spring Security.
 * 2. Permite que Spring Security use los beans declarados (PasswordEncoder,
 * AuthenticationManager)
 * y configure AuthenticationProviders, filtros, etc.
 */
@EnableWebSecurity
public class SecurityConfig {

    /*
     * 1. Declaramos un PasswordEncoder como bean para que Spring lo conozca.
     * 2. DaoAuthenticationProvider necesita un PasswordEncoder para poder comparar
     * la contraseña enviada por el usuario con la contraseña encriptada de la DB.
     * 3. Spring lo inyecta automáticamente en DaoAuthenticationProvider cuando
     * autentica.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * 1. Declaramos AuthenticationManager como bean para poder inyectarlo en el
     * controller.
     * 2. AuthenticationManager es el coordinador que delega la autenticación a
     * AuthenticationProviders
     * como DaoAuthenticationProvider, que a su vez usan UserDetailsService y
     * PasswordEncoder.
     * 3. Con esto, podemos hacer @Autowired en LoginController y llamar
     * authManager.authenticate(auth)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /*
     * Definimos el SecurityFilterChain como bean
     * 1. http.cors(): permite que Spring Security respete la configuración de CORS
     * definida en CorsConfig
     * 2. csrf().disable(): deshabilita CSRF para poder hacer POST desde front con
     * fetch sin token CSRF
     * 3. authorizeHttpRequests(): configuramos qué rutas son públicas y cuáles
     * necesitan autenticación
     * - /auth/**: permitimos todas las rutas de autenticación (login, register)
     * - anyRequest().authenticated(): todas las demás rutas requieren estar
     * autenticado
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}