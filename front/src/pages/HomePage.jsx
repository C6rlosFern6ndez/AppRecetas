// src/pages/HomePage.jsx

import React, { useState, useEffect } from 'react';
import Navbar from '../components/layout/Navbar';
import Footer from '../components/layout/Footer';
import Spinner from '../components/common/Spinner';
import SearchBar from '../components/common/SearchBar';
import ContentBlock from '../components/common/ContentBlock';

// Tenemos que crear los servicios
// import {
//     getBestRatedRecipes,
//     getCategoryShowcaseRecipes,
//     getLatestRecipes,
//     getRandomRecipes
// } from '../services/recipeService';

import '../styles/pages/HomePage.scss';

const HomePage = () => {
    const [pageData, setPageData] = useState({
        bestRated: [],
        byCategory: [],
        latest: [],
        random: []
    });
    // const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchAllData = async () => {
            try {
                // setIsLoading(true);
                const [bestRated, byCategory, latest, random] = await Promise.all([
                    // getBestRatedRecipes({ limit: 10 }),
                    // getCategoryShowcaseRecipes(),
                    // getLatestRecipes({ limit: 10 }),
                    // getRandomRecipes({ limit: 10 })
                ]);
                setPageData({ bestRated, byCategory, latest, random });
                setError(null);
            } catch (err) {
                console.error("Error fetching homepage data:", err);
                setError("No se pudo cargar el contenido principal.");
            } finally {
                // setIsLoading(false);
            }
        };

        fetchAllData();
    }, []);

    // if (isLoading) return <Spinner />;
    if (error) return <div className="error-message">{error}</div>;

    return (
        <>
            <Navbar />
            <main className="homepage-dashboard-layout">
                <div className="search-container">
                    {/* <SearchBar /> */}
                </div>

                <div className="content-grid">
                    <ContentBlock
                        title="Las 10 Mejor Valoradas"
                        recipes={pageData.bestRated}
                        className="grid-item-1"
                    />
                    <ContentBlock
                        title="Un Vistazo por Categoría"
                        recipes={pageData.byCategory}
                        className="grid-item-2"
                    />
                    <ContentBlock
                        title="Últimas Recetas Subidas"
                        recipes={pageData.latest}
                        className="grid-item-3"
                    />
                    <ContentBlock
                        title="Descubrimientos al Azar"
                        recipes={pageData.random}
                        className="grid-item-4"
                    />
                </div>
            </main>
            <Footer />
        </>
    );
};

export default HomePage;
