package henriquez.ProyectoApi.Models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class CuentaDTO {

    private Long numeroDocumento;
    private String tipoDeTransaccion;
    private LocalDate fechaIngreso;
    private LocalDate fechaPago;
}
