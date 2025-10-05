# Backend de Recetas App

Aplicación backend para una aplicación de recetas construida con Spring Boot 3.3.4. Esta aplicación permite gestionar recetas culinarias, usuarios, categorías, ingredientes, calificaciones, comentarios, seguidores y notificaciones en una plataforma social gastronómica.

## Tecnologías Utilizadas

### Core Framework
- **Java 17** - Lenguaje de programación principal
- **Spring Boot 3.3.4** - Framework principal para desarrollo web
- **Maven 3.6+** - Gestión de dependencias y construcción

### Seguridad y Autenticación
- **Spring Security** - Framework de seguridad
- **JWT (JSON Web Tokens)** - Autenticación stateless
- **BCrypt** - Hashing de contraseñas

### Base de Datos y Persistencia
- **Spring Data JPA** - Mapeo objeto-relacional
- **MariaDB** - Base de datos relacional principal
- **Hibernate** - ORM para JPA
- **Flyway** - Migraciones de base de datos versionadas
- **H2 Database** - Base de datos en memoria para testing

### API y Documentación
- **Spring Web** - Framework REST
- **SpringDoc OpenAPI 2.6.0** - Documentación Swagger/OpenAPI
- **Jackson** - Serialización/deserialización JSON

### Validación y Utilidades
- **Spring Validation** - Validación de datos
- **Lombok** - Reducción de código boilerplate
- **MapStruct** - Mapeo entre objetos
- **Apache Commons** - Utilidades generales

### Sesiones y Estado
- **Spring Session JDBC** - Gestión de sesiones en base de datos
- **JDBC** - Acceso directo a base de datos

### Testing
- **JUnit 5** - Framework de testing unitario
- **Spring Boot Test** - Testing de integración
- **Mockito** - Mocks para testing
- **AssertJ** - Aserciones expresivas

### Desarrollo
- **Spring Boot DevTools** - Herramientas de desarrollo
- **Checkstyle** - Análisis de estilo de código

### Versiones de Dependencias Clave
- MariaDB Connector: 3.5.6
- Mockito: 5.11.0
- Byte Buddy: 1.14.13
- Lombok: 1.18.32
- MapStruct: 1.5.5.Final

## Configuración del Entorno de Desarrollo

### Prerrequisitos

1. **Java 17** instalado
2. **Maven 3.6+** instalado
3. **MariaDB** corriendo en localhost:3336
4. **Base de datos** `recetas_db` creada

### Variables de Entorno

Configura las siguientes variables de entorno para la base de datos:

```bash
export DB_USERNAME=tu_usuario_de_db
export DB_PASSWORD=tu_password_de_db
```

Si no se configuran, se usan valores por defecto (usuario: `root`, password: `123456`).

Para el plugin de Flyway en Maven:

```bash
mvn -Dflyway.user=tu_usuario -Dflyway.password=tu_password flyway:migrate
```

### Compilar y Ejecutar

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar migraciones de base de datos (opcional)
mvn flyway:migrate

# Ejecutar la aplicación
mvn spring-boot:run

# Ejecutar tests
mvn test
```

### Perfiles de Spring

La aplicación utiliza perfiles de Spring para diferentes entornos:

- **dev** (desarrollo): Activa por defecto, incluye configuración de logging y Hibernate en modo `update`
- **prod** (producción): Para despliegue en producción
- **test** (pruebas): Para ejecución de tests unitarios e integración

### Acceder a la Aplicación

- **API REST**: `http://localhost:8080/api/`
- **Documentación Swagger**: `http://localhost:8080/swagger-ui/index.html`

## Arquitectura

### Capas de la Aplicación

1. **Controllers**: Manejan las peticiones HTTP y respuestas
2. **Services**: Contienen la lógica de negocio
3. **Repositories**: Manejan el acceso a datos con JPA
4. **Security**: Configuración de autenticación JWT y autorización

### Principales Entidades

- **Usuario**: Usuarios registrados con autenticación
- **Receta**: Recetas de cocina con ingredientes y pasos
- **Categoría**: Clasificación de recetas
- **Ingrediente**: Componentes de las recetas
- **Calificación**: Sistema de puntuaciones
- **Comentario**: Comentarios en recetas
- **Notificación**: Sistema de notificaciones
- **Seguidor**: Relaciones entre usuarios

## Testing

Los tests se ejecutan automáticamente con `mvn test`. Se incluyen tests unitarios para servicios y tests de integración para controladores.

Cobertura de tests incluye:
- Controllers
- Services
- Security configuration
- Image upload functionality

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/recetas/backend/
│   │   ├── App.java                           # Clase principal de Spring Boot
│   │   ├── config/
│   │   │   ├── SecurityConfig.java            # Configuración de Spring Security
│   │   │   └── SwaggerConfig.java             # Configuración de Swagger/OpenAPI
│   │   ├── controller/                        # Controllers REST
│   │   │   ├── AuthController.java            # Autenticación y registro
│   │   │   ├── CalificacionController.java    # Calificaciones de recetas
│   │   │   ├── CategoriaController.java       # Gestión de categorías
│   │   │   ├── ComentarioController.java      # Comentarios en recetas
│   │   │   ├── IngredienteController.java     # Gestión de ingredientes
│   │   │   ├── NotificacionController.java    # Notificaciones
│   │   │   ├── RecetaController.java          # CRUD de recetas
│   │   │   └── UserController.java            # Gestión de usuarios
│   │   ├── domain/
│   │   │   ├── dto/                           # Data Transfer Objects
│   │   │   │   ├── LoginRequestDto.java
│   │   │   │   ├── LoginResponseDto.java
│   │   │   │   ├── RecetaRequestDto.java
│   │   │   │   └── SignupRequestDto.java
│   │   │   ├── entity/                        # Entidades JPA
│   │   │   │   ├── Calificacion.java
│   │   │   │   ├── Categoria.java
│   │   │   │   ├── Comentario.java
│   │   │   │   ├── Ingrediente.java
│   │   │   │   ├── MeGustaReceta.java
│   │   │   │   ├── MeGustaRecetaId.java
│   │   │   │   ├── Notificacion.java
│   │   │   │   ├── Paso.java
│   │   │   │   ├── Receta.java
│   │   │   │   ├── RecetaIngrediente.java
│   │   │   │   ├── RecetaIngredienteId.java
│   │   │   │   ├── Rol.java
│   │   │   │   ├── Seguidor.java
│   │   │   │   ├── SeguidorId.java
│   │   │   └── Usuario.java
│   │   │   ├── mapper/
│   │   │   │   └── RecetaMapper.java          # MapStruct mappers
│   │   │   ├── model/
│   │   │   │   └── enums/
│   │   │   │       ├── Dificultad.java        # Enum dificultad receta
│   │   │   │       └── TipoNotificacion.java  # Enum tipos notificación
│   │   │   └── repository/                     # Repositories JPA
│   │   │       ├── CalificacionRepository.java
│   │   │       ├── CategoriaRepository.java
│   │   │       ├── ComentarioRepository.java
│   │   │       ├── IngredienteRepository.java
│   │   │       ├── MeGustaRecetaRepository.java
│   │   │       ├── NotificacionRepository.java
│   │   │       ├── PasoRepository.java
│   │   │       ├── RecetaIngredienteRepository.java
│   │   │       ├── RecetaRepository.java
│   │   │       ├── RolRepository.java
│   │   │       ├── SeguidorRepository.java
│   │   │       └── UsuarioRepository.java
│   │   ├── security/
│   │   │   ├── AuthEntryPointJwt.java         # Punto de entrada JWT
│   │   │   ├── AuthTokenFilter.java           # Filtro de tokens
│   │   │   └── JwtUtils.java                  # Utilidades JWT
│   │   └── service/
│   │       ├── ImageUploadService.java        # Subida de imágenes
│   │       ├── impl/                          # Implementaciones de servicios
│   │       │   ├── CalificacionServiceImpl.java
│   │       │   ├── CategoriaServiceImpl.java
│   │       │   ├── ComentarioServiceImpl.java
│   │       │   ├── IngredienteServiceImpl.java
│   │       │   ├── NotificacionServiceImpl.java
│   │       │   ├── RecetaServiceImpl.java
│   │       │   ├── UserDetailsServiceImpl.java
│   │       │   └── UserServiceImpl.java
│   │       ├── CalificacionService.java       # Interfaces de servicios
│   │       ├── CategoriaService.java
│   │       ├── ComentarioService.java
│   │       ├── IngredienteService.java
│   │       ├── NotificacionService.java
│   │       ├── RecetaService.java
│   │       └── UserService.java
│   ├── resources/
│   │   ├── application-prod.properties        # Configuración producción
│   │   ├── application.properties             # Configuración base
│   │   ├── application-dev.properties         # Configuración desarrollo
│   │   ├── application-test.properties        # Configuración testing
│   │   ├── db/migration/
│   │   │   └── V1__Initial_Schema.sql         # Migración inicial
│   │   └── META-INF/
│   │       └── additional-spring-configuration-metadata.json
│   └── test/
│       ├── java/com/recetas/backend/
│       │   ├── config/
│       │   │   ├── SecurityConfigTest.java
│       │   │   └── TestWebConfig.java
│       │   ├── controller/
│       │   │   ├── AuthControllerTest.java
│       │   │   ├── CalificacionControllerTest.java
│       │   │   ├── CategoriaControllerTest.java
│       │   │   ├── IngredienteControllerTest.java
│       │   │   ├── NotificacionControllerTest.java
│       │   │   ├── RecetaControllerTest.java
│       │   │   └── UserControllerTest.java
│       │   ├── service/
│       │   │   └── ImageUploadServiceTest.java
│       │   ├── service/impl/
│       │   │   ├── CalificacionServiceImplTest.java
│       │   │   ├── CategoriaServiceImplTest.java
│       │   │   ├── ComentarioServiceImplTest.java
│       │   │   ├── IngredienteServiceImplTest.java
│       │   │   ├── NotificacionServiceImplTest.java
│       │   │   ├── RecetaServiceImplTest.java
│       │   │   ├── UserDetailsServiceImplTest.java
│       │   │   └── UserServiceImplTest.java
│       │   └── BackendApplicationTests.java
│       └── resources/
│           └── application-test.properties
├── DataBase/
│   └── Tablas.sql                             # Esquema completo de BD
├── pom.xml                                    # Configuración Maven
├── mvnw                                       # Wrapper Maven (Linux/Mac)
├── mvnw.cmd                                   # Wrapper Maven (Windows)
├── checkstyle.xml                             # Configuración Checkstyle
├── Instrucciones_terminal.txt                 # Comandos útiles
├── test_login.ps1                             # Script test login
└── .gitignore                                 # Archivos ignorados Git
```

## Configuración Avanzada

### application.properties (Base)
```properties
spring.application.name=backend
spring.datasource.url=jdbc:mariadb://localhost:3336/recetas_db
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:123456}
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.profiles.active=dev
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=1
spring.flyway.validate-on-migrate=true
spring.flyway.clean-disabled=true
spring.session.jdbc.initialize-schema=never
spring.jpa.open-in-view=false
app.jwt.secret=mySuperSecretJwtKeyWith256BitsLengthForSecurityPurposes123
app.jwt.expirationMs=86400000
logging.level.org.springframework=WARN
logging.level.org.hibernate=ERROR
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql=TRACE
```

### application-dev.properties
```properties
spring.jpa.hibernate.ddl-auto=update
logging.level.org.springframework.security=DEBUG
```

### application-prod.properties
```properties
spring.jpa.hibernate.ddl-auto=validate
logging.level.root=INFO
server.error.include-message=never
```

### application-test.properties
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.flyway.enabled=false
logging.level.org.springframework=WARN
```

## API Endpoints Completos

### Autenticación (`/api/auth`)
- `POST /api/auth/signup` - Registro de nuevo usuario
- `POST /api/auth/login` - Inicio de sesión y generación de JWT

### Usuarios (`/api/users`)
- `GET /api/users` - Listar todos los usuarios
- `GET /api/users/{id}` - Obtener usuario por ID
- `PUT /api/users/{id}` - Actualizar perfil de usuario
- `DELETE /api/users/{id}` - Eliminar usuario
- `GET /api/users/{id}/followers` - Seguidores del usuario
- `GET /api/users/{id}/following` - Usuarios seguidos
- `POST /api/users/{id}/follow` - Seguir usuario
- `DELETE /api/users/{id}/unfollow` - Dejar de seguir usuario

### Recetas (`/api/recetas`)
- `GET /api/recetas` - Listar todas las recetas (con filtros opcionales)
- `GET /api/recetas/{id}` - Detalle completo de receta
- `POST /api/recetas` - Crear nueva receta (requiere auth)
- `PUT /api/recetas/{id}` - Actualizar receta (solo autor)
- `DELETE /api/recetas/{id}` - Eliminar receta (solo autor)
- `POST /api/recetas/{id}/like` - Dar/quitar like a receta
- `GET /api/recetas/{id}/likes` - Listar likes de receta
- `GET /api/recetas/search` - Buscar recetas por título/categoría

### Categorías (`/api/categorias`)
- `GET /api/categorias` - Listar todas las categorías
- `GET /api/categorias/{id}` - Detalle de categoría
- `POST /api/categorias` - Crear categoría (admin)
- `PUT /api/categorias/{id}` - Actualizar categoría (admin)
- `DELETE /api/categorias/{id}` - Eliminar categoría (admin)
- `GET /api/categorias/{id}/recetas` - Recetas de una categoría

### Ingredientes (`/api/ingredientes`)
- `GET /api/ingredientes` - Listar todos los ingredientes
- `GET /api/ingredientes/{id}` - Detalle de ingrediente
- `POST /api/ingredientes` - Crear ingrediente (requiere auth)
- `PUT /api/ingredientes/{id}` - Actualizar ingrediente
- `DELETE /api/ingredientes/{id}` - Eliminar ingrediente

### Calificaciones (`/api/calificaciones`)
- `GET /api/recetas/{recetaId}/calificaciones` - Calificaciones de receta
- `POST /api/recetas/{recetaId}/calificaciones` - Crear calificación
- `PUT /api/calificaciones/{id}` - Actualizar calificación (solo autor)
- `DELETE /api/calificaciones/{id}` - Eliminar calificación (solo autor)
- `GET /api/calificaciones/{id}` - Detalle de calificación

### Comentarios (`/api/comentarios`)
- `GET /api/recetas/{recetaId}/comentarios` - Comentarios de receta
- `POST /api/recetas/{recetaId}/comentarios` - Crear comentario
- `PUT /api/comentarios/{id}` - Actualizar comentario (solo autor)
- `DELETE /api/comentarios/{id}` - Eliminar comentario (solo autor)
- `GET /api/comentarios/{id}` - Detalle de comentario

### Notificaciones (`/api/notificaciones`)
- `GET /api/notificaciones` - Notificaciones del usuario actual
- `PUT /api/notificaciones/{id}/read` - Marcar como leída
- `PUT /api/notificaciones/read-all` - Marcar todas como leídas
- `DELETE /api/notificaciones/{id}` - Eliminar notificación

## Base de Datos

### Esquema Completo

La aplicación utiliza una base de datos relacional MariaDB con el siguiente esquema:

#### Tablas Principales:

**1. roles**
- `id` (INT, PK)
- `nombre` (VARCHAR(20), UNIQUE)

**2. usuarios**
- `id` (INT, PK)
- `nombre_usuario` (VARCHAR(50), UNIQUE)
- `email` (VARCHAR(100), UNIQUE)
- `contrasena` (VARCHAR(255))
- `url_foto_perfil` (VARCHAR(255), NULL)
- `delete_hash_perfil` (VARCHAR(255), NULL)
- `fecha_registro` (TIMESTAMP)
- `rol_id` (INT, FK → roles.id)

**3. seguidores**
- `seguidor_id` (INT, PK, FK → usuarios.id)
- `seguido_id` (INT, PK, FK → usuarios.id)

**4. recetas**
- `id` (INT, PK)
- `titulo` (VARCHAR(100))
- `descripcion` (TEXT)
- `tiempo_preparacion` (INT)
- `dificultad` (ENUM: 'Fácil', 'Media', 'Difícil')
- `porciones` (INT)
- `url_imagen` (VARCHAR(255), NULL)
- `delete_hash_imagen` (VARCHAR(255), NULL)
- `usuario_id` (INT, FK → usuarios.id)
- `fecha_creacion` (TIMESTAMP)

**5. pasos**
- `id` (INT, PK)
- `receta_id` (INT, FK → recetas.id)
- `orden` (INT)
- `descripcion` (TEXT)

**6. categorias**
- `id` (INT, PK)
- `nombre` (VARCHAR(50), UNIQUE)

**7. receta_categorias**
- `receta_id` (INT, PK, FK → recetas.id)
- `categoria_id` (INT, PK, FK → categorias.id)

**8. ingredientes**
- `id` (INT, PK)
- `nombre` (VARCHAR(100), UNIQUE)

**9. receta_ingredientes**
- `receta_id` (INT, PK, FK → recetas.id)
- `ingrediente_id` (INT, PK, FK → ingredientes.id)
- `cantidad` (VARCHAR(50), NULL)

**10. comentarios**
- `id` (INT, PK)
- `receta_id` (INT, FK → recetas.id)
- `usuario_id` (INT, FK → usuarios.id)
- `comentario` (TEXT)
- `fecha_comentario` (TIMESTAMP)

**11. calificaciones**
- `id` (INT, PK)
- `receta_id` (INT, FK → recetas.id)
- `usuario_id` (INT, FK → usuarios.id)
- `puntuacion` (INT)
- `fecha_calificacion` (TIMESTAMP)
- UNIQUE: (receta_id, usuario_id)

**12. recetas_likes**
- `usuario_id` (INT, PK, FK → usuarios.id)
- `receta_id` (INT, PK, FK → recetas.id)

**13. notificaciones**
- `id` (INT, PK)
- `usuario_id` (INT, FK → usuarios.id)
- `tipo` (ENUM: 'NUEVO_SEGUIDOR', 'ME_GUSTA_RECETA', 'NUEVO_COMENTARIO')
- `emisor_id` (INT, NULL, FK → usuarios.id)
- `receta_id` (INT, NULL, FK → recetas.id)
- `mensaje` (TEXT, NULL)
- `leida` (BOOLEAN, DEFAULT FALSE)
- `fecha_creacion` (TIMESTAMP)

#### Tablas de Sesión (Spring Session):
**SPRING_SESSION**
- `SESSION_ID` (CHAR(36), PK)
- `CREATION_TIME` (BIGINT)
- `LAST_ACCESS_TIME` (BIGINT)
- `MAX_INACTIVE_INTERVAL` (INT)
- `EXPIRY_TIME` (BIGINT)
- `PRINCIPAL_NAME` (VARCHAR(100))
- `SESSION_ATTRIBUTES` (BLOB)

**SPRING_SESSION_ATTRIBUTES**
- `SESSION_ID` (CHAR(36), PK, FK → SPRING_SESSION.SESSION_ID)
- `ATTRIBUTE_NAME` (VARCHAR(200), PK)
- `ATTRIBUTE_BYTES` (BLOB)

### Diagrama de Relaciones

```
usuarios (1)────┬─── (N) seguidores (seguidor_id)
                │
                ├─── (1) recetas (usuario_id)
                │
                ├─── (N) comentarios (usuario_id)
                │
                ├─── (N) calificaciones (usuario_id)
                │
                └─── (N) recetas_likes (usuario_id)

recetas (1)────┬─── (N) pasos (receta_id)
               │
               ├─── (N) receta_categorias (receta_id)
               │
               ├─── (N) receta_ingredientes (receta_id)
               │
               ├─── (N) comentarios (receta_id)
               │
               ├─── (N) calificaciones (receta_id)
               │
               └─── (N) recetas_likes (receta_id)

categorias (1) ─── (N) receta_categorias (categoria_id)

ingredientes (1) ── (N) receta_ingredientes (ingrediente_id)

usuarios (rol_id) ──── (1) roles

[Spring Session JDBC Tables for session management]
SPRING_SESSION ──── (1:N) SPRING_SESSION_ATTRIBUTES
```

### Migraciones Flyway

Las migraciones de base de datos se gestionan con Flyway y están ubicadas en `src/main/resources/db/migration/`.

**Comandos importantes:**
```bash
# Ejecutar migraciones
mvn flyway:migrate

# Limpiar base de datos (solo desarrollo)
mvn flyway:clean

# Ver estado de migraciones
mvn flyway:status

# Reparar checksums de migraciones
mvn flyway:repair
```

**Configuración Flyway en pom.xml:**
```xml
<plugin>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-maven-plugin</artifactId>
  <configuration>
    <url>jdbc:mariadb://localhost:3336/recetas_db</url>
    <user>${flyway.user}</user>
    <password>${flyway.password}</password>
    <locations>
      <location>classpath:db/migration</location>
    </locations>
    <baselineOnMigrate>true</baselineOnMigrate>
    <baselineVersion>1</baselineVersion>
  </configuration>
</plugin>
```

### Datos de Prueba

La migración inicial incluye datos de prueba:
- 3 usuarios de prueba (chef_master, food_lover, admin_user)
- 4 recetas de ejemplo completas
- Categorías predefinidas (Postres, Comida Saludable, Vegetariano, etc.)
- Ingredientes comunes
- Comentarios y calificaciones de ejemplo
- Relaciones de seguidores y likes
- Notificaciones de ejemplo

## Seguridad

- **JWT Authentication**: Tokens stateless para autenticación
- **CORS**: Configurado para frontend específico
- **Password Encoding**: BCrypt para hashing de contraseñas
- **Authorization**: Basada en roles de usuario

## Despliegue

Para producción, configurar:
1. Variables de entorno para credenciales
2. Perfil `prod` activo
3. Base de datos dedicada
4. Logging apropiado para producción

## Desarrollo Avanzado

### Comandos Útiles del Proyecto

**Compilación y Ejecución:**
```bash
# Desarrollo completo
mvn clean install
mvn spring-boot:run

# Compilación rápida
mvn compile

# Empaquetado para distribución
mvn clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar

# Construcción para producción
mvn clean package -Pprod
```

**Testing:**
```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests con cobertura (si está configurado)
mvn test jacoco:report

# Ejecutar tests de integración
mvn verify

# Tests específicos
mvn test -Dtest=UserServiceImplTest
mvn test -Dtest=*ControllerTest
```

**Code Quality:**
```bash
# Verificar estilo de código
mvn checkstyle:check

# Generar reportes
mvn site
```

### Estructura de Testing

Los tests están organizados siguiendo la estructura del código fuente:

#### Tests Unitarios:
- **Service Tests**: Prueban lógica de negocio aislada
- **Repository Tests**: Prueban consultas JPA
- **Mapper Tests**: Prueban mapeos entre objetos

#### Tests de Integración:
- **Controller Tests**: Prueban endpoints REST completos
- **Security Tests**: Prueban configuración de seguridad
- **Database Tests**: Usan H2 en memoria

#### Ejemplos de Tests Incluidos:
```java
// Test de controlador
@SpringBootTest
@AutoConfigureMockMvc
class RecetaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void crearReceta_debeRetornar201_cuandoEsValida() throws Exception {
        // Test implementation
    }
}

// Test de servicio
@ExtendWith(MockitoExtension.class)
class RecetaServiceImplTest {
    @Mock
    private RecetaRepository recetaRepository;

    @InjectMocks
    private RecetaServiceImpl recetaService;
}
```

### Configuración de Logging

**Configuración por Perfiles:**

**Desarrollo (dev):**
- Spring Security: DEBUG
- Hibernate SQL: DEBUG
- Hibernate Types: TRACE

**Producción (prod):**
- Root logger: INFO
- Errores de servidor: No incluidos en responses

**Testing (test):**
- Spring: WARN (menos verboso)

### Scripts de Desarrollo

**test_login.ps1**: Script PowerShell para testing de autenticación
```powershell
# Ejemplo de uso del script
# Define las credenciales
$username = "testuser"
$password = "testpass"
# Ejecuta el login
./test_login.ps1
```

### Convenciones de Código

**Nombres de Clases:**
- Controllers terminan en `Controller`
- Services terminan en `Service`
- Implementaciones terminan en `Impl`
- DTOs terminan en `Dto` o `RequestDto`/`ResponseDto`

**Estructura de Paquetes:**
- `com.recetas.backend` - Raíz del proyecto
- `controller` - Endpoints REST
- `service` - Lógica de negocio
- `domain` - Modelo de datos
- `security` - Configuración de seguridad

**Comentarios:**
- Todos los métodos públicos tienen JavaDoc
- Comentarios en español para funcionalidades complejas
- Todo el código generado por IA incluye comentarios descriptivos

### Manejo de Errores

**Estructura de Respuestas de Error:**
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "El campo 'titulo' es obligatorio",
  "path": "/api/recetas"
}
```

**Excepciones Personalizadas:**
- ValidationException
- ResourceNotFoundException
- UnauthorizedException
- ConflictException

### Despliegue y DevOps

**Variables de Entorno Necesarias:**
```bash
# Base de datos
DB_USERNAME=prod_user
DB_PASSWORD=prod_password
SPRING_PROFILES_ACTIVE=prod

# JWT
JWT_SECRET=your_production_secret_key_here

# Opcionales
SPRING_DATASOURCE_URL=jdbc:mariadb://prod-host:3306/prod_db
```

**Configuración Docker (si se implementa):**
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/backend-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

**Monitoreo:**
- Health Check: `GET /actuator/health`
- Métricas: `GET /actuator/metrics`
- Info: `GET /actuator/info`

### Contribución

**Proceso de Desarrollo:**
1. Crear rama feature desde `main`
2. Desarrollar siguiendo las convenciones
3. Ejecutar tests completos
4. Verificar estilo de código con Checkstyle
5. Crear Pull Request
6. Code Review aprobado
7. Merge a `main`

**Pre-commit Hooks:**
```bash
# Ejecutar antes de cada commit
mvn test
mvn checkstyle:check
mvn compile
```

### Soporte y Mantenimiento

**Enlaces Útiles:**
- Repositorio: https://github.com/C6rlosFern6ndez/AppRecetas
- Documentación Swagger: `http://localhost:8080/swagger-ui/index.html`
- H2 Console (test): `http://localhost:8080/h2-console`

**Contactos:**
- Desarrollador: [Carlos Fernández González]
- Email: [c6rlosfern6ndez@gmail.com]

## Licencia

Este proyecto está bajo la Licencia especificada en `LICENCE.md`.
