/**
 * Página principal de la aplicación Recetas App.
 * Demostración con imágenes de ImgBB para mostrar la funcionalidad de carruceles.
 * Usa URLs públicas de imágenes para efectos de presentación.
 */

import React, { useState, useEffect } from 'react';
import Navbar from '../components/layout/Navbar';
import Footer from '../components/layout/Footer';
import Spinner from '../components/common/Spinner';
import SearchBar from '../components/common/SearchBar';
import ContentBlock from '../components/common/ContentBlock';

import '../styles/pages/HomePage.scss';

const HomePage = () => {
    const [pageData, setPageData] = useState({
        bestRated: [],
        byCategory: [],
        latest: [],
        random: []
    });

    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);

    // URLs de imágenes del proyecto platosDeMuestra subidas a ImgBB
    const imgBBImages = [
        'https://i.ibb.co/N6T1c4bY/Carnes-Chuleton-de-ternera-a-la-brasa.jpg',
        'https://i.ibb.co/GQ2fZmRt/Aves-Pollo-con-champi-ones.jpg',
        'https://i.ibb.co/ZzVykdg4/Fritos-Croquetas-de-Jamon.jpg',
        'https://i.ibb.co/C3f31P8M/Legumbres-Cocido-Madrile-o.jpg',
        'https://i.ibb.co/99YLgsTB/Legumbres-Lentejas.jpg',
        'https://i.ibb.co/DHR0hT8R/Masa-Pan-de-Pueblo.jpg',
        'https://i.ibb.co/wZPbRgCt/Masas-Base-Pizza.jpg',
        'https://i.ibb.co/mjt2Tfy/Musaka.jpg',
        'https://i.ibb.co/zVRZyqVy/Pasta-bolo-esa.jpg',
        'https://i.ibb.co/GbTSD0g/Pasta-Carbonara.jpg',
        'https://i.ibb.co/xKkX7rLz/Pasta-lasa-a.jpg',
        'https://i.ibb.co/WZxKWKn/Pescado-Bacalao-Dorado.jpg',
        'https://i.ibb.co/6JtSZwbZ/Salsa-Bechamel.jpg',
        'https://i.ibb.co/SDNYgvB0/Salsa-Mojo-picon-verde.jpg',
        'https://i.ibb.co/1Gjpx7Z3/Salsa-teriyaki.jpg',
        'https://i.ibb.co/V0C5FR2Q/Tortilla.jpg',
        'https://i.ibb.co/wNCKNDqd/Verduras-Coliflor-gratinada-con-tomates-cherry.jpg',
        'https://i.ibb.co/sp99GBz0/Verduras-Espinacas-con-garbanzos.jpg'
    ];

    useEffect(() => {
        console.log('Cargando datos de demostración con imágenes ImgBB...');

        // Simular retraso de carga para mostrar spinner
        const loadDemoData = setTimeout(() => {
            try {
                setError(null);

                // Datos de demostración con imágenes aleatorias de ImgBB
                const demoRecipes = [
                    // Carrusel 1: Las Más Valoradas
                    [
                        {id: 1, name: 'Paella Marinera', description: 'Arroz con mariscos frescos del Mediterráneo', rating: 5, imageUrl: imgBBImages[0], category: 'Arroz', tiempoPreparacion: 45},
                        {id: 2, name: 'Tortilla de Patatas', description: 'Clásica tortilla española con cebolla', rating: 5, imageUrl: imgBBImages[1], category: 'Huevos', tiempoPreparacion: 30},
                        {id: 3, name: 'Fabada Asturiana', description: 'Guiso de fabes con chorizo y morcilla', rating: 4, imageUrl: imgBBImages[2], category: 'Guisos', tiempoPreparacion: 120},
                        {id: 4, name: 'Cocido Madrileño', description: 'Estofado tradicional de verduras y carne', rating: 4, imageUrl: imgBBImages[3], category: 'Estofados', tiempoPreparacion: 180},
                        {id: 5, name: 'Pimientos de Padrón', description: 'Pimientos fritos con sal marina', rating: 5, imageUrl: imgBBImages[4], category: 'Tapas', tiempoPreparacion: 15},
                        {id: 6, name: 'Salmorejo Cordobés', description: 'Crema fría de tomate y pan', rating: 4, imageUrl: imgBBImages[5], category: 'Sopas', tiempoPreparacion: 10},
                    ],
                    // Carrusel 2: Explora por Categoría
                    [
                        {id: 7, name: 'Arroz Caldoso', description: 'Arroz con pescados y mariscos', rating: 4, imageUrl: imgBBImages[6], category: 'Arroz', tiempoPreparacion: 40},
                        {id: 8, name: 'Jamón Ibérico', description: 'Jamón curado tradicional español', rating: 5, imageUrl: imgBBImages[7], category: 'Embutidos', tiempoPreparacion: 0},
                        {id: 9, name: 'Gamba al Ajillo', description: 'Gambas en aceite de oliva y ajo', rating: 4, imageUrl: imgBBImages[8], category: 'Mariscos', tiempoPreparacion: 10},
                        {id: 10, name: 'Huevos a la Flamenca', description: 'Huevos cocidos en salsa de chorizo', rating: 5, imageUrl: imgBBImages[9], category: 'Huevos', tiempoPreparacion: 25},
                        {id: 11, name: 'Churrasco Argentino', description: 'Corte argentino exquisito', rating: 4, imageUrl: imgBBImages[10], category: 'Carnes', tiempoPreparacion: 15},
                        {id: 12, name: 'Crema de Calabaza', description: 'Sopa cremosa de verduras de otoño', rating: 4, imageUrl: imgBBImages[11], category: 'Sopas', tiempoPreparacion: 20},
                    ],
                    // Carrusel 3: Recetas Recientes
                    [
                        {id: 13, name: 'Ensala Vizcaina', description: 'Rodajas de tomate con atún y pimientos', rating: 4, imageUrl: imgBBImages[0], category: 'Ensaladas', tiempoPreparacion: 15},
                        {id: 14, name: 'Potaje de Lentejas', description: 'Guiso de lentejas con verduras y panceta', rating: 4, imageUrl: imgBBImages[1], category: 'Legumbres', tiempoPreparacion: 90},
                        {id: 15, name: 'Migas Extremeñas', description: 'Pan rallado con ajo y chorizo', rating: 3, imageUrl: imgBBImages[2], category: 'Guiños', tiempoPreparacion: 30},
                        {id: 16, name: 'Pulpo a la Gallega', description: 'Pulpo cocido con pimentón', rating: 5, imageUrl: imgBBImages[3], category: 'Mariscos', tiempoPreparacion: 45},
                        {id: 17, name: 'Tarta de Santiago', description: 'Tarta de almendras tradicional gallega', rating: 5, imageUrl: imgBBImages[4], category: 'Postres', tiempoPreparacion: 60},
                        {id: 18, name: 'Ceviche Peruano', description: 'Pescado marinado con lima y ají', rating: 4, imageUrl: imgBBImages[5], category: 'Pescados', tiempoPreparacion: 20},
                    ],
                    // Carrusel 4: Descubrimientos
                    [
                        {id: 19, name: 'Mojo Picón', description: 'Salsa canaria con guindillas', rating: 4, imageUrl: imgBBImages[6], category: 'Salsas', tiempoPreparacion: 5},
                        {id: 20, name: 'Papas Bravas', description: 'Patatas fritas con salsa brava', rating: 4, imageUrl: imgBBImages[7], category: 'Tapas', tiempoPreparacion: 25},
                        {id: 21, name: 'Rabo de Toro', description: 'Estofado de cola de toro', rating: 4, imageUrl: imgBBImages[8], category: 'Estofados', tiempoPreparacion: 180},
                        {id: 22, name: 'Queso Idiazabal', description: 'Queso vasco ahumado', rating: 4, imageUrl: imgBBImages[9], category: 'Lácteos', tiempoPreparacion: 0},
                        {id: 23, name: 'Empanadas Gallegas', description: 'Empanada de bonito y cebolla', rating: 4, imageUrl: imgBBImages[10], category: 'Empanadas', tiempoPreparacion: 45},
                        {id: 24, name: 'Bollo Preñao', description: 'Bollería grancanaria tradicional', rating: 3, imageUrl: imgBBImages[11], category: 'Bollería', tiempoPreparacion: 35},
                    ],
                    // Carrusel 5: Categorías Especiales (usando imágenes restantes)
                    [
                        {id: 25, name: 'Salsa Bechamel', description: 'Salsa blanca clásica para pastelería', rating: 4, imageUrl: imgBBImages[12], category: 'Salsas', tiempoPreparacion: 15},
                        {id: 26, name: 'Mojo Picon Verde', description: 'Salsa canaria tradicional picante', rating: 5, imageUrl: imgBBImages[13], category: 'Salsas', tiempoPreparacion: 10},
                        {id: 27, name: 'Salsa Teriyaki', description: 'Salsa japonesa agridulce', rating: 4, imageUrl: imgBBImages[14], category: 'Salsas', tiempoPreparacion: 5},
                        {id: 28, name: 'Coliflor Gratinada', description: 'Verdura con queso fundido', rating: 4, imageUrl: imgBBImages[16], category: 'Verduras', tiempoPreparacion: 30},
                        {id: 29, name: 'Espinacas con Garbanzos', description: 'Verdura tradicional española', rating: 5, imageUrl: imgBBImages[17], category: 'Verduras', tiempoPreparacion: 20},
                        {id: 30, name: 'Bacalao Dorado', description: 'Pescado blanco en salsa', rating: 4, imageUrl: imgBBImages[11], category: 'Pescados', tiempoPreparacion: 25}
                    ]
                ];

                console.log('Datos de demostración cargados exitosamente');

                setPageData({
                    bestRated: demoRecipes[0],
                    byCategory: demoRecipes[1],
                    latest: demoRecipes[2],
                    random: demoRecipes[3]
                });

                setIsLoading(false);

            } catch (err) {
                console.error('Error en carga de demostración:', err);
                setError('Error al cargar las imágenes de demostración');
                setIsLoading(false);
            }
        }, 1500); // Simular 1.5 segundos de carga

        return () => clearTimeout(loadDemoData);
    }, []);

    // Mostrar loading mientras cargan los datos
    if (isLoading) {
        return (
            <>
                <Navbar />
                <main className="homepage-dashboard-layout">
                    <div className="loading-container">
                        <Spinner />
                        <p>Cargando deliciosas recetas...</p>
                    </div>
                </main>
                <Footer />
            </>
        );
    }

    // Mostrar error si ocurrió alguno
    if (error) {
        return (
            <>
                <Navbar />
                <main className="homepage-dashboard-layout">
                    <div className="error-container">
                        <div className="error-message">
                            <h2>¡Ups! Algo salió mal</h2>
                            <p>{error}</p>
                            <button
                                onClick={() => window.location.reload()}
                                className="retry-button"
                            >
                                Intentar de nuevo
                            </button>
                        </div>
                    </div>
                </main>
                <Footer />
            </>
        );
    }

    return (
        <>
            <Navbar />
            <main className="homepage-dashboard-layout">
                <div className="search-container">
                    <SearchBar />
                </div>

                <div className="content-grid">
                    <ContentBlock
                        title="Las Más Valoradas"
                        recipes={pageData.bestRated}
                        className="grid-item-1"
                        interval={10000} // 10 segundos
                    />
                    <ContentBlock
                        title="Explora por Categoría"
                        recipes={pageData.byCategory}
                        className="grid-item-2"
                        interval={12000} // 12 segundos
                    />
                    <ContentBlock
                        title="Recetas Recientes"
                        recipes={pageData.latest}
                        className="grid-item-3"
                        interval={8000} // 8 segundos
                    />
                    <ContentBlock
                        title="Descubrimientos"
                        recipes={pageData.random}
                        className="grid-item-4"
                        interval={15000} // 15 segundos
                    />
                </div>
            </main>
            <Footer />
        </>
    );
};

export default HomePage;
