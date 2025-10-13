package co.edu.udistrital.mdp.back.repositories;

import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ViviendaRepository extends JpaRepository<ViviendaEntity, Long> {

    // Busca ViviendaEntitys por propietario
    List<ViviendaEntity> findByPropietarioId(Long propietarioId);

    // Busca ViviendaEntitys disponibles por ciudad
    List<ViviendaEntity> findByCiudadAndDisponible(String ciudad, Boolean disponible);

    // Busca ViviendaEntitys en rango de precio
    List<ViviendaEntity> findByPrecioMensualBetween(BigDecimal precioMin, BigDecimal precioMax);
}
