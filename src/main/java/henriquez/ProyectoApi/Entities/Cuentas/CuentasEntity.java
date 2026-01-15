package henriquez.ProyectoApi.Entities.Cuentas;


import henriquez.ProyectoApi.Entities.Cliente.ClienteEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Table(name= "tb_cuentas_por_cobrar")
public class CuentasEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "numero_documento")
    private Long numeroDocumento;

    @Column(name="documento_asociado")
    private Long documentoAsociado;

    @Column(name = "tipo_de_transaccion")
    private String tipoDeTransaccion;

    @ManyToOne
    @JoinColumn(name = "codigo_cliente")
    private ClienteEntity cliente;

    @Column(name = "clase_cliente")
    private String claseCliente;

    @Column(name = "percepcion")
    private BigDecimal percepcion;

    @Column(name = "bodega")
    private String bodega;

    @Column(name = "vendedor")
    private String vendedor;

    @Column(name = "cobrador")
    private String cobrador;

    @Column(name = "impuesto")
    private String impuesto;

    @Column(name = "condicion_pago")
    private String condicionPago;

    @Column(name = "nit")
    private String nit;

    @Column(name = "registro")
    private String registro;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @Column(name = "total_impuesto")
    private BigDecimal totalImpuesto;

    @Column(name = "total_percepcion")
    private BigDecimal totalPercepcion;

    @Column(name = "total_mercaderia")
    private BigDecimal totalMercaderia;

    @Column(name = "saldo_documento")
    private BigDecimal saldoDocumento;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "naturaleza")
    private String naturaleza;

}


