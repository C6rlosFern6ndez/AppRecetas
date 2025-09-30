import React from 'react';
import RecipeCarousel from './RecipeCarousel'; // Reutilizamos el carrusel

// El prop 'className' nos permitirá darle estilos únicos a cada bloque (los colores de fondo)
const ContentBlock = ({ title, recipes, className }) => {
    return (
        <div className={`content-block ${className || ''}`}>
            <h3>{title}</h3>
            <div className="carousel-wrapper">
                {recipes && recipes.length > 0 ? (
                    <RecipeCarousel recipes={recipes} />
                ) : (
                    <p>No hay recetas para mostrar.</p>
                )}
            </div>
        </div>
    );
};

export default ContentBlock;
