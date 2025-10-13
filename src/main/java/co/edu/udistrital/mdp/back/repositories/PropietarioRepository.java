package co.edu.udistrital.mdp.back.repositories;

import co.edu.udistrital.mdp.back.entities.PropietarioEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface PropietarioRepository extends JpaRepository<PropietarioEntity, Long> {

    // Busca un PropietarioEntity por documento
    Optional<PropietarioEntity> findByDocumento(String documento);

    // Busca un PropietarioEntity por email
    Optional<PropietarioEntity> findByEmail(String email);

    // Verifica si existe un PropietarioEntity con ese documento
    boolean existsByDocumento(String documento);

    // Verifica si existe un PropietarioEntity con ese email
    boolean existsByEmail(String email);

    // Busca PropietarioEntitys por nombre o apellido (búsqueda parcial)
    List<PropietarioEntity> findByNombreOrApellido(String nombre, String apellido);
}