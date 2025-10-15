package co.edu.udistrital.mdp.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.back.entities.UniversidadCercaEntity;

import java.util.List;

@Repository
public interface UniversidadCercaRepository extends JpaRepository<UniversidadCercaEntity, Long> {

    // Buscar universidades por ciudad
    List<UniversidadCercaEntity> findByCiudad(String ciudad);

    // Buscar universidades cuyo nombre contenga texto
    List<UniversidadCercaEntity> findByNombreContaining(String nombre);
}
