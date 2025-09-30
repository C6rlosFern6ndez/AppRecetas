// src/services/recipeService.js

// Simulación de llamadas a API para obtener recetas.
// En una aplicación real, aquí harías llamadas a tu backend.

const mockRecipes = [
    { id: 1, name: 'Paella Valenciana', description: 'Arroz con pollo, conejo y verduras.', category: 'Arroces', rating: 5, imageUrl: 'https://via.placeholder.com/150/a17c38' },
    { id: 2, name: 'Tortilla Española', description: 'Tortilla de patatas y cebolla.', category: 'Tapas', rating: 4, imageUrl: 'https://via.placeholder.com/150/b0a89e' },
    { id: 3, name: 'Gazpacho Andaluz', description: 'Sopa fría de tomate y verduras.', category: 'Sopas', rating: 4, imageUrl: 'https://via.placeholder.com/150/b0a89e' },
    { id: 4, name: 'Crema Catalana', description: 'Postre similar a la crème brûlée.', category: 'Postres', rating: 5, imageUrl: 'https://via.placeholder.com/150/6b4f2c' },
    { id: 5, name: 'Pulpo a la Gallega', description: 'Pulpo cocido con pimentón y aceite de oliva.', category: 'Mariscos', rating: 4, imageUrl: 'https://via.placeholder.com/150/a17c38' },
    { id: 6, name: 'Fabada Asturiana', description: 'Guiso de fabes con chorizo y morcilla.', category: 'Guisos', rating: 5, imageUrl: 'https://via.placeholder.com/150/b0a89e' },
    { id: 7, name: 'Salmorejo Cordobés', description: 'Crema fría de tomate más espesa que el gazpacho.', category: 'Sopas', rating: 3, imageUrl: 'https://via.placeholder.com/150/b0a89e' },
    { id: 8, name: 'Tarta de Santiago', description: 'Tarta de almendras.', category: 'Postres', rating: 4, imageUrl: 'https://via.placeholder.com/150/6b4f2c' },
    { id: 9, name: 'Gambas al Ajillo', description: 'Gambas salteadas con ajo y guindilla.', category: 'Mariscos', rating: 4, imageUrl: 'https://via.placeholder.com/150/a17c38' },
    { id: 10, name: 'Pimientos de Padrón', description: 'Pequeños pimientos fritos con sal.', category: 'Tapas', rating: 3, imageUrl: 'https://via.placeholder.com/150/b0a89e' },
];

// Simula un retraso de red
const simulateApiCall = (data, delay = 500) => {
    return new Promise(resolve => setTimeout(() => resolve(data), delay));
};

/**
 * Obtiene las recetas mejor valoradas.
 * @param {object} options - Opciones para la consulta.
 * @param {number} options.limit - Número máximo de recetas a devolver.
 * @returns {Promise<Array<object>>} - Promesa que resuelve con un array de recetas.
 */
export const getBestRatedRecipes = async ({ limit = 10 }) => {
    console.log(`Fetching best rated recipes (limit: ${limit})...`);
    // Ordenar por rating descendente y tomar el límite
    const sortedRecipes = [...mockRecipes].sort((a, b) => b.rating - a.rating);
    return simulateApiCall(sortedRecipes.slice(0, limit));
};

/**
 * Obtiene recetas representativas de diferentes categorías.
 * @returns {Promise<Array<object>>} - Promesa que resuelve con un array de recetas.
 */
export const getCategoryShowcaseRecipes = async () => {
    console.log('Fetching category showcase recipes...');
    // Seleccionar algunas recetas de diferentes categorías para mostrar
    const showcase = mockRecipes.filter(recipe => 
        recipe.category === 'Arroces' || 
        recipe.category === 'Tapas' || 
        recipe.category === 'Postres'
    );
    return simulateApiCall(showcase);
};

/**
 * Obtiene las últimas recetas subidas.
 * @param {object} options - Opciones para la consulta.
 * @param {number} options.limit - Número máximo de recetas a devolver.
 * @returns {Promise<Array<object>>} - Promesa que resuelve con un array de recetas.
 */
export const getLatestRecipes = async ({ limit = 10 }) => {
    console.log(`Fetching latest recipes (limit: ${limit})...`);
    // En un escenario real, esto se basaría en una fecha de creación. Aquí simulamos con las últimas del array.
    const latest = [...mockRecipes].slice(-limit);
    return simulateApiCall(latest);
};

/**
 * Obtiene recetas aleatorias.
 * @param {object} options - Opciones para la consulta.
 * @param {number} options.limit - Número máximo de recetas a devolver.
 * @returns {Promise<Array<object>>} - Promesa que resuelve con un array de recetas.
 */
export const getRandomRecipes = async ({ limit = 10 }) => {
    console.log(`Fetching random recipes (limit: ${limit})...`);
    // Barajar el array y tomar el límite
    const shuffled = [...mockRecipes].sort(() => 0.5 - Math.random());
    return simulateApiCall(shuffled.slice(0, limit));
};
