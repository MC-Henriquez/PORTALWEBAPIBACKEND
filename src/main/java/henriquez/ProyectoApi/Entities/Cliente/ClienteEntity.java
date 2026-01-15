package henriquez.ProyectoApi.Entities.Cliente;

import henriquez.ProyectoApi.Entities.Cuentas.CuentasEntity;
import henriquez.ProyectoApi.Entities.Rol.RolEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Table(name = "tb_cliente")
public class ClienteEntity {


    @Id
    @Column(name = "id_cliente")
    private String idCliente;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "dui")
    private String dui;

    @Column(name = "nit")
    private String nit;

    @Column(name = "telefono")
    private String telefono;

    @Column (name = "telefono_fijo")
    private String telefonoFijo;

    @Column(name = "correo_electronico")
    private String correoElectronico;

    @Column(name = "contrasena")
    private String contrasena;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "departamento")
    private String departamento;

    @Column(name = "tipo_persona")
    private String tipoPersona;

    @Column(name = "razon_denominacion_social")
    private String razonDenominacionSocial;

    @Column(name = "nombre_comercial")
    private String nombreComercial;

    @Column(name = "limite_credito")
    private BigDecimal limiteCredito;

    @Column(name = "foto")
    private String foto;

    //Referenciando a que sera una relación de Muchos a uno (Muchos cliente solo pueden tener un Rol)
    @ManyToOne
    @JoinColumn(name = "id_rol",referencedColumnName = "id_rol")
    private RolEntity idRol;

    @OneToMany (mappedBy = "cliente", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CuentasEntity> cuenta = new ArrayList<>();
}
