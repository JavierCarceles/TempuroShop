package com.tempuro.auth.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/*
* Component es el @ generico de Service, ambos son "lo mismo" pero service es mas específico
* para decir que la clase contiene logica de caso de uso, pero tanto component como service 
* meten la clase donde esten en el applicationContext 
*/
@Component
public class JwtUtils {

    /*
     * Value recoge el valor de application properties para asignarlo a la variable
     * de la clase
     */
    @Value("${jwt.accessExpiration}")
    private Long accessExpirationT;
    @Value("${jwt.refreshExpiration}")
    private Long refreshExpirationT;

    @Value("${jwt.secret}")
    private String key;

    private Key getKey() {
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    /*
     * La clave secreta y el tiempo de expiración deben guardarse en application
     * properties y nunca subir este archivo en los push
     * La clave secreta se importa y luego se codifica para usarla en la firma, es
     * decir, doble capa de seguridad
     * 1. Para generar el Token se necesita:
     * 2. Jwts.Builder()
     * 2. Usuario .setSubject()
     * 3. Fecha de creación .setIssuedAt()
     * 4. Fecha de expiración .setExpiration()
     * 5. Firma con la clave secreta .signWith()
     * 6. Convertir a String
     */
    public String generateAccessToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpirationT))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateExpirationToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationT))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

}
