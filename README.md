# TempuroShop - Simulación de Tienda Online

TempuroShop es un proyecto de simulación de una tienda online al estilo Amazon. Actualmente, se está desarrollando con un enfoque **full-stack moderno**, integrando frontend en React y backend en Spring Boot con Java 17.  

El objetivo de este proyecto es construir un **sistema modular y escalable**, con microservicios, API Gateway y seguridad robusta mediante JWT y Spring Security.

---

## Tecnologías

- **Frontend:** React, TypeScript  
- **Backend:** Spring Boot, Java 17, Spring Security, Hibernate/JPA  
- **Base de datos:** MySQL  
- **Seguridad:** JWT, Refresh Tokens, Cookies HttpOnly  
- **Arquitectura:** Microservicios (en desarrollo), API Gateway (próximamente)  

---

## Características actuales

1. **Autenticación y registro de usuarios:**  
   - Registro de nuevos usuarios con roles y credenciales seguras.  
   - Login con **JWT** y persistencia de sesión.  

2. **Gestión de tokens:**  
   - Generación de **Access Token** (15 min) y **Refresh Token** (30 días).  
   - Refresco automático de Access Token usando cookies HttpOnly.  
   - Persistencia de Refresh Token en base de datos para seguridad y control de revocación.  

3. **Logs de seguridad:**  
   - Validación y seguimiento de sesiones en tiempo real.  
   - Ejemplo de logs:
     ```
     INFO  Usuario jcf7@gmail.com ha iniciado sesión correctamente
     INFO  Refresh token usado correctamente para generar nuevo access token
     ```

