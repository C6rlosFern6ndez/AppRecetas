import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

const Navbar = () => {
  const { isAuthenticated, logout, user } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <Link to="/"><h1>Recetas</h1></Link>
      <ul>
        <li><Link to="/">Inicio</Link></li>
        {isAuthenticated ? (
          <>
            <li><Link to={`/perfil/${user?.id}`}>Mi Perfil</Link></li>
            <li><Link to="/crear-receta">Crear Receta</Link></li>
            <li><button onClick={handleLogout} className="btn btn-secondary">Cerrar Sesión</button></li>
          </>
        ) : (
          <>
            <li><Link to="/login">Iniciar Sesión</Link></li>
            <li><Link to="/signup">Registrarse</Link></li>
          </>
        )}
      </ul>
    </nav>
  );
};

export default Navbar;
