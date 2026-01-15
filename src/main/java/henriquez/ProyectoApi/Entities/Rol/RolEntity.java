package henriquez.ProyectoApi.Entities.Rol;

import henriquez.ProyectoApi.Entities.Cliente.ClienteEntity;
import henriquez.ProyectoApi.Entities.Empleado.EmpleadoEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "tb_rol")
public class RolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    @Column(name = "nombre_rol")
    private String nombreRol;


    //Referenciando que es una relación de Uno a Muchos con cliente(Un rol puede estar en muchos clientes)
    @OneToMany(mappedBy = "idRol",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<ClienteEntity> cliente = new ArrayList<>();

    @OneToMany (mappedBy = "idRol", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<EmpleadoEntity>empleado = new ArrayList<>();
}
