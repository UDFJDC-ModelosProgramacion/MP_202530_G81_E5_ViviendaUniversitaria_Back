package co.edu.udistrital.mdp.back.repositories;

import co.edu.udistrital.mdp.back.entities.UniversidadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UniversidadRepository extends JpaRepository<UniversidadEntity, Long> {
}
