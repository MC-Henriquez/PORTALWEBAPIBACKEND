package henriquez.ProyectoApi.Repositories.Rol;


import henriquez.ProyectoApi.Entities.Rol.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository   extends JpaRepository<RolEntity,Long> {


}
