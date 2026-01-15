package henriquez.ProyectoApi.Models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode // Usa Lombok para getters, setters, etc.
public class EstadoCuentaDetalleDTO {

    private Long identificadorDocumento;
    private String tipoTransaccion;
    private LocalDate fechaEmision;

    // Campo calculado: Fecha Emisión + 30 días
    private LocalDate fechaVencimiento;

    // Monto del documento (total mercadería)
    private BigDecimal monto;

    // Para identificar si suma o resta (Cargo: true, Abono: false)
    private boolean esCargo;

    // Saldo pendiente
    private BigDecimal saldoPendiente;

    // Campo calculado: Clasificación de Antigüedad (Ej: "31 - 60 días")
    private String clasificacionAntiguedad;

}
