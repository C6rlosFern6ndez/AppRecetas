import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom'; // Importar Link para el enlace de registro
import { useAuth } from '../../hooks/useAuth';
import { login as loginService } from '../../services/authService';
import '../../styles/pages/LoginPage.scss'; // Importar estilos SCSS

const LoginPage = () => {
  const [formData, setFormData] = useState({
    email: '',
    contrasena: '' // Cambiado de 'password' a 'contrasena'
  });
  const [error, setError] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prevState => ({
      ...prevState,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      // El servicio de login espera { nombreUsuarioOrEmail, contrasena }
      // y el formulario envía { email, contrasena }
      // El authService.js ya mapea 'email' a 'nombreUsuarioOrEmail'
      const data = await loginService(formData);
      console.log('Login exitoso:', data); // Log descriptivo

      // El backend devuelve { token, type, id, nombreUsuario, email }
      // El AuthContext espera un objeto de usuario y el token
      login({ id: data.id, nombreUsuario: data.nombreUsuario, email: data.email }, data.token);
      navigate('/'); // Redirigir a la página principal
    } catch (err) {
      console.error('Error al iniciar sesión:', err); // Log descriptivo
      setError(err.message || 'Error al iniciar sesión. Verifica tus credenciales.');
    }
  };

  return (
    <div className="login-page">
      <h2>Iniciar Sesión</h2>
      <form onSubmit={handleSubmit} className="auth-form">
        {error && <p className="error-message">{error}</p>}
        <div className="form-group">
          <label htmlFor="email">Email o Nombre de Usuario:</label>
          <input
            type="text"
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="contrasena">Contraseña:</label> {/* Cambiado de 'password' a 'contrasena' */}
          <input
            type="password"
            id="contrasena"
            name="contrasena"
            value={formData.contrasena}
            onChange={handleChange}
            required
            minLength="6" // Añadir requisito de longitud mínima
          />
        </div>
        <button type="submit" className="submit-button">Entrar</button>
        <p className="register-link">
          ¿No tienes cuenta? <Link to="/signup">Regístrate aquí</Link>
        </p>
      </form>
    </div>
  );
};

export default LoginPage;
