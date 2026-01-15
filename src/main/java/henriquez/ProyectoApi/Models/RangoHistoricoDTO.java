package henriquez.ProyectoApi.Models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class RangoHistoricoDTO {
    private Integer anioInicio;
    private Integer anioFin;
}