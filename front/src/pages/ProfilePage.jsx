import React from 'react';
import { useParams } from 'react-router-dom';

const ProfilePage = () => {
  const { id } = useParams();

  return (
    <div>
      <h2>Perfil de Usuario</h2>
      <p>Mostrando el perfil del usuario con ID: {id}</p>
    </div>
  );
};

export default ProfilePage;
