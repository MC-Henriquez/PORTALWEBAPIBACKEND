package henriquez.ProyectoApi.Controllers.Empleado;


import henriquez.ProyectoApi.Models.EmpleadoDTO;
import henriquez.ProyectoApi.Services.Empleado.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleado")
@CrossOrigin

public class EmpleadoController {

    @Autowired
    private EmpleadoService service;

    // --- 1. LEER (READ) ---
    @GetMapping("/MostrarEmpleado")
    public List<EmpleadoDTO> getallEmpleados() {
        return service.MostrarEmpleado();
    }

    // --- 2. CREAR (CREATE) ---
    @PostMapping("/CrearEmpleado")
    public ResponseEntity<EmpleadoDTO> insertarEmpleado(@RequestBody EmpleadoDTO empleadoDTO) {
        EmpleadoDTO nuevoEmpleado = service.InsertarEmpleado(empleadoDTO);
        return new ResponseEntity<>(nuevoEmpleado, HttpStatus.CREATED);
    }

//    // --- 3. ACTUALIZAR (UPDATE) ---
//    // PUT /api/empleado/{id}
//    @PutMapping("ActualizarEmpleado/{id}")
//    public ResponseEntity<EmpleadoDTO> actualizarEmpleado(@PathVariable Long id, @RequestBody EmpleadoDTO empleadoDTO) {
//        if (empleadoDTO.getIdEmpleado() == null || !empleadoDTO.getIdEmpleado().equals(id)) {
//            empleadoDTO.setIdEmpleado(id);
//        }
//        EmpleadoDTO empleadoActualizado = service.ActualizarEmpleado(empleadoDTO);
//        return ResponseEntity.ok(empleadoActualizado);
//    }

    // --- 3. ACTUALIZAR (UPDATE) ---
// PUT /api/empleado/{id}
    @PatchMapping("/actualizarEmpleado/{id}")
    public ResponseEntity<EmpleadoDTO> actualizarEmpleado(@PathVariable Long id, @RequestBody EmpleadoDTO empleadoDTO) {
        if (empleadoDTO.getIdEmpleado() == null || !empleadoDTO.getIdEmpleado().equals(id)) {
            empleadoDTO.setIdEmpleado(id);
        }
        EmpleadoDTO empleadoActualizado = service.ActualizarEmpleadoPatch(empleadoDTO);
        return ResponseEntity.ok(empleadoActualizado);
    }

    // --- 5. OBTENER POR ID (GET) ---
    @GetMapping("/mostrarEmpleadoPorId/{id}") // La ruta debe ser simplemente /api/empleado/{id}
    public ResponseEntity<EmpleadoDTO> getEmpleadoById(@PathVariable Long id) {
        EmpleadoDTO empleado = service.BuscarPorId(id); // Asumiendo que tienes un método 'BuscarPorId'
        if (empleado != null) {
            return ResponseEntity.ok(empleado); // Retorna 200 OK con los datos
        } else {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found
        }
    }

    // --- 4. ELIMINAR (DELETE) ---
    @DeleteMapping("EliminarEmpleado/{id}")
    public ResponseEntity<Void> eliminarEmpleado(@PathVariable Long id) {
        service.EliminarEmpleado(id);
        return ResponseEntity.noContent().build();
    }
}