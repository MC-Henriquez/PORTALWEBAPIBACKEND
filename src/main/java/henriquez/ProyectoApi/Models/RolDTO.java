package henriquez.ProyectoApi.Models;


import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter  @ToString @EqualsAndHashCode
public class RolDTO {


    private Long rolId;

    @NotBlank(message = "El nombre rol es obligatorio")
    private String nombreRol;
}
