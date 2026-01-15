package henriquez.ProyectoApi.Controllers.Cliente;


import henriquez.ProyectoApi.Exceptions.Rol.ExceptionDuplicateData;
import henriquez.ProyectoApi.Exceptions.Rol.ExceptionUnregistered;
import henriquez.ProyectoApi.Models.ClienteDTO;
import henriquez.ProyectoApi.Models.ClienteInformacionDTO;
import henriquez.ProyectoApi.Repositories.Cliente.ClienteRepository;
import henriquez.ProyectoApi.Services.Cliente.ClienteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/cliente")
@CrossOrigin
public class ClienteController {

    /**
     *  Es para inyectar las dependencias para acceder a los metodos CRUD del service Cliente
     */

    @Autowired
    private ClienteService service;

    @Autowired
    private ClienteRepository repository;


    /**
     * Este metodo es para mostrar todos los clientes
     * @return
     */
    @GetMapping("/mostrarCliente")
    public List<ClienteDTO> getAllCliente(){return service.MostrarClientes(); }

    /**
     * Este metodo es para registrar a todos los clientes
     * @param clienteDTO
     * @param bindingResult
     * @param request
     * @return
     */
    @PostMapping("/agregarCliente")
    public ResponseEntity<Map<String,Object>> registrarCliente
    (@Valid @RequestBody ClienteDTO clienteDTO, BindingResult bindingResult, HttpServletRequest request) {

        //  Verifica si hay errores de validación antes del try
        if (bindingResult.hasErrors()) {
            Map<String, String> validationErrors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    validationErrors.put(error.getField(), error.getDefaultMessage())
            );
            // Aca se crea la respuesta HTTP con código(400 BAD REQUEST) con información del error
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "VALIDATION_ERROR",
                    "errors", validationErrors
            ));
        }

        try{
            ClienteDTO reply = service.AgregarClientes(clienteDTO);
            if (reply == null){
                // Aca se crea la respuesta HTTP con código(400 BAD REQUEST) con información del error
                return ResponseEntity.badRequest().body(Map.of("status","Registro incorrecto",
                        "errorType","VALIDATION_ERROR",
                        "message","Datos del Cliente invalidos"
                ));
            }else {
                // Si reply no null(No esta (vacío) entonces se crea la respuesta HTTP con código(201 CREATED) con información del resultado
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status","success",
                        "data",reply
                ));
            }


        } catch (Exception e) {
            // Si falla el Servidor entonces se crea la respuesta HTTP con código(500 INTERNAL SERVER ERROR) con información del fallo
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status","error",
                    "message","Error al registrar El Cliente",
                    "detail",e.getMessage()
            ));
        }
    }

    /**
     * Este metodo es para Actualizar a todos los clientes
     * @param id
     * @param clienteDTO
     * @param bindingResult
     * @return
     */
    @PutMapping("/actualizarCliente/{id}")
    public ResponseEntity<?>ActualizarCliente(@PathVariable String id, @RequestBody ClienteDTO clienteDTO,BindingResult bindingResult){
        // Verifica si el objeto Cliente no pasa las validaciones
        if (bindingResult.hasErrors() ){
            // Aca se crea un mapa para almacenar los errores
            Map<String ,String> errors  = new HashMap<>();
            // Recorre todos los errores campo por campo y los agrega al mapa
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(),error.getDefaultMessage() ) );
            // Aca se crea la respuesta HTTP con código(400 BAD REQUEST) con información del error
            return  ResponseEntity.badRequest().body(errors);
        }
        try {
            // Si no existen errores se procede a actualizar
            ClienteDTO clienteUpdate = service.ActualizarClientes(id,clienteDTO);
            //entonces se crea la respuesta HTTP con código(200 OK) con información del resultado
            return ResponseEntity.ok(clienteUpdate);
        }
        catch (ExceptionUnregistered e){
            // Aca se crea la respuesta HTTP con código(404 NOT FOUND) con información del error
            return ResponseEntity.notFound().build();

        }
        catch (ExceptionDuplicateData e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error","Datos duplicados","campo",e.getDuplicateField()
            ) );
        }
    }

    @GetMapping("/{idCliente}/clienteInformacion")
    public ResponseEntity<ClienteInformacionDTO> obtenerDatos(
            @PathVariable String idCliente) {

        try {
            ClienteInformacionDTO info = service.obtenerInformacionClienteFormateada(idCliente);
            return ResponseEntity.ok(info);
        } catch (NoSuchElementException e) {
            // Devuelve 404 si el cliente no existe
            return ResponseEntity.notFound().build();
        }
    }
}

