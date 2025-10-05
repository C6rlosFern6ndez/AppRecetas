import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { createReceta, uploadRecetaImage, apiClient } from '../services/recipeService'; // Importar apiClient para obtener categorías
import '../styles/pages/CreateRecipePage.scss'; // Asumiendo que crearé un archivo SCSS para esta página

const CreateRecipePage = () => {
  const navigate = useNavigate();

  // Estados para los campos de la receta
  const [titulo, setTitulo] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [tiempoPreparacion, setTiempoPreparacion] = useState('');
  const [dificultad, setDificultad] = useState('FACIL'); // Default
  const [porciones, setPorciones] = useState('');
  const [selectedFile, setSelectedFile] = useState(null);
  const [previewImage, setPreviewImage] = useState(null);

  // Estados para categorías
  const [allCategories, setAllCategories] = useState([]);
  const [selectedCategories, setSelectedCategories] = useState([]);

  // Estados para ingredientes (nombre y cantidad)
  const [ingredientes, setIngredientes] = useState([{ nombre: '', cantidad: '' }]);

  // Estados para pasos de preparación
  const [pasos, setPasos] = useState([{ descripcion: '' }]);

  // Estados para manejo de UI
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);

  // Cargar categorías al montar el componente
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await apiClient.get('/categorias');
        setAllCategories(response.data);
        console.log('Categorías cargadas:', response.data); // Log descriptivo
      } catch (err) {
        console.error('Error al cargar categorías:', err); // Log descriptivo
        setError('No se pudieron cargar las categorías.');
      }
    };
    fetchCategories();
  }, []);

  // Manejadores de cambio para campos de texto
  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name === 'titulo') setTitulo(value);
    else if (name === 'descripcion') setDescripcion(value);
    else if (name === 'tiempoPreparacion') setTiempoPreparacion(value);
    else if (name === 'dificultad') setDificultad(value);
    else if (name === 'porciones') setPorciones(value);
  };

  // Manejador para la selección de archivo de imagen
  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setSelectedFile(file);
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreviewImage(reader.result);
      };
      reader.readAsDataURL(file);
      console.log('Archivo de imagen seleccionado:', file.name); // Log descriptivo
    } else {
      setSelectedFile(null);
      setPreviewImage(null);
      console.log('No se seleccionó ningún archivo de imagen.'); // Log descriptivo
    }
  };

  // Manejadores para ingredientes
  const handleIngredienteChange = (index, e) => {
    const { name, value } = e.target;
    const newIngredientes = [...ingredientes];
    newIngredientes[index][name] = value;
    setIngredientes(newIngredientes);
    console.log(`Ingrediente ${index} actualizado: ${name}=${value}`); // Log descriptivo
  };

  const addIngrediente = () => {
    setIngredientes([...ingredientes, { nombre: '', cantidad: '' }]);
    console.log('Ingrediente añadido.'); // Log descriptivo
  };

  const removeIngrediente = (index) => {
    const newIngredientes = ingredientes.filter((_, i) => i !== index);
    setIngredientes(newIngredientes);
    console.log(`Ingrediente ${index} eliminado.`); // Log descriptivo
  };

  // Manejadores para pasos
  const handlePasoChange = (index, e) => {
    const newPasos = [...pasos];
    newPasos[index].descripcion = e.target.value;
    setPasos(newPasos);
    console.log(`Paso ${index} actualizado: ${e.target.value}`); // Log descriptivo
  };

  const addPaso = () => {
    setPasos([...pasos, { descripcion: '' }]);
    console.log('Paso añadido.'); // Log descriptivo
  };

  const removePaso = (index) => {
    const newPasos = pasos.filter((_, i) => i !== index);
    setPasos(newPasos);
    console.log(`Paso ${index} eliminado.`); // Log descriptivo
  };

  // Manejador para la selección de categorías
  const handleCategoryChange = (e) => {
    const categoryId = parseInt(e.target.value);
    const category = allCategories.find(cat => cat.id === categoryId);

    if (e.target.checked) {
      setSelectedCategories([...selectedCategories, category]);
      console.log(`Categoría seleccionada: ${category.nombre}`); // Log descriptivo
    } else {
      setSelectedCategories(selectedCategories.filter(cat => cat.id !== categoryId));
      console.log(`Categoría deseleccionada: ${category.nombre}`); // Log descriptivo
    }
  };

  // Manejador de envío del formulario
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccessMessage(null);
    console.log('Intentando crear receta...'); // Log descriptivo

    try {
      // Validaciones básicas
      if (!titulo || !descripcion || !tiempoPreparacion || !porciones || selectedCategories.length === 0 || ingredientes.some(i => !i.nombre || !i.cantidad) || pasos.some(p => !p.descripcion)) {
        throw new Error('Por favor, completa todos los campos obligatorios.');
      }

      const recipeData = {
        titulo,
        descripcion,
        tiempoPreparacion: parseInt(tiempoPreparacion),
        dificultad,
        porciones: parseInt(porciones),
        // Las categorías se envían como un array de objetos { id: categoriaId }
        categorias: selectedCategories.map(cat => ({ id: cat.id })),
        // Los ingredientes se envían como un array de objetos { ingredienteId: id, cantidad: "X" }
        // Aquí asumimos que los ingredientes ya existen en el backend y solo necesitamos su ID.
        // Si no existen, el backend debería manejarlos o necesitaríamos un endpoint para crearlos primero.
        // Por ahora, solo enviamos el nombre y la cantidad, el backend deberá resolver el ID.
        ingredientes: ingredientes.map(ing => ({ nombre: ing.nombre, cantidad: ing.cantidad })),
        pasos: pasos.map((paso, index) => ({ orden: index + 1, descripcion: paso.descripcion })),
      };

      console.log('Datos de la receta a enviar:', recipeData); // Log descriptivo
      const newRecipe = await createReceta(recipeData);
      console.log('Receta creada exitosamente:', newRecipe); // Log descriptivo

      // Si hay una imagen seleccionada, subirla
      if (selectedFile) {
        console.log('Subiendo imagen...'); // Log descriptivo
        const imageUrlResponse = await uploadRecetaImage(newRecipe.id, selectedFile);
        console.log('Imagen subida, URL:', imageUrlResponse); // Log descriptivo
        // Opcional: Actualizar la receta con la URL de la imagen si el backend lo requiere
        // Aunque la documentación sugiere que el endpoint de imagen ya asocia la URL.
      }

      setSuccessMessage('Receta creada exitosamente!');
      // Redirigir a la página de detalles de la nueva receta o a la página principal
      navigate(`/receta/${newRecipe.id}`);
    } catch (err) {
      console.error('Error al crear la receta:', err); // Log descriptivo
      setError(err.message || 'Error al crear la receta. Inténtalo de nuevo.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="create-recipe-page">
      <h2>Crear Nueva Receta</h2>
      {error && <p className="error-message">{error}</p>}
      {successMessage && <p className="success-message">{successMessage}</p>}

      <form onSubmit={handleSubmit} className="recipe-form">
        {/* Título */}
        <div className="form-group">
          <label htmlFor="titulo">Título:</label>
          <input
            type="text"
            id="titulo"
            name="titulo"
            value={titulo}
            onChange={handleChange}
            required
          />
        </div>

        {/* Descripción */}
        <div className="form-group">
          <label htmlFor="descripcion">Descripción:</label>
          <textarea
            id="descripcion"
            name="descripcion"
            value={descripcion}
            onChange={handleChange}
            required
          ></textarea>
        </div>

        {/* Tiempo de Preparación */}
        <div className="form-group">
          <label htmlFor="tiempoPreparacion">Tiempo de Preparación (minutos):</label>
          <input
            type="number"
            id="tiempoPreparacion"
            name="tiempoPreparacion"
            value={tiempoPreparacion}
            onChange={handleChange}
            required
            min="1"
          />
        </div>

        {/* Dificultad */}
        <div className="form-group">
          <label htmlFor="dificultad">Dificultad:</label>
          <select
            id="dificultad"
            name="dificultad"
            value={dificultad}
            onChange={handleChange}
          >
            <option value="FACIL">Fácil</option>
            <option value="MEDIA">Media</option>
            <option value="DIFICIL">Difícil</option>
          </select>
        </div>

        {/* Porciones */}
        <div className="form-group">
          <label htmlFor="porciones">Porciones:</label>
          <input
            type="number"
            id="porciones"
            name="porciones"
            value={porciones}
            onChange={handleChange}
            required
            min="1"
          />
        </div>

        {/* Imagen */}
        <div className="form-group">
          <label htmlFor="imagen">Imagen de la Receta:</label>
          <input
            type="file"
            id="imagen"
            name="imagen"
            accept="image/*"
            onChange={handleFileChange}
          />
          {previewImage && (
            <div className="image-preview">
              <img src={previewImage} alt="Previsualización" />
            </div>
          )}
        </div>

        {/* Categorías */}
        <div className="form-group">
          <label>Categorías:</label>
          <div className="categories-checkbox-group">
            {allCategories.map((cat) => (
              <label key={cat.id}>
                <input
                  type="checkbox"
                  value={cat.id}
                  checked={selectedCategories.some(selectedCat => selectedCat.id === cat.id)}
                  onChange={handleCategoryChange}
                />
                {cat.nombre}
              </label>
            ))}
          </div>
        </div>

        {/* Ingredientes */}
        <div className="form-group">
          <label>Ingredientes:</label>
          {ingredientes.map((ing, index) => (
            <div key={index} className="ingredient-item">
              <input
                type="text"
                name="nombre"
                placeholder="Nombre del ingrediente"
                value={ing.nombre}
                onChange={(e) => handleIngredienteChange(index, e)}
                required
              />
              <input
                type="text"
                name="cantidad"
                placeholder="Cantidad (ej: 200g, 2 unidades)"
                value={ing.cantidad}
                onChange={(e) => handleIngredienteChange(index, e)}
                required
              />
              <button type="button" onClick={() => removeIngrediente(index)} className="remove-button">
                Eliminar
              </button>
            </div>
          ))}
          <button type="button" onClick={addIngrediente} className="add-button">
            Añadir Ingrediente
          </button>
        </div>

        {/* Pasos */}
        <div className="form-group">
          <label>Pasos de Preparación:</label>
          {pasos.map((paso, index) => (
            <div key={index} className="step-item">
              <textarea
                placeholder={`Paso ${index + 1}`}
                value={paso.descripcion}
                onChange={(e) => handlePasoChange(index, e)}
                required
              ></textarea>
              <button type="button" onClick={() => removePaso(index)} className="remove-button">
                Eliminar
              </button>
            </div>
          ))}
          <button type="button" onClick={addPaso} className="add-button">
            Añadir Paso
          </button>
        </div>

        <button type="submit" disabled={loading} className="submit-button">
          {loading ? 'Creando...' : 'Crear Receta'}
        </button>
      </form>
    </div>
  );
};

export default CreateRecipePage;
