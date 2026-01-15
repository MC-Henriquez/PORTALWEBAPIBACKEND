package henriquez.ProyectoApi.Models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter @Setter @ToString  @EqualsAndHashCode
public class ClienteDTO {

    private String clienteId;

    private String apellido;

    private String nombre;

    private String dui;

    private String nit;

    private String telefono;

    private String telefonoFijo;

    private String correoElectronico;

    private String contrasena;

    private String direccion;

    private String departamento;

    private String tipoPersona;

    private String razonDenominacionSocial;

    private String nombreComercial;

    private BigDecimal limiteCredito;


    private String foto;

    // Campos FK
    private Long idRol;
    // Campo agreagado para saber que rol es
    private String nombreRol;

    // campo extra
    private String solicitante;


}
