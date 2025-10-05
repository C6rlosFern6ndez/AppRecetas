# Lista de Tareas para el Desarrollo del Frontend

Este archivo contiene todas las tareas necesarias para desarrollar el frontend de la aplicación de recetas.

## Fase 1: Configuración y Autenticación

- [x] Instalar dependencias (`react-router-dom`, `sass`).
- [x] Configurar la estructura de carpetas según el `README.md`.
- [x] Implementar la página `LoginPage` con su formulario, funcionalidad de inicio de sesión y validación de contraseña.
- [x] Implementar la página `SignupPage` con su formulario, funcionalidad de registro y validación de contraseña.
- [x] Crear archivos SCSS para las páginas de autenticación (`LoginPage.scss`, `SignupPage.scss`).
- [x] Implementar el `AuthContext` para gestionar el estado de autenticación (revisado y correcto).
- [x] Implementar el `authService` para las peticiones de login y registro (refactorizado para usar `axios`, corregido el mapeo de contraseña y el nombre del campo de email).
- [x] Configurar el enrutador principal en `App.jsx`.
- [x] Implementar rutas protegidas para usuarios autenticados.
- [x] Crear componentes `Navbar` y `Footer`.

## Fase 2: Visualización de Recetas

- [ ] Crear la página `HomePage` para mostrar el feed de recetas.
- [ ] Crear el componente `RecipeCard` para la vista previa de recetas.
- [ ] Crear la página `RecipeDetailsPage` para la vista completa de una receta.
- [ ] Implementar el `recipeService` con la función para obtener todas las recetas.
- [ ] Implementar el `recipeService` con la función para obtener una receta por su ID.
- [ ] Mostrar los detalles de la receta (ingredientes, pasos, etc.) en `RecipeDetailsPage`.

## Fase 3: Perfil de Usuario y Funcionalidades Sociales

- [ ] Crear la página `ProfilePage` para mostrar la información del usuario.
- [ ] Implementar el `userService` para obtener datos de un usuario, sus seguidores y seguidos.
- [ ] Añadir botones para seguir y dejar de seguir en `ProfilePage`.
- [ ] Implementar la lógica de "me gusta" y "quitar me gusta" en `RecipeDetailsPage`.
- [ ] Crear el componente `Comment` para mostrar un comentario.
- [ ] Añadir la sección de comentarios en `RecipeDetailsPage`.
- [ ] Implementar el formulario para añadir nuevos comentarios.
- [ ] Implementar el `recipeService` para gestionar comentarios y "me gusta".

## Fase 4: Creación y Edición de Recetas

- [x] Crear la página `CreateRecipePage` con el formulario de creación/edición.
- [x] Implementar los campos del formulario (título, descripción, ingredientes, pasos, etc.).
- [x] Implementar la lógica para la subida de imágenes.
- [x] Implementar las funciones en `recipeService` para crear y actualizar recetas.
- [ ] Añadir la lógica para precargar los datos de una receta al editarla.

## Fase 5: Búsqueda y Refinamiento

- [ ] Crear la página `SearchPage`.
- [ ] Añadir una barra de búsqueda en la `Navbar`.
- [ ] Implementar la lógica de búsqueda en `recipeService`.
- [ ] Mostrar los resultados de la búsqueda en `SearchPage`.
- [ ] Añadir paginación a los listados de recetas en `HomePage` y `SearchPage`.
- [ ] Realizar una revisión general de la UI/UX y aplicar mejoras.
- [ ] Realizar pruebas de la aplicación completa.
