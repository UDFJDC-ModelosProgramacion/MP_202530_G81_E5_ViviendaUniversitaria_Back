package co.edu.udistrital.mdp.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import co.edu.udistrital.mdp.back.entities.ContratoEntity;
import java.util.Optional;
import java.util.List;


@Repository
public interface ContratoRepository extends JpaRepository<ContratoEntity, Long> {

    // Buscar ContratoEntity por código
    Optional<ContratoEntity> findByCodigo(String codigo);

    // Buscar ContratoEntitys por monto mayor a cierto valor
    List<ContratoEntity> findByMontoTotalGreaterThan(Double monto);

    // Buscar ContratoEntitys que terminen en una fecha específica
    List<ContratoEntity> findByFechaFin(java.time.LocalDate fechaFin);
}
