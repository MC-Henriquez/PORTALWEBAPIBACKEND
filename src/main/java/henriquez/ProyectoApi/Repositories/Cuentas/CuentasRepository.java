package henriquez.ProyectoApi.Repositories.Cuentas;

import henriquez.ProyectoApi.Entities.Cuentas.CuentasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CuentasRepository extends JpaRepository <CuentasEntity, Long>{

    // 1. Obtener TODAS las cuentas de un cliente
    @Query("SELECT c FROM CuentasEntity c WHERE c.cliente.idCliente = :idCliente")
    List<CuentasEntity> findByCliente(String idCliente);

    @Query("SELECT MIN(c.fechaIngreso), MAX(c.fechaIngreso) FROM CuentasEntity c WHERE c.cliente.idCliente = :clienteId")
    Object[] findMinMaxFechaIngresoByCliente_IdCliente(@Param("clienteId") String clienteId);

    // 2. Obtener cuentas de un cliente filtradas por rango de fecha de emisión (fechaIngreso)
    /**
     * Busca cuentas por el ID del cliente y cuya fecha de ingreso (emisión) esté entre
     * la fecha de inicio y la fecha de fin.
     */
    List<CuentasEntity> findByClienteIdClienteAndFechaIngresoBetween(
            String idCliente,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    @Query(value = """
        SELECT * 
        FROM tb_cuentas_por_cobrar 
        WHERE codigo_cliente = :idCliente
          AND fecha_pago IS NOT NULL
        ORDER BY fecha_pago ASC
        LIMIT 5
        """, nativeQuery = true)
    List<CuentasEntity> obtenerTop5Proximos(@Param("idCliente") String  idCliente);

}
