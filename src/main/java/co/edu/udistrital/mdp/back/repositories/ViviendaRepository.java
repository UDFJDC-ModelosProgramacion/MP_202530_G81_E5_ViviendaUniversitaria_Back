package co.edu.udistrital.mdp.back.repositories;

import co.edu.udistrital.mdp.back.entities.Vivienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ViviendaRepository extends JpaRepository<Vivienda, Long> {

    // Busca viviendas por propietario
    List<Vivienda> findByPropietarioId(Long propietarioId);

    // Busca viviendas disponibles por ciudad
    List<Vivienda> findByCiudadAndDisponible(String ciudad, Boolean disponible);

    // Busca viviendas en rango de precio
    List<Vivienda> findByPrecioMensualBetween(BigDecimal precioMin, BigDecimal precioMax);
}
