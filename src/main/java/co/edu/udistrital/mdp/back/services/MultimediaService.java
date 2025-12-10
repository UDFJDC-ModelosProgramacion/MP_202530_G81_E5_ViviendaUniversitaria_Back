package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.MultimediaEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.repositories.MultimediaRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MultimediaService {

    private final MultimediaRepository multimediaRepository;
    private final ViviendaRepository viviendaRepository;
    private final CloudinaryService cloudinaryService;

    /**
     * Sube una imagen y la asocia a una vivienda
     */
    @Transactional
    public MultimediaEntity subirImagenVivienda(
            Long viviendaId,
            MultipartFile file,
            String titulo,
            String descripcion,
            Boolean esPortada) throws IOException {
        // Validar que la vivienda existe
        ViviendaEntity vivienda = viviendaRepository.findById(viviendaId)
                .orElseThrow(() -> new IllegalArgumentException("Vivienda no encontrada con id: " + viviendaId));

        // Subir imagen a Cloudinary
        String folder = "viviendas/vivienda_" + viviendaId;
        String urlArchivo = cloudinaryService.uploadImage(file, folder);

        // Determinar tipo de archivo desde el content type
        String tipoArchivo = file.getContentType();
        if (tipoArchivo != null && tipoArchivo.startsWith("image/")) {
            tipoArchivo = "IMAGEN";
        } else {
            tipoArchivo = "OTRO";
        }

        // Si se marca como portada, quitar la portada actual
        if (Boolean.TRUE.equals(esPortada)) {
            Optional<MultimediaEntity> portadaActual = multimediaRepository
                    .findByViviendaIdAndEsPortada(viviendaId, true);
            portadaActual.ifPresent(m -> {
                m.setEsPortada(false);
                multimediaRepository.save(m);
            });
        }

        // Determinar el orden de visualización
        Long count = multimediaRepository.countByViviendaId(viviendaId);
        int ordenVisualizacion = (count != null) ? count.intValue() + 1 : 1;

        // Crear entidad multimedia
        MultimediaEntity multimedia = new MultimediaEntity();
        multimedia.setVivienda(vivienda);
        multimedia.setTitulo(titulo != null ? titulo : "Imagen " + ordenVisualizacion);
        multimedia.setDescripcion(descripcion);
        multimedia.setTipoArchivo(tipoArchivo);
        multimedia.setUrlArchivo(urlArchivo);
        multimedia.setOrdenVisualizacion(ordenVisualizacion);
        multimedia.setEsPortada(Boolean.TRUE.equals(esPortada));

        return multimediaRepository.save(multimedia);
    }

    /**
     * Obtiene todas las imágenes de una vivienda
     */
    public List<MultimediaEntity> obtenerImagenesVivienda(Long viviendaId) {
        // Simplemente retornar la lista (vacía si no hay imágenes)
        return multimediaRepository.findByViviendaIdOrderByOrdenVisualizacionAsc(viviendaId);
    }

    /**
     * Marca una imagen como portada
     */
    @Transactional
    public MultimediaEntity marcarComoPortada(Long multimediaId) {
        MultimediaEntity multimedia = multimediaRepository.findById(multimediaId)
                .orElseThrow(() -> new IllegalArgumentException("Multimedia no encontrada con id: " + multimediaId));

        Long viviendaId = multimedia.getVivienda().getId();

        // Quitar portada actual
        Optional<MultimediaEntity> portadaActual = multimediaRepository
                .findByViviendaIdAndEsPortada(viviendaId, true);
        portadaActual.ifPresent(m -> {
            m.setEsPortada(false);
            multimediaRepository.save(m);
        });

        // Marcar nueva portada
        multimedia.setEsPortada(true);
        return multimediaRepository.save(multimedia);
    }

    /**
     * Elimina una imagen (de BD y de Cloudinary)
     */
    @Transactional
    public void eliminarImagen(Long multimediaId) throws IOException {
        MultimediaEntity multimedia = multimediaRepository.findById(multimediaId)
                .orElseThrow(() -> new IllegalArgumentException("Multimedia no encontrada con id: " + multimediaId));

        // Intentar eliminar de Cloudinary
        try {
            String publicId = cloudinaryService.extractPublicIdFromUrl(multimedia.getUrlArchivo());
            if (publicId != null) {
                cloudinaryService.deleteImage(publicId);
            }
        } catch (Exception e) {
            // Si falla, solo loguear, no impedir la eliminación de la BD
            System.err.println("Error al eliminar de Cloudinary: " + e.getMessage());
        }

        // Eliminar de la BD
        multimediaRepository.delete(multimedia);
    }
}
