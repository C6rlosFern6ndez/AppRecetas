# 🥘 API Backend - Recetas App

## 📋 Guía para Desarrolladores Frontend

Esta documentación proporciona toda la información necesaria para consumir y trabajar con la API REST del backend de Recetas App.

---

## 🚀 Información General

### URL Base de Producción


### URL Base de Desarrollo
```
http://localhost:8080/api/
```

### Formato de Respuestas
- **Content-Type**: `application/json`
- **Codificación**: `UTF-8`

### Autenticación
- **Type**: Bearer Token JWT
- **Header**: `Authorization: Bearer {token}`
- **Duración**: 24 horas (86400000 ms)
- **Refresh**: No implementado (necesario re-login)

### Gestión de Imágenes Externas
- **Servicio**: ImgBB (external service)
- **URLs de Imagen**: Directas - no requieren autenticación adicional
- **Almacenamiento**: URLs públicas alojadas en ImgBB
- **Formato**: HTTP URLs estándar

### Cors y Seguridad
- **Origen Permitido**: Configurado para `http://localhost:5173/` (frontend React dev)
- **Headers Permitidos**: `Authorization, Content-Type, Accept`
- **Métodos Permitidos**: `GET, POST, PUT, DELETE, OPTIONS`

### Paginación y Ordenamiento
- **Framework**: Spring Data Pageable
- **Parámetros por defecto**: `page=0, size=20, sort=id,desc`
- **Format**: Zero-based indexing

---

## 🔐 Autenticación y Seguridad

### 1. Registro de Usuario

**Endpoint**: `POST /api/auth/signup`

**Request Body**:
```json
{
  "nombreUsuario": "chef_master",
  "email": "chef@example.com",
  "contrasena": "password123"
}
```

**Validaciones**:
- `nombreUsuario`: 3-50 caracteres, obligatorio, único
- `email`: formato válido, obligatorio, único
- `contraseña`: mínimo 6 caracteres, obligatoria

**Response** (201 Created):
```json
{
  "id": 1,
  "nombreUsuario": "chef_master",
  "email": "chef@example.com",
  "fechaRegistro": "2024-01-01T10:00:00",
  "urlFotoPerfil": null,
  "rol": {
    "id": 1,
    "nombre": "USER"
  }
}
```

### 2. Inicio de Sesión

**Endpoint**: `POST /api/auth/login`

**Request Body**:
```json
{
  "email": "chef@example.com",
  "contrasena": "password123"
}
```

**Response** (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjaGVmX21hc3RlciIsI...",
  "type": "Bearer",
  "id": 1,
  "nombreUsuario": "chef_master",
  "email": "chef@example.com"
}
```

### 3. Cómo Usar el Token JWT

Después de obtener el token, inclúyelo en el header de todas las peticiones protegidas:

```javascript
const response = await fetch('/api/recetas', {
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
});
```

**Duración del Token**: 24 horas (86400000 ms)

---

## 📊 Modelos de Datos

### Usuario
```typescript
interface Usuario {
  id: number;
  nombreUsuario: string;
  email: string;
  urlFotoPerfil?: string;
  fechaRegistro: string; // ISO 8601
  rol: {
    id: number;
    nombre: string; // 'USER' | 'ADMIN' | etc.
  };
}
```

### Receta
```typescript
interface Receta {
  id: number;
  titulo: string;
  descripcion: string;
  tiempoPreparacion: number; // minutos
  dificultad: 'FACIL' | 'MEDIA' | 'DIFICIL';
  porciones: number;
  urlImagen?: string;
  fechaCreacion: string;
  usuario: Usuario;
  categorias: Categoria[];
  pasos: Paso[];
  ingredientes: RecetaIngrediente[];
  comentarios: Comentario[];
  calificaciones: Calificacion[];
  meGustas: MeGustaReceta[];
}
```

### Categoría
```typescript
interface Categoria {
  id: number;
  nombre: string;
}
```

### Ingrediente (Base)
```typescript
interface Ingrediente {
  id: number;
  nombre: string;
}
```

### RecetaIngrediente (Relación)
```typescript
interface RecetaIngrediente {
  recetaId: number;
  ingredienteId: number;
  cantidad: string; // ej: "500g", "2 unidades"
  ingrediente: Ingrediente;
}
```

### Paso
```typescript
interface Paso {
  id: number;
  recetaId: number;
  orden: number;
  descripcion: string;
}
```

### Comentario
```typescript
interface Comentario {
  id: number;
  comentario: string;
  fechaComentario: string; // ISO 8601 LocalDateTime
  usuario: Usuario;
  receta: Receta;
}
```

### Calificación
```typescript
interface Calificacion {
  id: number;
  recetaId: number;
  usuarioId: number;
  puntuacion: number; // 1-5
  fechaCalificacion: string;
}
```

### Seguidores
```typescript
interface Seguidor {
  seguidorId: number;
  seguidoId: number;
  seguidor: Usuario;
  seguido: Usuario;
}
```

### Notificación
```typescript
interface Notificacion {
  id: number;
  usuarioId: number;
  tipo: 'NUEVO_SEGUIDOR' | 'ME_GUSTA_RECETA' | 'NUEVO_COMENTARIO';
  emisorId?: number;
  recetaId?: number;
  mensaje?: string;
  leida: boolean;
  fechaCreacion: string;
}
```

---

## 🛠️ Endpoints de la API

### 👥 Gestión de Usuarios

#### Perfil de Usuario
- **GET** `/api/users/{id}` - Obtener perfil de usuario
- **PUT** `/api/users/{id}` - Actualizar perfil (Auth required)
- **DELETE** `/api/users/{id}` - Eliminar cuenta (Auth required)

#### Relaciones Sociales
- **POST** `/api/users/{id}/follow` - Seguir usuario (Auth required)
- **DELETE** `/api/users/{id}/unfollow` - Dejar de seguir (Auth required)
- **GET** `/api/users/{id}/followers` - Lista de seguidores
- **GET** `/api/users/{id}/following` - Lista de seguidos

### 👩‍🍳 Gestión de Recetas

#### CRUD Básico
- **GET** `/api/recetas` - Listar todas las recetas (con paginación)
- **GET** `/api/recetas/{id}` - Detalle de receta
- **POST** `/api/recetas` - Crear receta (Auth required)
- **PUT** `/api/recetas/{id}` - Actualizar receta (Auth required)
- **DELETE** `/api/recetas/{id}` - Eliminar receta (Auth required)

#### Búsqueda y Filtros
- **GET** `/api/recetas/search` - Buscar recetas con filtros:
  - `titulo` - búsqueda parcial
  - `ingredienteNombre` - búsqueda por ingrediente
  - `dificultad` - FACIL/MEDIA/DIFICIL
  - `tiempoPreparacionMax` - tiempo máximo
  - `categoriaNombre` - búsqueda por categoría

#### Gestión de Imágenes
- **POST** `/api/recetas/{id}/imagen` - Subir imagen (multipart/form-data)
- **DELETE** `/api/recetas/{id}/imagen` - Eliminar imagen

#### Interacciones Sociales
- **POST** `/api/recetas/{id}/like` - Dar/quitar me gusta (Auth required)
- **GET** `/api/recetas/{id}/likes` - Lista de me gustas
- **POST** `/api/recetas/{id}/calificar` - Calificar receta (Auth required)
- **GET** `/api/recetas/{id}/calificacion` - Obtener calificación del usuario

#### Comentarios
- **POST** `/api/recetas/{id}/comments` - Agregar comentario (Auth required)
- **GET** `/api/recetas/{id}/comments` - Lista de comentarios

#### Categorías en Recetas
- **POST** `/api/recetas/{recetaId}/categorias/{categoriaId}` - Agregar categoría
- **DELETE** `/api/recetas/{recetaId}/categorias/{categoriaId}` - Remover categoría

### 🏷️ Gestión de Categorías

- **GET** `/api/categorias` - Lista todas las categorías
- **GET** `/api/categorias/{id}` - Detalle de categoría
- **POST** `/api/categorias` - Crear categoría (Admin required)
- **PUT** `/api/categorias/{id}` - Actualizar categoría (Admin required)
- **DELETE** `/api/categorias/{id}` - Eliminar categoría (Admin required)
- **GET** `/api/categorias/{id}/recetas` - Recetas de una categoría

### 🥕 Gestión de Ingredientes

- **GET** `/api/ingredientes` - Lista todos los ingredientes
- **GET** `/api/ingredientes/{id}` - Detalle de ingrediente
- **POST** `/api/ingredientes` - Crear ingrediente (Auth required)
- **PUT** `/api/ingredientes/{id}` - Actualizar ingrediente (Auth required)
- **DELETE** `/api/ingredientes/{id}` - Eliminar ingrediente (Auth required)

### ⭐ Calificaciones

- **GET** `/api/recetas/{recetaId}/calificaciones` - Calificaciones de una receta

### 🔔 Notificaciones

- **GET** `/api/notificaciones` - Notificaciones del usuario actual
- **PUT** `/api/notificaciones/{id}/read` - Marcar como leída
- **PUT** `/api/notificaciones/read-all` - Marcar todas como leídas
- **DELETE** `/api/notificaciones/{id}` - Eliminar notificación

---

## 📄 Códigos de Estado HTTP

### 2xx - Éxito
- `200 OK` - Operación exitosa
- `201 Created` - Recurso creado
- `204 No Content` - Operación exitosa sin contenido

### 4xx - Error del Cliente
- `400 Bad Request` - Datos inválidos
- `401 Unauthorized` - Token faltante/inválido
- `403 Forbidden` - Sin permisos
- `404 Not Found` - Recurso no encontrado
- `409 Conflict` - Conflicto (ej: usuario ya existe)

### 5xx - Error del Servidor
- `500 Internal Server Error` - Error interno

---

## 🔍 Estructura de Errores

```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "El campo 'titulo' no puede estar vacío",
  "path": "/api/recetas"
}
```

---

## 💡 Ejemplos de Uso con JavaScript/React

### 1. Configuración Axios con Token

```javascript
import axios from 'axios';

// Crear instancia axios
const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api/',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para agregar token automáticamente
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Interceptor para manejo de errores de autenticación
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;
```

### 2. Autenticación Completa

```javascript
import apiClient from './apiClient';

// Login
export const login = async (email, contrasena) => {
  try {
    const response = await apiClient.post('/auth/login', {
      email,
      contrasena
    });

    const { token, nombreUsuario, id } = response.data;
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify({ nombreUsuario, id }));

    return response.data;
  } catch (error) {
    throw error.response?.data || error.message;
  }
};

// Signup
export const signup = async (nombreUsuario, email, contrasena) => {
  try {
    const response = await apiClient.post('/auth/signup', {
      nombreUsuario,
      email,
      contrasena
    });
    return response.data;
  } catch (error) {
    throw error.response?.data || error.message;
  }
};
```

### 3. Gestión de Recetas

```javascript
// Listar recetas con paginación
export const getRecetas = async (page = 0, size = 10) => {
  try {
    const response = await apiClient.get('/recetas', {
      params: { page, size, sort: 'fechaCreacion,desc' }
    });
    return response.data;
  } catch (error) {
    throw error;
  }
};

// Buscar recetas
export const searchRecetas = async (params) => {
  try {
    const response = await apiClient.get('/recetas/search', {
      params
    });
    return response.data;
  } catch (error) {
    throw error;
  }
};

// Crear receta
export const createReceta = async (recetaData) => {
  try {
    const response = await apiClient.post('/recetas', recetaData);
    return response.data;
  } catch (error) {
    throw error;
  }
};

// Dar/quitar me gusta
export const toggleLike = async (recetaId, liked) => {
  try {
    if (liked) {
      await apiClient.delete(`/recetas/${recetaId}/like`);
    } else {
      await apiClient.post(`/recetas/${recetaId}/like`);
    }
    return true;
  } catch (error) {
    throw error;
  }
};
```

### 4. Gestión de Comentarios

```javascript
// Agregar comentario
export const addComment = async (recetaId, comentario) => {
  try {
    const response = await apiClient.post(`/recetas/${recetaId}/comments`, {
      comentario
    });
    return response.data;
  } catch (error) {
    throw error;
  }
};

// Obtener comentarios
export const getComments = async (recetaId) => {
  try {
    const response = await apiClient.get(`/recetas/${recetaId}/comments`);
    return response.data;
  } catch (error) {
    throw error;
  }
};
```

### 5. Gestión de Usuario

```javascript
// Obtener perfil
export const getUserProfile = async (userId) => {
  try {
    const response = await apiClient.get(`/users/${userId}`);
    return response.data;
  } catch (error) {
    throw error;
  }
};

// Seguir/Dejar de seguir
export const toggleFollow = async (userId, following) => {
  try {
    if (following) {
      await apiClient.delete(`/users/${userId}/unfollow`);
    } else {
      await apiClient.post(`/users/${userId}/follow`);
    }
    return true;
  } catch (error) {
    throw error;
  }
};
```

### 6. Subida de Imágenes

```javascript
// Subir imagen de receta
export const uploadRecetaImage = async (recetaId, file) => {
  try {
    const formData = new FormData();
    formData.append('file', file);

    const response = await apiClient.post(`/recetas/${recetaId}/imagen`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data;
  } catch (error) {
    throw error;
  }
};
```

## 📸 Gestión de Imágenes y Archivos

### Servicio de Imágenes: ImgBB
- **Proveedor**: ImgBB (servicio externo gratuito)
- **URLs de retorno**: HTTP URLs públicas directas
- **Almacenamiento**: Imágenes alojadas en servidores de ImgBB
- **Costos**: Gratuito (con límites diarios)
- **Tamaño máximo**: 32MB por imagen
- **Formatos soportados**: JPG, PNG, GIF, etc.

### Cómo Consumir las Imágenes
```javascript
// Las URLs devueltas por la API son directamente accesibles
const receta = await apiClient.get('/recetas/1');

// Para mostrar la imagen en React
<img
  src={receta.urlImagen}
  alt="Imagen de la receta"
  style={{ maxWidth: '400px', height: 'auto' }}
  onError={(e) => {
    // Fallback si la imagen no carga
    e.target.src = '/imagen-default.png';
  }}
/>

// Para descargar/modificar imagen
const downloadImage = async (url) => {
  try {
    const response = await fetch(url);
    const blob = await response.blob();
    const urlBlob = URL.createObjectURL(blob);
    // Usar urlBlob para descargar o procesar
  } catch (error) {
    console.error('Error descargando imagen:', error);
  }
};
```

### Subida de Imágenes
```javascript
// Subir imagen de receta
const uploadRecetaImage = async (recetaId, file) => {
  const formData = new FormData();
  formData.append('file', file);

  // Validar tipo de archivo
  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif'];
  if (!allowedTypes.includes(file.type)) {
    throw new Error('Tipo de archivo no permitido');
  }

  // Validar tamaño (ej: máximo 5MB)
  const maxSize = 5 * 1024 * 1024; // 5MB
  if (file.size > maxSize) {
    throw new Error('Archivo demasiado grande');
  }

  try {
    const response = await apiClient.post(
      `/recetas/${recetaId}/imagen`,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        timeout: 30000, // 30 segundos para subida
      }
    );

    return response.data; // Nueva URL de la imagen
  } catch (error) {
    console.error('Error subiendo imagen:', error);
    throw error;
  }
};
```

### Manejo de Imágenes en Formularios
```javascript
const handleImageUpload = (event) => {
  const file = event.target.files[0];
  if (file) {
    // Previsualizar antes de subir
    const reader = new FileReader();
    reader.onload = (e) => {
      setPreviewImage(e.target.result);
    };
    reader.readAsDataURL(file);

    // Guardar archivo para subir después
    setSelectedFile(file);
  }
};
```

## 📄 Paginación Detallada

### Estructura de Respuestas Paginadas
```typescript
interface PageResponse<T> {
  content: T[];           // Array de elementos
  totalElements: number;  // Total de elementos
  totalPages: number;     // Total de páginas
  size: number;           // Tamaño de página
  number: number;         // Número de página actual (0-based)
  numberOfElements: number; // Elementos en página actual
  first: boolean;         // Es primera página
  last: boolean;          // Es última página
  empty: boolean;         // Página vacía
}
```

### Ejemplo de Uso con Paginación
```javascript
// Listar recetas paginadas
const loadRecetas = async (page = 0, size = 10, sort = 'fechaCreacion,desc') => {
  try {
    const response = await apiClient.get('/recetas', {
      params: { page, size, sort }
    });

    const data = response.data;
    setRecetas(data.content);
    setPagination({
      currentPage: data.number,
      totalPages: data.totalPages,
      totalItems: data.totalElements,
      hasNext: !data.last,
      hasPrev: !data.first
    });
  } catch (error) {
    console.error('Error cargando recetas:', error);
  }
};

// Navegación de páginas
const handlePageChange = (newPage) => {
  loadRecetas(newPage, pageSize);
};

// Buscar con paginación
const searchWithPagination = async (filters, page = 0) => {
  try {
    const response = await apiClient.get('/recetas/search', {
      params: { ...filters, page, size: 10, sort: 'fechaCreacion,desc' }
    });

    return response.data;
  } catch (error) {
    throw error;
  }
};
```

### Componente de Paginación en React
```javascript
const Pagination = ({ pagination, onPageChange }) => {
  if (!pagination || pagination.totalPages <= 1) return null;

  return (
    <div className="pagination">
      <button
        disabled={!pagination.hasPrev}
        onClick={() => onPageChange(pagination.currentPage - 1)}
      >
        Anterior
      </button>

      <span>
        Página {pagination.currentPage + 1} de {pagination.totalPages}
        ({pagination.totalItems} recetas)
      </span>

      <button
        disabled={!pagination.hasNext}
        onClick={() => onPageChange(pagination.currentPage + 1)}
      >
        Siguiente
      </button>
    </div>
  );
};
```

## 🔗 Manejo de Relaciones N:N

### Categorías y Recetas
```javascript
// Agregar categoría a receta
const addCategoryToRecipe = async (recetaId, categoriaId) => {
  try {
    const response = await apiClient.post(
      `/recetas/${recetaId}/categorias/${categoriaId}`
    );
    return response.data; // Receta actualizada con nueva categoría
  } catch (error) {
    throw error;
  }
};

// Remover categoría de receta
const removeCategoryFromRecipe = async (recetaId, categoriaId) => {
  try {
    await apiClient.delete(`/recetas/${recetaId}/categorias/${categoriaId}`);
    return true;
  } catch (error) {
    throw error;
  }
};

// Obtener todas las categorías de una receta
const getRecipeCategories = (receta) => {
  return receta.categorias || [];
};
```

### Ingredientes y Recetas
```javascript
// Los ingredientes se manejan automáticamente en RecetaIngrediente
// Al crear/actualizar una receta, incluye los ingredientes
const createRecipeWithIngredients = async (recipeData) => {
  const payload = {
    ...recipeData,
    ingredientes: [
      { ingredienteId: 1, cantidad: "500g" },
      { ingredienteId: 2, cantidad: "1 unidad" }
    ]
  };

  return await apiClient.post('/recetas', payload);
};
```

## ⏰ Manejo de Fechas y Formatos

### Formatos de Fecha
- **Backend**: `LocalDateTime` (ej: "2024-01-01T10:30:00")
- **Frontend**: Convertir a objetos Date de JavaScript
- **Display**: Formatear según locale del usuario

### Utilidades de Fecha
```javascript
// Convertir string de backend a Date
const parseBackendDate = (dateString) => {
  return new Date(dateString);
};

// Formatear para display
const formatDate = (date) => {
  return date.toLocaleDateString('es-ES', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// Calcular tiempo relativo
const getRelativeTime = (dateString) => {
  const date = parseBackendDate(dateString);
  const now = new Date();
  const diffInHours = Math.floor((now - date) / (1000 * 60 * 60));

  if (diffInHours < 1) return 'Hace menos de una hora';
  if (diffInHours < 24) return `Hace ${diffInHours} horas`;
  const diffInDays = Math.floor(diffInHours / 24);
  return `Hace ${diffInDays} días`;
};

// Ejemplo de uso en componente
const CommentItem = ({ comment }) => (
  <div className="comment">
    <p>{comment.comentario}</p>
    <small>
      Por {comment.usuario.nombreUsuario} - {getRelativeTime(comment.fechaComentario)}
    </small>
  </div>
);
```

## 🔒 Headers y Seguridad Adicional

### Headers Requeridos
```javascript
// Para peticiones con token
const authHeaders = {
  'Authorization': `Bearer ${token}`,
  'Content-Type': 'application/json'
};

// Para subida de archivos
const uploadHeaders = {
  'Authorization': `Bearer ${token}`,
  'Content-Type': 'multipart/form-data'
};
```

### Timeouts Recomendados
```javascript
const TIMEOUTS = {
  DEFAULT: 10000,     // 10 segundos para requests normales
  UPLOAD: 30000,      // 30 segundos para uploads
  LONG_RUNNING: 60000 // 60 segundos para operaciones complejas
};

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api/',
  timeout: TIMEOUTS.DEFAULT
});
```

### Manejo de Rate Limiting
- **Actual**: No implementado
- **Recomendación**: Verificar respuesta 429 si se implementa posteriormente
- **Estrategia**: Implementar exponential backoff en frontend

```javascript
// Manejo futuro de rate limiting
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 429) {
      // Rate limit alcanzado
      const retryAfter = error.response.headers['retry-after'];
      console.warn(`Rate limited. Retry after ${retryAfter} seconds`);
      // Implementar lógica de retry con exponential backoff
    }
    return Promise.reject(error);
  }
);
```

---

## 🧪 Datos de Prueba Disponibles

### Usuarios de Prueba
| Usuario | Email | Contraseña | Notas |
|---------|-------|------------|--------|
| chef_master | chef@example.com | password | Usuario con recetas |
| food_lover | foodlover@example.com | password | Usuario seguidor |
| admin_user | admin@example.com | password | Usuario administrador |

### Claves de Acceso de Prueba
- Todas las contraseñas son: `password`
- Los hashes de contraseña en la BD están generados con BCrypt

### Recetas Disponibles
1. **Espinacas con Garbanzos** (ID: 1)
2. **Tortilla Española** (ID: 2)
3. **Paella Valenciana** (ID: 3)
4. **Crema Catalana** (ID: 4)

### Categorías Predefinidas
- Postres
- Comida Saludable
- Vegetariano
- Carnes
- Pescados y Mariscos
- Pasta
- Guarniciones
- Desayunos

### Ingredientes Comunes
- Espinacas, Garbanzos, Aceite de oliva, Ajo, Huevos, Harina, Leche, etc.

---

## 🔄 Versionado de API

### Versionado Actual
- **Versión**: `v1`
- **Date**: Octubre 2025

### Cambios Propuestos
- **Campos opcionales**: Hacer opcional urlImagen en Receta
- **Nuevos filtros**: Agregar filtro por rango de calificaciones
- **Paginación mejorada**: Incluir metadata completa de paginación

### Compatibilidad
- La API mantiene retrocompatibilidad
- Se comunica el deprecado 6 meses antes de removerlo

---

## 📞 Soporte y Contacto

### Documentación Relacionada
- [README.md](./README.md) - Documentación completa del backend
- [Instrucciones_terminal.txt](./Instrucciones_terminal.txt) - Comandos útiles

### Documentación OpenAPI/Swagger
- **URL**: `http://localhost:8080/swagger-ui/index.html`
- **JSON**: `http://localhost:8080/v3/api-docs`

### Equipo de Desarrollo
- **Backend Developer**: Carlos Fernández González
- **Frontend Developer**: Carlos Fernández González

### Reportar Problemas
- Crea un issue en el repositorio con detalles del error
- Incluye: URL, método HTTP, body, headers, y respuesta de error

---

## 🎉 ¡Comienza Ahora!

1. **Configura el entorno** - Levanta el backend local
2. **Registra un usuario** - O usa los datos de prueba
3. **Obtén tu token JWT** - Haz login para conseguir tokens
4. **Explora la API** - Usa Swagger UI para familiarizarte
5. **¡Contruye tu frontend!** - Todas las rutas están documentadas aquí

¿Necesitas ayuda? Revisa la documentación de Swagger integrada o contacta al equipo de backend. ¡Éxito con tu desarrollo frontend! 🚀
