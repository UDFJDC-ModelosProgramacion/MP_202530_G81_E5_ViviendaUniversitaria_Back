package co.edu.udistrital.mdp.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.back.entities.UniversidadCercaEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface UniversidadCercaRepository extends JpaRepository<UniversidadCercaEntity, Long> {

    // Buscar universidades por ciudad
    List<UniversidadCercaEntity> findByCiudad(String ciudad);

    // Buscar universidades cuyo nombre contenga texto
    List<UniversidadCercaEntity> findByNombreContaining(String nombre);

    // Verificar existencia por nombre ignorando mayúsculas/minúsculas
    boolean existsByNombreIgnoreCase(String nombre);
    
    // Encontrar por nombre exacto ignorando mayúsculas/minúsculas
    Optional<UniversidadCercaEntity> findByNombreIgnoreCase(String nombre);
}
