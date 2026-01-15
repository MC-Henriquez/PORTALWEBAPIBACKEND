package henriquez.ProyectoApi.Services.Rol;

import henriquez.ProyectoApi.Entities.Rol.RolEntity;
import henriquez.ProyectoApi.Models.RolDTO;
import henriquez.ProyectoApi.Repositories.Rol.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolService {

    @Autowired
    private RolRepository repositoryRol;


    /**
     * Este metodo sirve para obtener los roles que existen
     */
    public List<RolDTO>getAllRol(){
        List<RolEntity>rol = repositoryRol.findAll();
        return  rol.stream().map(this::convertToDTORol).collect(Collectors.toList());
    }

    /**
     * Este metodo es para pasar los datos de tipo Entity que vienen de la BD a datos DTO para mostrar en FrontEnd
     */
    private RolDTO convertToDTORol(RolEntity rolEntity){
        RolDTO rolDTO = new RolDTO();
        rolDTO.setRolId(rolEntity.getIdRol());
        rolDTO.setNombreRol(rolEntity.getNombreRol());
        return rolDTO;
    }

}
