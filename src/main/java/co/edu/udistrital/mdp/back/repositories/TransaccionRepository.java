package co.edu.udistrital.mdp.back.repositories;

import co.edu.udistrital.mdp.back.entities.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    // Recupera todas las transacciones asociadas a una estancia.
    List<Transaccion> findByEstanciaId(Long estanciaId);

    // Busca transacciones por su estado (ej: "Completada", "Pendiente").
    List<Transaccion> findByEstado(String estado);
}