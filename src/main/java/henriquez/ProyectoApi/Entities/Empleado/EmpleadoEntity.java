package henriquez.ProyectoApi.Entities.Empleado;


import henriquez.ProyectoApi.Entities.Rol.RolEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Table(name = "tb_empleado")
public class EmpleadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado")
    private Long IdEmpleado;

    @Column(name = "nombre")
    private String EmpleadoNombre;

    @Column(name = "apellido")
    private String EmpleadoApellido;

    @Column(name = "fecha_nacimiento")
    private Date NacimientoEmpleado;

    @Column(name = "telefono")
    private String EmpleadoTelefono;

    @Column(name = "dui")
    private String EmpleadoDui;

    @Column(name = "correo_electronico")
    private String correoElectronico;

    @Column(name = "contrasena")
    private String contrasena;

    @Column(name = "direccion")
    private String EmpleadoDireccion;

    @Column(name = "foto")
    private String EmpleadoFoto;

    @Column(name = "estado")
    private Long estado;

    @ManyToOne
    @JoinColumn(name = "id_rol",referencedColumnName = "id_rol")
    private RolEntity idRol;
}
