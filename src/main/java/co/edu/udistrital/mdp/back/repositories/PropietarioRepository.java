package co.edu.udistrital.mdp.back.repositories;

import co.edu.udistrital.mdp.back.entities.Propietario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface PropietarioRepository extends JpaRepository<Propietario, Long> {

    // Busca un propietario por documento
    Optional<Propietario> findByDocumento(String documento);

    // Busca un propietario por email
    Optional<Propietario> findByEmail(String email);

    // Verifica si existe un propietario con ese documento
    boolean existsByDocumento(String documento);

    // Verifica si existe un propietario con ese email
    boolean existsByEmail(String email);

    // Busca propietarios por nombre o apellido (búsqueda parcial)
    List<Propietario> findByNombreOApellido(String nombre, String apellido);
}