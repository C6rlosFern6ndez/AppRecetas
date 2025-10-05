import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom'; // Importar Link para el enlace de login
import { signup as signupService } from '../../services/authService';
import '../../styles/pages/SignupPage.scss'; // Importar estilos SCSS

const SignupPage = () => {
  const [formData, setFormData] = useState({
    nombreUsuario: '', // Cambiado de 'nombre_usuario' a 'nombreUsuario'
    email: '',
    contrasena: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
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
    setSuccess('');
    console.log('Intentando registrar usuario:', formData); // Log descriptivo

    try {
      // El servicio de signup espera { nombreUsuario, email, contrasena }
      await signupService(formData);
      console.log('Registro exitoso.'); // Log descriptivo
      setSuccess('¡Registro exitoso! Redirigiendo al login...');
      setTimeout(() => {
        navigate('/login');
      }, 2000);
    } catch (err) {
      console.error('Error al registrarse:', err); // Log descriptivo
      setError(err.message || 'Error al registrarse. Inténtalo de nuevo.');
    }
  };

  return (
    <div className="signup-page">
      <h2>Crear Cuenta</h2>
      <form onSubmit={handleSubmit} className="auth-form">
        {error && <p className="error-message">{error}</p>}
        {success && <p className="success-message">{success}</p>}
        <div className="form-group">
          <label htmlFor="nombreUsuario">Nombre de Usuario:</label> {/* Cambiado de 'nombre_usuario' a 'nombreUsuario' */}
          <input
            type="text"
            id="nombreUsuario"
            name="nombreUsuario"
            value={formData.nombreUsuario}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="email">Email:</label>
          <input
            type="email"
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="contrasena">Contraseña:</label>
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
        <button type="submit" className="submit-button">Registrarse</button>
        <p className="login-link">
          ¿Ya tienes cuenta? <Link to="/login">Inicia sesión aquí</Link>
        </p>
      </form>
    </div>
  );
};

export default SignupPage;
