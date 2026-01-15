package henriquez.ProyectoApi.Controllers.Rol;

import henriquez.ProyectoApi.Models.RolDTO;
import henriquez.ProyectoApi.Services.Rol.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rol")
@CrossOrigin

public class RolController {

    @Autowired
    private RolService service;

    /**
     * Este metodo sirve para mostrar todos los roles que existen
     */
    @GetMapping("/mostrarRoles")
    public List<RolDTO> getAllRol() {
        return service.getAllRol();
    }
}
