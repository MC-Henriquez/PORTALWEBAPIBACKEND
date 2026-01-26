package henriquez.ProyectoApi.Services.Cuentas;

import henriquez.ProyectoApi.Entities.Cuentas.CuentasEntity;
import henriquez.ProyectoApi.Exceptions.Rol.TransactionRecordsNotFoundException;
import henriquez.ProyectoApi.Models.*;
import henriquez.ProyectoApi.Repositories.Cuentas.CuentasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public List<CuentasEntity> obtenerPorCliente(String idCliente) {
    return cuentasRepository.findByCliente(idCliente);
}


@Service
public class CuentasService {

    // 1. Definir los códigos que representan un ABONO
    private static final Set<String> CODIGOS_ABONO = Set.of(
            "AA", // Abono por Ajuste
            "AB", // Abono a Factura
            "AC", // Abono a Cheque
            "AL", // Abono Por Letra
            "DC", // Nota de Crédito
            "DF", // Devolución a Factura
            "NC", // Nota de Crédito (Visto en la tabla de ejemplo, aunque 'DC' es el código en la lista principal)
            "PAG" // Pago (Visto en la tabla de ejemplo)
    );
    /**
     * Acá se inyecta el repositorio de cuentas
     */
    @Autowired
    private CuentasRepository repo;

    public List<CuentasDTO>MostrarCuenta(){
        List<CuentasEntity>CuentasEntity = repo.findAll();
        return CuentasEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------------------
    // LÓGICA PARA ESTADO DE CUENTA
    // -------------------------------------------------------------------------------------

    /**
     * Define la clasificación de antigüedad basada en el número de días.
     * @param diasAntiguedad La diferencia de días entre la fecha actual y la fecha de emisión.
     * @return String con la clasificación (ej. "31 - 60 días").
     */
    private String clasificarAntiguedad(long diasAntiguedad) {
        if (diasAntiguedad < 0) {
            return "Emisión Futura"; // Para documentos con fecha futura (aunque inusual en cuentas por cobrar)
        } else if (diasAntiguedad <= 30) {
            return "Menos de 30 días";
        } else if (diasAntiguedad <= 60) {
            return "31 - 60 días";
        } else if (diasAntiguedad <= 90) {
            return "61 - 90 días";
        } else {
            return "Más de 91 días";
        }
    }

    /**
     * Obtiene el detalle de transacciones de un cliente en un periodo específico,
     * aplicando los cálculos de vencimiento, clasificación de cargo/abono y antigüedad.
     *
     * @param idCliente El identificador del cliente.
     * @param fechaInicio La fecha de inicio del periodo.
     * @param fechaFin La fecha de fin del periodo.
     * @return Lista de DTOs con el detalle de las transacciones y sus cálculos.
     */
    public List<EstadoCuentaDetalleDTO> obtenerDetalleTransacciones(
            String idCliente, LocalDate fechaInicio, LocalDate fechaFin) {

        // 1. Obtener la fecha actual del servidor (Fecha Base para cálculo de antigüedad)
        final LocalDate fechaBaseCalculo = LocalDate.now();

        // 2. Consulta a la BD EFICIENTE:
        List<CuentasEntity> transacciones = repo.findByClienteIdClienteAndFechaIngresoBetween(
                idCliente,
                fechaInicio,
                fechaFin
        );


        // 3. Mapeo a DTO y aplicación de la lógica de negocio (cálculos)
        return transacciones.stream()
                .map(t -> {
                    EstadoCuentaDetalleDTO dto = new EstadoCuentaDetalleDTO();
                    dto.setIdentificadorDocumento(t.getNumeroDocumento());
                    dto.setTipoTransaccion(t.getTipoDeTransaccion());
                    dto.setFechaEmision(t.getFechaIngreso());
                    dto.setMonto(Optional.ofNullable(t.getTotalMercaderia()).orElse(BigDecimal.ZERO));
                    dto.setSaldoPendiente(Optional.ofNullable(t.getSaldoDocumento()).orElse(BigDecimal.ZERO));

                    // --- Lógica: Cargo vs Abono (Abono si el código está en CODIGOS_ABONO) ---
                    String tipoTransaccion = Optional.ofNullable(t.getTipoDeTransaccion()).orElse("").toUpperCase();
                    // Es un CARGO si el código NO está en nuestra lista de códigos de ABONO
                    boolean esCargo = !CODIGOS_ABONO.contains(tipoTransaccion);
                    dto.setEsCargo(esCargo);

                    // --- CÁLCULO CONDICIONAL DE FECHA DE VENCIMIENTO (SOLO PARA CARGOS) ---
                    if (esCargo) {
                        // Aplica solo a Cargos (Facturas, Notas de Débito, etc.)
                        // ASUMIMOS que la condición de pago estándar es 30 días.
                        LocalDate fechaVencimiento = t.getFechaIngreso().plusDays(30);
                        dto.setFechaVencimiento(fechaVencimiento);
                    } else {
                        // Para Abonos (Notas de Crédito, Recibos), la fecha de vencimiento es NULL.
                        dto.setFechaVencimiento(null);
                    }

                    // --- Cálculo: Clasificación de Antigüedad ---
                    if (dto.getSaldoPendiente().compareTo(BigDecimal.ZERO) > 0) {
                        // Diferencia de días entre la fecha de emisión y la fecha actual del servidor
                        long diasAntiguedad = ChronoUnit.DAYS.between(t.getFechaIngreso(), fechaBaseCalculo);
                        dto.setClasificacionAntiguedad(clasificarAntiguedad(diasAntiguedad));
                    } else {
                        dto.setClasificacionAntiguedad("Pagado / Sin Saldo");
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public RangoHistoricoDTO obtenerRangoHistorico(String clienteId) {

        // Llama al nuevo metodo del repository
        Object[] resultados = repo.findMinMaxFechaIngresoByCliente_IdCliente(clienteId);

        // Si la lista está vacía, la longitud es 0. Si el elemento anidado es null, también falla.
        if (resultados == null || resultados.length == 0 || resultados[0] == null) {
            // Lanzar la excepción con el mensaje deseado
            throw new TransactionRecordsNotFoundException(
                    "Todavía no hay registros de transacciones para este cliente.");
        }

        Object[] fechas = (Object[]) resultados[0];

        if (fechas.length < 2 || fechas[0] == null || fechas[1] == null) {
            // En caso de que se haya encontrado el registro, pero las fechas sean null.
            throw new TransactionRecordsNotFoundException(
                    "Los registros encontrados no contienen las fechas de inicio/fin válidas para el rango histórico.");
        }


        Object minObj = fechas[0];
        Object maxObj = fechas[1];

        if (fechas.length < 2) {
            return null; // Aún puedes validar si el array interno no tiene 2 elementos.
        }

        LocalDate fechaMin;
        LocalDate fechaMax;

        if (minObj instanceof java.util.Date) {
            // Necesitas convertir java.util.Date a LocalDate
            fechaMin = ((java.util.Date) minObj).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            fechaMax = ((java.util.Date) maxObj).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        } else {
            // Intenta el cast directo a LocalDate
            fechaMin = (LocalDate) minObj;
            fechaMax = (LocalDate) maxObj;
        }

        RangoHistoricoDTO dto = new RangoHistoricoDTO();
        // Extrae el año de cada fecha
        dto.setAnioInicio(fechaMin.getYear());
        dto.setAnioFin(fechaMax.getYear());

        return dto;
    }

    /**
     * Calcula la suma total histórica de Cargos y Abonos de un cliente.
     * La suma se realiza sobre el campo total_mercaderia (monto original del documento).
     * @param idCliente El identificador del cliente.
     * @return DTO con los totales consolidados.
     */
    public TotalConsolidadoDTO obtenerTotalesConsolidados(String idCliente) {

        // 1. Obtener TODAS las transacciones del cliente (Histórico Completo)
        // Se asume que repo.findByCliente(idCliente) está disponible.
        List<CuentasEntity> todasLasCuentas = repo.findByCliente(idCliente);

        TotalConsolidadoDTO totales = new TotalConsolidadoDTO();

        // 2. Iterar sobre las cuentas y sumar por clasificación (Cargo/Abono)
        for (CuentasEntity cuenta : todasLasCuentas) {

            String tipoTransaccion = Optional.ofNullable(cuenta.getTipoDeTransaccion()).orElse("").toUpperCase();
            // El monto a sumar es el totalMercaderia, asegurando que no sea null para evitar NullPointerException.
            BigDecimal monto = Optional.ofNullable(cuenta.getTotalMercaderia()).orElse(BigDecimal.ZERO);

            // La lógica de Cargo/Abono es la misma que usamos en el detalle.
            if (CODIGOS_ABONO.contains(tipoTransaccion)) {
                // Es un ABONO (reduce la deuda)
                totales.setTotalAbonos(totales.getTotalAbonos().add(monto));
            } else {
                // Es un CARGO (aumenta la deuda)
                totales.setTotalCargos(totales.getTotalCargos().add(monto));
            }
        }

        return totales;
    }

    private CuentasDTO convertToDTO (CuentasEntity cuentasEntity){
        CuentasDTO cuentasDTO = new CuentasDTO();
        cuentasDTO.setNumeroDocumento(cuentasEntity.getNumeroDocumento());
        cuentasDTO.setDocumentoAsociado(cuentasEntity.getDocumentoAsociado());
        cuentasDTO.setTipoDeTransaccion(cuentasEntity.getTipoDeTransaccion());
        cuentasDTO.setClaseCliente(cuentasEntity.getClaseCliente());
        cuentasDTO.setPercepcion(cuentasEntity.getPercepcion());
        cuentasDTO.setBodega(cuentasEntity.getBodega());
        cuentasDTO.setVendedor(cuentasEntity.getVendedor());
        cuentasDTO.setCobrador(cuentasEntity.getCobrador());
        cuentasDTO.setImpuesto(cuentasEntity.getImpuesto());
        cuentasDTO.setCondicionPago(cuentasEntity.getCondicionPago());
        cuentasDTO.setNit(cuentasEntity.getNit());
        cuentasDTO.setRegistro(cuentasEntity.getRegistro());
        cuentasDTO.setFechaIngreso(cuentasEntity.getFechaIngreso());
        cuentasDTO.setTotalImpuesto(cuentasEntity.getTotalImpuesto());
        cuentasDTO.setTotalPercepcion(cuentasEntity.getTotalPercepcion());
        cuentasDTO.setTotalMercaderia(cuentasEntity.getTotalMercaderia());
        cuentasDTO.setSaldoDocumento(cuentasEntity.getSaldoDocumento());
        cuentasDTO.setFechaPago(cuentasEntity.getFechaPago());
        cuentasDTO.setDescripcion(cuentasEntity.getDescripcion());
        cuentasDTO.setNaturaleza(cuentasEntity.getNaturaleza());

        if (cuentasEntity.getCliente()!= null){
            cuentasDTO.setCodigoCliente(cuentasEntity.getCliente().getIdCliente());
        } else {
            cuentasDTO.setCodigoCliente(null);
        }
        return cuentasDTO;
    }

    public List<CuentaDTO> obtenerTop5PorCliente(String idCliente) {
        List<CuentasEntity> cuentas = repo.obtenerTop5Proximos(idCliente);

        return cuentas.stream().map(c -> {
            CuentaDTO dto = new CuentaDTO();
            dto.setNumeroDocumento(c.getNumeroDocumento());
            dto.setTipoDeTransaccion(c.getTipoDeTransaccion());
            dto.setFechaIngreso(c.getFechaIngreso());
            dto.setFechaPago(c.getFechaPago());
            return dto;
        }).toList();
    }
}
