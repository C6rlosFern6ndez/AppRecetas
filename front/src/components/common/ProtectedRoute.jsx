import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const ProtectedRoute = () => {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    // Si el usuario no está autenticado, redirige a la página de login
    return <Navigate to="/login" replace />;
  }

  // Si está autenticado, renderiza el componente hijo (la página protegida)
  return <Outlet />;
};

export default ProtectedRoute;
