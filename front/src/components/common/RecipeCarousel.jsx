import React, { useState, useEffect, useCallback } from 'react';

const RecipeCarousel = ({ recipes, interval = 10000 }) => { // Acepta 'interval' como prop, con 10s por defecto
    const [currentIndex, setCurrentIndex] = useState(0);

    // Función para pasar a la siguiente receta, es useCallback para evitar re-creaciones
    const nextSlide = useCallback(() => {
        setCurrentIndex((prevIndex) => (prevIndex + 1) % recipes.length);
    }, [recipes.length]);

    // Función para volver a la receta anterior
    const prevSlide = () => {
        setCurrentIndex((prevIndex) => (prevIndex - 1 + recipes.length) % recipes.length);
    };

    // Efecto para el cambio automático cada 10 segundos
    useEffect(() => {
        // Si no hay recetas, no hacer nada
        if (!recipes || recipes.length === 0) {
            return;
        }

        const timer = setInterval(() => {
            nextSlide();
        }, interval); // Usa el intervalo proporcionado

        // Limpiar el intervalo cuando el componente se desmonte o cambien las recetas
        return () => clearInterval(timer);
    }, [recipes, nextSlide]);

    // Si no hay recetas, muestra un mensaje
    if (!recipes || recipes.length === 0) {
        return <p>No hay recetas para mostrar en este carrusel.</p>;
    }

    // Obtener la receta actual para mostrar
    const currentRecipe = recipes[currentIndex];

    return (
        <div className="recipe-slideshow">
            <div className="slideshow-item">
                <img 
                    src={currentRecipe.imageUrl} 
                    alt={currentRecipe.name} 
                    className="recipe-image" 
                    referrerPolicy="no-referrer" 
                />
                <div className="recipe-info">
                    <h4>{currentRecipe.name}</h4>
                    <p className="recipe-description">{currentRecipe.description}</p>
                    <div className="recipe-rating">
                        {'⭐'.repeat(currentRecipe.rating)}
                    </div>
                    <a href={`/recipes/${currentRecipe.id}`} className="recipe-link">Ver detalles</a>
                </div>
            </div>

            <div className="slideshow-controls">
                <button onClick={prevSlide} className="prev-btn" aria-label="Receta anterior">‹</button>
                <button onClick={nextSlide} className="next-btn" aria-label="Siguiente receta">›</button>
            </div>
        </div>
    );
};

export default RecipeCarousel;
