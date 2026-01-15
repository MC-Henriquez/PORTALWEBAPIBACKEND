package henriquez.ProyectoApi.Repositories.Empleado;

import henriquez.ProyectoApi.Entities.Empleado.EmpleadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<EmpleadoEntity, Long> {

    Optional<EmpleadoEntity> findByCorreoElectronico(String correoElectronico);


}
