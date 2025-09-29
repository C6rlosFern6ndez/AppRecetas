import React from 'react';
import { useParams } from 'react-router-dom';

const RecipeDetailsPage = () => {
  const { id } = useParams();

  return (
    <div>
      <h2>Detalles de la Receta</h2>
      <p>Mostrando detalles para la receta con ID: {id}</p>
    </div>
  );
};

export default RecipeDetailsPage;
