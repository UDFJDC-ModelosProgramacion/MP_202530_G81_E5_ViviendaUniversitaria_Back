package co.edu.udistrital.mdp.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import co.edu.udistrital.mdp.back.entities.Contrato;
import java.util.Optional;
import java.util.List;


@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    // Buscar contrato por código
    Optional<Contrato> findByCodigo(String codigo);

    // Buscar contratos por monto mayor a cierto valor
    List<Contrato> findByMontoTotalGreaterThan(Double monto);

    // Buscar contratos que terminen en una fecha específica
    List<Contrato> findByFechaFin(java.time.LocalDate fechaFin);
}
