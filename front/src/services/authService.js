const API_URL = 'http://localhost:8080/api/auth'; // Ajusta esta URL si tu backend corre en otro puerto

/**
 * Registra un nuevo usuario.
 * @param {object} userData - Datos del usuario (nombre_usuario, email, contrasena).
 * @returns {Promise<object>} - La respuesta de la API.
 */
export const signup = async (userData) => {
  try {
    const response = await fetch(`${API_URL}/signup`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(userData),
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || 'Error al registrar el usuario');
    }

    return await response.json();
  } catch (error) {
    console.error('Error en signup service:', error);
    throw error;
  }
};

/**
 * Inicia sesión de un usuario.
 * @param {object} credentials - Credenciales del usuario (nombreUsuarioOrEmail, contrasena).
 * @returns {Promise<object>} - La respuesta de la API con el token.
 */
export const login = async (credentials) => {
  try {
    const response = await fetch(`${API_URL}/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        nombreUsuarioOrEmail: credentials.email, // El backend espera 'nombreUsuarioOrEmail'
        contrasena: credentials.password,
      }),
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || 'Error al iniciar sesión');
    }

    return await response.json();
  } catch (error) {
    console.error('Error en login service:', error);
    throw error;
  }
};
