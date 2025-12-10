package co.edu.udistrital.mdp.back.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Sube una imagen a Cloudinary
     * 
     * @param file   El archivo a subir
     * @param folder Carpeta donde se guardará (ej: "viviendas/vivienda_123")
     * @return URL pública de la imagen subida
     */
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        // Validar tipo de archivo
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen");
        }

        // Validar tamaño (máximo 5MB)
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo de 5MB");
        }

        try {
            // Generar nombre único para el archivo
            String publicId = folder + "/" + UUID.randomUUID().toString();

            // Subir a Cloudinary
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "folder", folder,
                            "resource_type", "image",
                            "transformation", ObjectUtils.asMap(
                                    "quality", "auto:good",
                                    "fetch_format", "auto")));

            // Retornar URL segura
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new IOException("Error al subir la imagen a Cloudinary: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina una imagen de Cloudinary
     * 
     * @param publicId ID público de la imagen en Cloudinary
     */
    public void deleteImage(String publicId) throws IOException {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new IOException("Error al eliminar la imagen de Cloudinary: " + e.getMessage(), e);
        }
    }

    /**
     * Extrae el public_id de una URL de Cloudinary
     * 
     * @param url URL completa de Cloudinary
     * @return public_id
     */
    public String extractPublicIdFromUrl(String url) {
        // URL típica:
        // https://res.cloudinary.com/{cloud_name}/image/upload/v{version}/{public_id}.{format}
        if (url == null || !url.contains("cloudinary.com")) {
            return null;
        }

        try {
            String[] parts = url.split("/upload/");
            if (parts.length > 1) {
                String pathWithVersion = parts[1];
                // Remover versión si existe (v123456789/)
                String path = pathWithVersion.replaceFirst("v\\d+/", "");
                // Remover extensión
                return path.substring(0, path.lastIndexOf('.'));
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }
}
