package co.edu.udistrital.mdp.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.back.entities.UniversidadCerca;

import java.util.List;

@Repository
public interface UniversidadCercaRepository extends JpaRepository<UniversidadCerca, Long> {

    // Buscar universidades por ciudad
    List<UniversidadCerca> findByCiudad(String ciudad);

    // Buscar universidades cuyo nombre contenga texto
    List<UniversidadCerca> findByNombreContaining(String nombre);
}
