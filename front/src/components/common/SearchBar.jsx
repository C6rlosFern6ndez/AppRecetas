import React, { useState } from 'react';

const SearchBar = ({ onSearch }) => {
    const [searchTerm, setSearchTerm] = useState('');

    const handleInputChange = (event) => {
        setSearchTerm(event.target.value);
    };

    const handleSearch = () => {
        if (onSearch) {
            onSearch(searchTerm);
        }
    };

    const handleKeyPress = (event) => {
        if (event.key === 'Enter') {
            handleSearch();
        }
    };

    return (
        <div className="search-bar-container">
            <input
                type="text"
                placeholder="Busca recetas..."
                value={searchTerm}
                onChange={handleInputChange}
                onKeyPress={handleKeyPress}
                className="search-input"
            />
            <button onClick={handleSearch} className="search-button">
                Buscar
            </button>
        </div>
    );
};

export default SearchBar;
