package henriquez.ProyectoApi.Models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter @Setter @ToString  @EqualsAndHashCode
public class EmpleadoDTO {

    private Long IdEmpleado;

    private String EmpleadoNombre;

    private String EmpleadoApellido;

    private Date NacimientoEmpleado;

    private String EmpleadoTelefono;

    private String EmpleadoDui;

    private String correoElectronico;

    private String contrasena;

    private String EmpleadoDireccion;

    private String EmpleadoFoto;

    private Long estado;


    private Long idRol;

    private String nombreRol;
}
