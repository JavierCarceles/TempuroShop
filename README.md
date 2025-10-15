<<<<<<< HEAD
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

=======
## Descripción del Proyecto
**TempuroShop** es un MVP de tienda online construido con arquitectura de microservicios. Actualmente, el proyecto incluye un servicio de autenticación (`auth-service`) y un frontend en React. La idea es escalar progresivamente, incorporando microservicios para productos, pedidos y otros módulos típicos de un e-commerce.

El proyecto utiliza **Java 17**, **Spring Boot**, **JWT**, **Spring Security**, **JPA/Hibernate**, y herramientas de **observabilidad** como **OpenTelemetry** y **Jaeger**, junto con **SonarQube** y **Docker** para CI/CD y monitoreo de calidad de código.

---

## Estado Actual

### Backend (`auth-service`)
- **Autenticación y autorización** con JWT.
  - **Access Tokens** y **Refresh Tokens** persistidos en base de datos.
  - Refresh tokens enviados mediante **cookies** seguras.
- **Seguridad** implementada con Spring Security.
- **Persistencia**:
  - Base de datos principal: **MySQL**
  - Base de datos de pruebas: **H2**
  - Uso de **JPA/Hibernate** para manejo de entidades.
- **Trazabilidad y observabilidad**:
  - Integración con **OpenTelemetry**
  - Visualización de trazas con **Jaeger**
  - Seguimiento de spans en operaciones críticas (login, refresh token, registro)
- **Gestión de código y calidad**:
  - Proyecto dockerizado para desarrollo y despliegue.
  - Integración con **SonarQube** para análisis de calidad de código.
- **Servicios disponibles**:
  - Login
  - Registro de usuarios
  - Refresh de JWT
- **Frontend (React)**:
  - Login
  - Registro
  - Ventana principal de la tienda con productos de ejemplo (estáticos por ahora)

---

## Tecnologías Utilizadas

| Capa                  | Tecnología / Herramienta                    |
|-----------------------|--------------------------------------------|
| Lenguaje              | Java 17                                    |
| Framework Backend     | Spring Boot, Spring Security, JPA/Hibernate|
| Base de Datos         | MySQL, H2                                  |
| Seguridad             | JWT, Refresh Tokens, Cookies               |
| Observabilidad        | OpenTelemetry, Jaeger                      |
| Contenedores          | Docker                                     |
| Calidad de Código     | SonarQube                                  |
| Frontend              | React                                      |
| Build / Run           | Maven                                      |

---

## Próximos Pasos / Roadmap

1. **Microservicio de Productos**
   - Cargar productos desde la base de datos.
   - CRUD de productos.
   - Integración con frontend.

2. **API Gateway**
   - Implementar un gateway para manejar routing, seguridad y autenticación centralizada.
   - Posible uso de **Spring Cloud Gateway** o **Zuul**.

3. **Microservicios adicionales**
   - Pedidos (orders)
   - Carrito de compras
   - Pagos / checkout

4. **Mejoras en Observabilidad**
   - Métricas adicionales con **Prometheus / Grafana**
   - Trazabilidad extendida entre microservicios.
>>>>>>> a7531e7494a3a91e56317dc315a531d4255b7256
