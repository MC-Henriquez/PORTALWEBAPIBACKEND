package henriquez.ProyectoApi.Repositories.Cliente;

import henriquez.ProyectoApi.Entities.Cliente.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity,String> {

    Optional<ClienteEntity>findByCorreoElectronico(String correoElectronico);

    /**
     * Endpoint 1: Obtiene los datos crudos del cliente por ID.
     * La lógica de formato (NIT, nombre de contacto) se aplica en el Servicio.
     */
    @Query(value = """
    SELECT 
        c.id_cliente AS idCliente,
        c.nombre AS nombre,
        c.apellido AS apellido,
        c.razon_denominacion_social AS razonDenominacionSocial,
        c.nombre_comercial AS nombreComercial,  -- <-- AÑADIDO
        c.tipo_persona AS tipoPersona,
        c.nit AS nit,
        c.telefono AS telefono,
        c.direccion AS direccion,
        c.limite_credito AS limiteCredito
    FROM tb_cliente c
    WHERE c.id_cliente = :idCliente
    """, nativeQuery = true)
    Optional<Map<String, Object>> findRawDataById(String idCliente);

}
