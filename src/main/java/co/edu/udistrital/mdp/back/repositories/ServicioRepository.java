package co.edu.udistrital.mdp.back.repositories;

import co.edu.udistrital.mdp.back.entities.ServicioEntity;
import co.edu.udistrital.mdp.back.entities.ServicioEntity.CategoriaServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServicioRepository extends JpaRepository<ServicioEntity, Long> {

    // Busca un ServicioEntity por nombre
    Optional<ServicioEntity> findByNombre(String nombre);

    // Busca ServicioEntitys por categoría
    List<ServicioEntity> findByCategoria(CategoriaServicio categoria);

    // Verifica si existe un ServicioEntity con ese nombre
    boolean existsByNombreIgnoreCase(String nombre);

}
