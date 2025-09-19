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
