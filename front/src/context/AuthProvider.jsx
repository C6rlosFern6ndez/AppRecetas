import React, { useState } from 'react';
import { AuthContext } from './auth-context';

// 2. Crear el Proveedor del Contexto
export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null); // Podría ser el objeto de usuario decodificado del token
  const [token, setToken] = useState(localStorage.getItem('token') || null);

  // Función para iniciar sesión
  const login = (userData, authToken) => {
    localStorage.setItem('token', authToken);
    setToken(authToken);
    setUser(userData); // Aquí podrías decodificar el token para obtener los datos del usuario
  };

  // Función para cerrar sesión
  const logout = () => {
    console.log('Cerrando sesión y redirigiendo al login...'); // Log descriptivo
    localStorage.removeItem('token');
    setToken(null);
    setUser(null);
    // Redirigir al login para forzar una nueva autenticación
    window.location.href = '/login';
  };

  // Valor que se pasará a los componentes hijos
  const value = {
    user,
    token,
    isAuthenticated: !!token,
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
