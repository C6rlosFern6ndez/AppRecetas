# Plan de Desarrollo del Frontend - Aplicación de Recetas

Este documento describe el plan de desarrollo para el frontend de la aplicación de recetas. El objetivo es crear una interfaz de usuario moderna, reactiva y fácil de usar, utilizando React, Vite y SCSS.

## 1. Stack Tecnológico

- **Framework:** React 19
- **Bundler:** Vite
- **Lenguaje:** JavaScript (JSX)
- **Estilos:** SCSS
- **Enrutamiento:** React Router
- **Gestión de Estado:** React Context API (para empezar, se puede migrar a Redux/Zustand si la complejidad aumenta)
- **Librería de Componentes:** Ninguna por ahora, se crearán componentes personalizados.
- **Peticiones HTTP:** Fetch API (nativa del navegador)

## 2. Estructura del Proyecto

Se seguirá una estructura de carpetas organizada para mantener el código limpio y escalable.

```
front/
├── public/
│   └── ...
├── src/
│   ├── assets/             # Imágenes, fuentes, etc.
│   ├── components/         # Componentes reutilizables (Botones, Inputs, Cards, etc.)
│   │   ├── common/         # Componentes muy genéricos (Button, Input, Spinner)
│   │   └── layout/         # Componentes de estructura (Navbar, Footer, Sidebar)
│   ├── constants/          # Constantes de la aplicación (rutas, claves de API, etc.)
│   ├── context/            # Contextos de React para gestión de estado global
│   ├── helpers/            # Funciones de ayuda y utilidades
│   ├── hooks/              # Hooks personalizados
│   ├── pages/              # Componentes que representan páginas completas
│   │   ├── Auth/
│   │   │   ├── LoginPage.jsx
│   │   │   └── SignupPage.jsx
│   │   ├── HomePage.jsx
│   │   ├── RecipeDetailsPage.jsx
│   │   ├── ProfilePage.jsx
│   │   └── ...
│   ├── services/           # Lógica para interactuar con la API del backend
│   │   ├── authService.js
│   │   ├── userService.js
│   │   └── recipeService.js
│   ├── styles/             # Archivos SCSS globales y variables
│   │   ├── _variables.scss
│   │   ├── _mixins.scss
│   │   └── main.scss
│   ├── App.jsx             # Componente principal y configuración de rutas
│   └── main.jsx            # Punto de entrada de la aplicación
├── .gitignore
├── package.json
└── ...
```

## 3. Desglose de Componentes y Páginas

### Páginas (Pages)

- **`HomePage`**: Página principal que mostrará un feed de recetas populares o recientes.
- **`LoginPage`**: Formulario de inicio de sesión.
- **`SignupPage`**: Formulario de registro.
- **`RecipeDetailsPage`**: Vista detallada de una receta, incluyendo ingredientes, pasos, comentarios y calificaciones.
- **`ProfilePage`**: Perfil de un usuario, mostrando sus recetas, seguidores y seguidos.
- **`CreateRecipePage`**: Formulario para crear o editar una receta.
- **`SearchPage`**: Página para buscar recetas por nombre, ingredientes o categorías.

### Componentes Comunes (Components)

- **`Button`**: Botón reutilizable con diferentes estilos.
- **`Input`**: Campo de texto reutilizable.
- **`RecipeCard`**: Tarjeta para mostrar una vista previa de una receta en los listados.
- **`Comment`**: Componente para mostrar un comentario individual.
- **`Rating`**: Componente para mostrar y/o interactuar con la calificación de una receta.
- **`Navbar`**: Barra de navegación principal.
- **`Footer`**: Pie de página.
- **`Spinner`**: Indicador de carga.

## 4. Enrutamiento (Routing)

Se utilizará `react-router-dom` para gestionar las rutas de la aplicación.

- `/`: Página de inicio (`HomePage`)
- `/login`: Página de inicio de sesión (`LoginPage`)
- `/signup`: Página de registro (`SignupPage`)
- `/receta/:id`: Detalles de una receta (`RecipeDetailsPage`)
- `/perfil/:id`: Perfil de usuario (`ProfilePage`)
- `/crear-receta`: Formulario de creación de recetas (`CreateRecipePage`)
- `/editar-receta/:id`: Formulario de edición de recetas
- `/buscar`: Página de búsqueda (`SearchPage`)

Se implementarán rutas protegidas para las páginas que requieran autenticación (ej. `/crear-receta`, `/perfil/:id`).

## 5. Gestión de Estado

Se utilizará la **Context API** de React para gestionar el estado global, principalmente para la información del usuario autenticado.

- **`AuthContext`**: Almacenará la información del usuario logueado y el token JWT. Proveerá funciones para `login`, `logout` y `signup`.

El estado local de los componentes se gestionará con los hooks `useState` y `useReducer` según sea necesario.

## 6. Interacción con la API (Services)

Se creará una capa de servicios para encapsular la lógica de las peticiones a la API del backend.

- **`authService.js`**: Funciones para `login` y `signup`.
- **`userService.js`**: Funciones para seguir/dejar de seguir usuarios, obtener seguidores/seguidos.
- **`recipeService.js`**: Funciones para obtener, crear, actualizar y eliminar recetas, así como para gestionar comentarios, "me gusta" y calificaciones.

## 7. Estilos (Styling)

Se utilizará **SCSS** para escribir los estilos de forma modular y organizada.

- **`main.scss`**: Archivo principal que importará variables, mixins y estilos globales.
- **Variables (`_variables.scss`)**: Para colores, fuentes, espaciados, etc.
- **Mixins (`_mixins.scss`)**: Para fragmentos de código reutilizables.
- **Estilos por componente**: Cada componente tendrá su propio archivo `.scss` para mantener los estilos encapsulados.

## 8. Plan de Desarrollo por Fases

1.  **Fase 1: Configuración y Autenticación**
    -   Instalar dependencias (`react-router-dom`, `sass`).
    -   Configurar la estructura de carpetas.
    -   Crear las páginas de Login y Signup.
    -   Implementar el `AuthContext` y el `authService`.
    -   Implementar el enrutamiento básico y las rutas protegidas.

2.  **Fase 2: Visualización de Recetas**
    -   Crear la `HomePage` para mostrar un listado de recetas.
    -   Crear el componente `RecipeCard`.
    -   Crear la `RecipeDetailsPage` para ver el detalle de una receta.
    -   Implementar el `recipeService` para obtener recetas.

3.  **Fase 3: Perfil de Usuario y Funcionalidades Sociales**
    -   Crear la `ProfilePage`.
    -   Implementar las funcionalidades de seguir/dejar de seguir.
    -   Implementar la funcionalidad de "me gusta" en las recetas.
    -   Implementar la sección de comentarios en `RecipeDetailsPage`.

4.  **Fase 4: Creación y Edición de Recetas**
    -   Crear el formulario para crear y editar recetas (`CreateRecipePage`).
    -   Implementar la subida de imágenes para las recetas.

5.  **Fase 5: Búsqueda y Refinamiento**
    -   Implementar la `SearchPage`.
    -   Añadir paginación a los listados de recetas.
    -   Refinar la interfaz de usuario y la experiencia de usuario.
