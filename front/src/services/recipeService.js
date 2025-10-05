/**
 * Servicio para gestionar llamadas a la API de recetas.
 *
 * Base URL: http://localhost:8080/api/
 * Utiliza axios para las peticiones HTTP.
 * Implementa autenticación con JWT cuando sea necesario.
 */

import axios from 'axios';

// Crear instancia de axios con configuración base
export const apiClient = axios.create({
    baseURL: 'http://localhost:8080/api/',
    headers: {
        'Content-Type': 'application/json',
    },
    timeout: 10000, // 10 segundos de timeout
});

// Interceptor para agregar token JWT automáticamente
apiClient.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

// Interceptor para manejar errores de autenticación
apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            console.warn('Token expirado o inválido. Redirigiendo al login');
            localStorage.removeItem('token');
            // Opcional: redirigir al login
            // window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

/**
 * Mapea la respuesta de la API a un formato compatible con los componentes
 * @param {object} receta - Objeto receta del backend
 * @returns {object} - Receta en formato frontend
 */
const mapRecipeToFrontend = (receta) => ({
    id: receta.id,
    name: receta.titulo,
    description: receta.descripcion,
    category: receta.categorias?.[0]?.nombre || 'Sin categoría',
    rating: Math.round(receta.calificaciones?.reduce((sum, cal) => sum + cal.puntuacion, 0) / (receta.calificaciones?.length || 1)) || 0,
    imageUrl: receta.urlImagen || '/imagen-default.jpg',
    usuario: receta.usuario?.nombreUsuario,
    tiempoPreparacion: receta.tiempoPreparacion,
    dificultad: receta.dificultad,
    createdAt: receta.fechaCreacion
});

/**
 * Obtiene las recetas mejor valoradas ordenadas por calificación promedio.
 * @param {object} options - Opciones para la consulta.
 * @param {number} options.limit - Número máximo de recetas a devolver (default: 10).
 * @returns {Promise<Array<object>>} - Promesa que resuelve con un array de recetas mejores valoradas.
 */
export const getBestRatedRecipes = async ({ limit = 10 }) => {
    console.log(`Obteniendo recetas mejor valoradas (límite: ${limit})...`);

    try {
        // Obtener todas las recetas con calificaciones
        const response = await apiClient.get('/recetas', {
            params: { size: 100 } // Obtener más para poder ordenar por calificación
        });

        // Mapeamos las recetas y calculamos su promedio de calificación
        const recipes = response.data.content.map(mapRecipeToFrontend);

        // Ordenar por rating descendente
        const sortedRecipes = recipes.sort((a, b) => b.rating - a.rating);

        return sortedRecipes.slice(0, limit);
    } catch (error) {
        console.error('Error obteniendo recetas mejor valoradas:', error);
        throw new Error('No se pudo cargar las recetas mejor valoradas');
    }
};

/**
 * Obtiene recetas representativas de diferentes categorías para mostrar un "vistazo".
 * Selecciona categorías populares y toma algunas recetas de cada una.
 * @returns {Promise<Array<object>>} - Promesa que resuelve con un array de recetas por categoría.
 */
export const getCategoryShowcaseRecipes = async () => {
    console.log('Obteniendo recetas representativas por categoría...');

    try {
        // Obtener las categorías disponibles
        const categoriesResponse = await apiClient.get('/categorias');
        const categories = categoriesResponse.data;

        const showcaseRecipes = [];

        // Para cada categoría, obtener algunas recetas (máximo 2-3 por categoría)
        for (const category of categories.slice(0, 6)) { // Limitar a 6 categorías para no sobrecargar
            try {
                const recipesResponse = await apiClient.get(`/categorias/${category.id}/recetas`, {
                    params: { size: 3, sort: 'fechaCreacion,desc' }
                });

                const categoryRecipes = recipesResponse.data.content.map(mapRecipeToFrontend);
                showcaseRecipes.push(...categoryRecipes);
            } catch (error) {
                console.warn(`Error obteniendo recetas de categoría ${category.nombre}:`, error);
            }
        }

        return showcaseRecipes;
    } catch (error) {
        console.error('Error obteniendo categorias:', error);
        // Fallback: devolver array vacío
        return [];
    }
};

/**
 * Obtiene las últimas recetas subidas ordenadas por fecha de creación.
 * @param {object} options - Opciones para la consulta.
 * @param {number} options.limit - Número máximo de recetas a devolver (default: 10).
 * @returns {Promise<Array<object>>} - Promesa que resuelve con un array de las últimas recetas.
 */
export const getLatestRecipes = async ({ limit = 10 }) => {
    console.log(`Obteniendo últimas recetas (límite: ${limit})...`);

    try {
        const response = await apiClient.get('/recetas', {
            params: {
                size: limit,
                sort: 'fechaCreacion,desc'
            }
        });

        return response.data.content.map(mapRecipeToFrontend);
    } catch (error) {
        console.error('Error obteniendo últimas recetas:', error);
        throw new Error('No se pudo cargar las últimas recetas');
    }
};

/**
 * Obtiene recetas aleatorias mezclando los resultados de la búsqueda general.
 * @param {object} options - Opciones para la consulta.
 * @param {number} options.limit - Número máximo de recetas a devolver (default: 10).
 * @returns {Promise<Array<object>>} - Promesa que resuelve con un array de recetas aleatorias.
 */
export const getRandomRecipes = async ({ limit = 10 }) => {
    console.log(`Obteniendo recetas aleatorias (límite: ${limit})...`);

    try {
        // Obtener una página grande de recetas para luego mezclar
        const response = await apiClient.get('/recetas', {
            params: { size: 50 } // Obtener más para tener variedad
        });

        const recipes = response.data.content.map(mapRecipeToFrontend);

        // Mezclar el array y tomar el límite
        const shuffled = [...recipes].sort(() => 0.5 - Math.random());
        return shuffled.slice(0, limit);
    } catch (error) {
        console.error('Error obteniendo recetas aleatorias:', error);
        throw new Error('No se pudo cargar las recetas aleatorias');
    }
};

/**
 * Realiza una búsqueda avanzada de recetas
 * @param {object} filtros - Filtros de búsqueda
 * @param {string} filtros.titulo - Búsqueda por título
 * @param {string} filtros.ingredienteNombre - Búsqueda por ingrediente
 * @param {string} filtros.dificultad - FACIL/MEDIA/DIFICIL
 * @param {number} filtros.tiempoPreparacionMax - Tiempo máximo de preparación
 * @param {string} filtros.categoriaNombre - Búsqueda por categoría
 * @param {number} page - Página (0-based, default: 0)
 * @param {number} size - Tamaño de página (default: 20)
 * @returns {Promise<object>} - Resultado de búsqueda con paginación
 */
export const searchRecipes = async (filtros = {}, page = 0, size = 20) => {
    console.log('Buscando recetas con filtros:', filtros);

    try {
        const response = await apiClient.get('/recetas/search', {
            params: {
                ...filtros,
                page,
                size,
                sort: 'fechaCreacion,desc'
            }
        });

        return {
            ...response.data,
            content: response.data.content.map(mapRecipeToFrontend)
        };
    } catch (error) {
        console.error('Error buscando recetas:', error);
        throw error;
    }
};
