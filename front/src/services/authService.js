import { apiClient } from './recipeService'; // Importar la instancia de axios configurada

/**
 * Registra un nuevo usuario.
 * @param {object} userData - Datos del usuario (nombreUsuario, email, contrasena).
 * @returns {Promise<object>} - La respuesta de la API.
 */
export const signup = async (userData) => {
  console.log('Intentando registrar usuario con datos:', userData); // Log descriptivo
  try {
    const response = await apiClient.post('/auth/signup', userData);
    console.log('Registro exitoso:', response.data); // Log descriptivo
    return response.data;
  } catch (error) {
    console.error('Error en signup service:', error.response?.data || error.message); // Log descriptivo
    throw error.response?.data || error.message;
  }
};

/**
 * Inicia sesión de un usuario.
 * @param {object} credentials - Credenciales del usuario (email, contrasena).
 * @returns {Promise<object>} - La respuesta de la API con el token.
 */
export const login = async (credentials) => {
  console.log('Intentando iniciar sesión con credenciales:', credentials); // Log descriptivo
  try {
    const response = await apiClient.post('/auth/login', {
      email: credentials.email, // Corregido: el backend espera 'email' directamente
      contrasena: credentials.contrasena,
    });
    console.log('Inicio de sesión exitoso:', response.data); // Log descriptivo
    return response.data;
  } catch (error) {
    console.error('Error en login service:', error.response?.data || error.message); // Log descriptivo
    throw error.response?.data || error.message;
  }
};
