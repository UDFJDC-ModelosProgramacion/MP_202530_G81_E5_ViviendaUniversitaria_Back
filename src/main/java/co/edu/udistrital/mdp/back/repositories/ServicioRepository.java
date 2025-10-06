package co.edu.udistrital.mdp.back.repositories;

import co.edu.udistrital.mdp.back.entities.Servicio;
import co.edu.udistrital.mdp.back.entities.Servicio.CategoriaServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    // Busca un servicio por nombre
    Optional<Servicio> findByNombre(String nombre);

    // Busca servicios por categoría
    List<Servicio> findByCategoria(CategoriaServicio categoria);

    // Verifica si existe un servicio con ese nombre
    boolean existsByNombreIgnoreCase(String nombre);

}
