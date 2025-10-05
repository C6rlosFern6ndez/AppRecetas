import { useContext } from 'react';
import { AuthContext } from '../context/auth-context';

// Hook personalizado para usar el contexto fácilmente
export const useAuth = () => {
  return useContext(AuthContext);
};
