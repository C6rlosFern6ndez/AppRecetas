import React from 'react';

const RecipeCarousel = ({ recipes }) => {
    // Si no hay recetas o la lista está vacía, muestra un mensaje
    if (!recipes || recipes.length === 0) {
        return <p>No hay recetas para mostrar en este carrusel.</p>;
    }

    return (
        <div className="recipe-carousel">
            {recipes.map((recipe) => (
                <div key={recipe.id} className="recipe-carousel-item">
                    <img src={recipe.imageUrl} alt={recipe.name} className="recipe-image" />
                    <div className="recipe-info">
                        <h4>{recipe.name}</h4>
                        <p className="recipe-description">{recipe.description}</p>
                        <div className="recipe-rating">
                            {'⭐'.repeat(recipe.rating)} {/* Muestra estrellas según la calificación */}
                        </div>
                        <a href={`/recipes/${recipe.id}`} className="recipe-link">Ver detalles</a>
                    </div>
                </div>
            ))}
        </div>
    );
};

export default RecipeCarousel;
