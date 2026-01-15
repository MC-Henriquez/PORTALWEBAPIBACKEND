package henriquez.ProyectoApi.Models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @ToString  @EqualsAndHashCode
public class CuentasDTO {

        private Long numeroDocumento;

        private Long documentoAsociado;

        private String tipoDeTransaccion;

        private String codigoCliente; // LA fk del idCliente

        private String claseCliente;

        private BigDecimal percepcion;

        private String bodega;

        private String vendedor;

        private String cobrador;

        private String impuesto;

        private String condicionPago;

        private String nit;

        private String registro;

        private LocalDate fechaIngreso;

        private BigDecimal totalImpuesto;

        private BigDecimal totalPercepcion;

        private BigDecimal totalMercaderia;

        private BigDecimal saldoDocumento;

        private LocalDate fechaPago;

        private String descripcion;

        private String naturaleza;





}
