package com.recetas.backend.service;

import com.recetas.backend.exception.ImageUploadException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ByteArrayResource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la carga de imágenes utilizando la API de imgbb.
 */
@Service
public class ImageUploadService {

    // Clave API de imgbb, con un valor por defecto si no se configura en
    // application.properties
    @Value("${imgbb.api.key:9dcb1f2df4f6375a49e21a785345d86f}")
    private String imgbbApiKey;

    // URL de la API de subida de imgbb
    private final String imgbbUploadUrl = "https://api.imgbb.com/1/upload";

    // RestTemplate para realizar las llamadas HTTP
    private final RestTemplate restTemplate = new RestTemplate();
    // ObjectMapper para parsear las respuestas JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ImageService imageService; // Inyectar ImageService

    /**
     * Constructor para inyectar el servicio de imágenes.
     *
     * @param imageService El servicio de imágenes.
     */
    @Autowired
    public ImageUploadService(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * Sube una imagen a imgbb con un nombre de archivo personalizado.
     *
     * @param imageFile    El archivo de imagen a subir.
     * @param categoryName El nombre de la categoría a la que pertenece la receta.
     * @param recipeTitle  El título de la receta.
     * @return La URL de la imagen subida.
     * @throws IOException          Si ocurre un error al procesar el archivo.
     * @throws ImageUploadException Si la subida de la imagen falla o la respuesta
     *                              de imgbb no es válida.
     */
    public String uploadImage(MultipartFile imageFile, String categoryName, String recipeTitle)
            throws IOException, ImageUploadException {
        // Configurar las cabeceras para la solicitud multipart/form-data
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Extraer la extensión del archivo original
        String originalFilename = imageFile.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Generar un timestamp para asegurar un nombre de archivo único
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        // Construir el nuevo nombre del archivo, limpiando espacios y añadiendo
        // timestamp
        String baseName = (categoryName != null ? categoryName.replaceAll("\\s+", "") : "unknown") + "-"
                + (recipeTitle != null ? recipeTitle.replaceAll("\\s+", "") : "recipe");
        String newFilename = baseName + "-" + timestamp + fileExtension;

        // Crear el cuerpo de la solicitud con los parámetros obligatorios
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("key", imgbbApiKey); // Añadir la clave API
        body.add("image", new ByteArrayResource(imageFile.getBytes()) {
            @Override
            public String getFilename() {
                return newFilename; // Usar el nombre de archivo generado
            }
        });
        // Añadir el nombre de la imagen, eliminando espacios en blanco del título de la
        // receta
        if (recipeTitle != null && !recipeTitle.trim().isEmpty()) {
            body.add("name", recipeTitle.replaceAll("\\s+", ""));
        }

        // Crear la entidad HTTP con el cuerpo y las cabeceras
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            // Realizar la llamada a la API de imgbb
            ResponseEntity<String> response = restTemplate.exchange(
                    imgbbUploadUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            // Verificar si la respuesta fue exitosa
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Parsear la respuesta JSON
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode dataNode = rootNode.path("data");
                // Comprobar si la URL de la imagen y el delete_hash están presentes en la
                // respuesta
                if (dataNode.has("url") && dataNode.has("deletehash")) {
                    String imageUrl = dataNode.path("url").asText();
                    String deleteHash = dataNode.path("deletehash").asText();

                    // Guardar la referencia de la imagen en la base de datos
                    imageService.saveImage(imageUrl, deleteHash);

                    System.out.println("Imagen subida correctamente. URL: " + imageUrl);
                    return imageUrl;
                } else {
                    // Si la URL no está presente, lanzar una excepción
                    String errorMessage = "Error: La respuesta de imgbb no contiene la URL de la imagen. Respuesta completa: "
                            + response.getBody();
                    System.err.println(errorMessage);
                    throw new ImageUploadException(errorMessage);
                }
            } else {
                // Si la respuesta no es exitosa, lanzar una excepción con el código de estado
                String errorMessage = "Error al subir la imagen a imgbb. Código de estado: "
                        + response.getStatusCode() + ". Cuerpo de la respuesta: " + response.getBody();
                System.err.println(errorMessage);
                throw new ImageUploadException(errorMessage);
            }
        } catch (IOException e) {
            // Capturar errores de E/S al leer el archivo o parsear JSON
            System.err.println("Excepción de E/S al subir la imagen a imgbb: " + e.getMessage());
            throw new ImageUploadException("Error de E/S al procesar la imagen para subirla.", e);
        } catch (Exception e) {
            // Capturar cualquier otra excepción durante la subida
            System.err.println("Excepción inesperada al subir la imagen a imgbb: " + e.getMessage());
            throw new ImageUploadException("Error inesperado durante la subida de la imagen.", e);
        }
    }

    /**
     * Elimina una imagen de imgbb y de la base de datos.
     *
     * @param deleteHash El hash de eliminación de la imagen en imgbb.
     * @throws ImageUploadException Si la eliminación falla o la imagen no se
     *                              encuentra.
     */
    public void deleteImage(String deleteHash) throws ImageUploadException {
        // Primero, intentar eliminar de imgbb
        String imgbbDeleteUrl = "https://api.imgbb.com/1/delete/" + deleteHash + "?key=" + imgbbApiKey;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    imgbbDeleteUrl,
                    HttpMethod.DELETE,
                    requestEntity,
                    String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                boolean success = rootNode.path("success").asBoolean();
                if (success) {
                    System.out.println("Imagen eliminada de imgbb con deleteHash: " + deleteHash);
                    // Si se elimina de imgbb, eliminar también de la base de datos
                    imageService.deleteImageByDeleteHash(deleteHash);
                } else {
                    String errorMessage = "Error al eliminar la imagen de imgbb. Respuesta: " + response.getBody();
                    System.err.println(errorMessage);
                    throw new ImageUploadException(errorMessage);
                }
            } else {
                String errorMessage = "Error al eliminar la imagen de imgbb. Código de estado: "
                        + response.getStatusCode() + ". Cuerpo de la respuesta: " + response.getBody();
                System.err.println(errorMessage);
                throw new ImageUploadException(errorMessage);
            }
        } catch (IOException e) {
            System.err.println("Excepción de E/S al eliminar la imagen de imgbb: " + e.getMessage());
            throw new ImageUploadException("Error de E/S al procesar la eliminación de la imagen.", e);
        } catch (Exception e) {
            System.err.println("Excepción inesperada al eliminar la imagen de imgbb: " + e.getMessage());
            throw new ImageUploadException("Error inesperado durante la eliminación de la imagen.", e);
        }
    }

    /**
     * Lista todas las URLs de las imágenes almacenadas en la base de datos.
     *
     * @return Una lista de cadenas con las URLs de todas las imágenes.
     */
    public List<String> listAllImageUrls() {
        System.out.println("Obteniendo todas las URLs de las imágenes de la base de datos.");
        return imageService.getAllImages().stream()
                .map(com.recetas.backend.domain.entity.Image::getUrl)
                .collect(Collectors.toList());
    }
}
