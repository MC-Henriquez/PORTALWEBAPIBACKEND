package henriquez.ProyectoApi.Models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class TotalConsolidadoDTO {

    // Total de documentos que aumentan la deuda (FA, ND, etc.)
    private BigDecimal totalCargos;

    // Total de documentos que reducen la deuda (AB, DC, etc.)
    private BigDecimal totalAbonos;

    public TotalConsolidadoDTO() {
        this.totalCargos = BigDecimal.ZERO;
        this.totalAbonos = BigDecimal.ZERO;
    }
}
