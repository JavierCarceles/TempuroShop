package com.tempuro.auth.util;

import org.springframework.http.ResponseCookie;

public class AuthCookieUtil {

    // Impide la instancicación de la clase ya que tiene metodos estáticos
    // Y nadie deberia crear objetos de esta clase
    private AuthCookieUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final long REFRESH_TOKEN_MAX_AGE = 30L * 24 * 60 * 60;

    /*
     * Creamos el objeto cookie, que es el portador del refreshToken, el token que
     * usará
     * el navegador para actualizar la sesion del usuario cuando esté dentro de la
     * pagina web
     * httpOnly impide que JS del front pueda leer la cookie, importante para
     * impedir que te la roben
     * secure hace que la cookie solo se envie por HTTPS
     * path "/" hace que la cookie esté disponible en toda la web
     * maxAge es el tiempo que dura la cookie en el navegador, en este caso 30 dias
     * sameSite "Strict" impide que la cookie se envie en peticiones cross-site, es
     * decir, si por ejemplo alguien hace click en un enlace a tempuroshop desde
     * facebook, la cookie no se envia
     * build termina de montar la cookie y el objeto ResponseCookie
     */
    public static ResponseCookie buildRefreshCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(REFRESH_TOKEN_MAX_AGE)
                .sameSite("Strict")
                .build();
    }

}
