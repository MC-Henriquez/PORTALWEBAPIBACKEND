package henriquez.ProyectoApi.Services.Empleado;

import henriquez.ProyectoApi.Config.Argon2.Argon2PasswordEncoder;
import henriquez.ProyectoApi.Entities.Empleado.EmpleadoEntity;
import henriquez.ProyectoApi.Entities.Rol.RolEntity;
import henriquez.ProyectoApi.Exceptions.Rol.EmpleadoExceptions;
import henriquez.ProyectoApi.Models.EmpleadoDTO;
import henriquez.ProyectoApi.Repositories.Empleado.EmpleadoRepository;
import henriquez.ProyectoApi.Repositories.Rol.RolRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private Argon2PasswordEncoder passwordEncoder;

    @Autowired
    private RolRepository rolRepository;

    public List<EmpleadoDTO>MostrarEmpleado(){
        List<EmpleadoEntity>EmpleadosEntities = empleadoRepository.findAll();
        return EmpleadosEntities.stream().map(this::ConvertirADTOEmpleado).collect(Collectors.toList());
    }

    private EmpleadoDTO ConvertirADTOEmpleado(EmpleadoEntity empleadoEntity){
        EmpleadoDTO empleadoDTO = new EmpleadoDTO();

        empleadoDTO.setIdEmpleado(empleadoEntity.getIdEmpleado());
        empleadoDTO.setEmpleadoNombre(empleadoEntity.getEmpleadoNombre());
        empleadoDTO.setEmpleadoApellido(empleadoEntity.getEmpleadoApellido());
        empleadoDTO.setNacimientoEmpleado(empleadoEntity.getNacimientoEmpleado());
        empleadoDTO.setEmpleadoTelefono(empleadoEntity.getEmpleadoTelefono());
        empleadoDTO.setEmpleadoDui(empleadoEntity.getEmpleadoDui());
        empleadoDTO.setCorreoElectronico(empleadoEntity.getCorreoElectronico());
        empleadoDTO.setEmpleadoDireccion(empleadoEntity.getEmpleadoDireccion());
        empleadoDTO.setContrasena(empleadoEntity.getContrasena());
        empleadoDTO.setEmpleadoFoto(empleadoEntity.getEmpleadoFoto());
        empleadoDTO.setEstado(empleadoEntity.getEstado());

        if (empleadoEntity.getIdRol()!= null){
            empleadoDTO.setIdRol(empleadoEntity.getIdRol().getIdRol());
            empleadoDTO.setNombreRol(empleadoEntity.getIdRol().getNombreRol());
        } else {
            empleadoDTO.setIdRol(null);
            empleadoDTO.setNombreRol("ID no asignado");
        }
        return empleadoDTO;
    }

    // Metodo para ingresar los empleados (POST)
    public EmpleadoDTO InsertarEmpleado(EmpleadoDTO empleadoDTO){
        // Primero verificar que no haya un campo sin ingresar
        validarEmpleadoDTO(empleadoDTO);
        try {
            // Usa passwordEncoder inyectado
            String encodedPass = passwordEncoder.HashPassword(empleadoDTO.getContrasena());
            empleadoDTO.setContrasena(encodedPass);

            // 3. Persistencia
            EmpleadoEntity empleado = convertToEntityEmpleado(empleadoDTO);
            EmpleadoEntity empleadoSave = empleadoRepository.save(empleado);

            // 4. Retorno
            return ConvertirADTOEmpleado(empleadoSave);

        }catch (Exception e){
            // Esto solo debería atrapar errores de persistencia (BD) o Argon2
            log.error("Error al registrar empleado: " + e.getMessage());
            throw new EmpleadoExceptions("Error al registrar empleado: " + e.getMessage());
        }

    }

    //Metodo para actualizar

    public EmpleadoDTO ActualizarEmpleado(EmpleadoDTO empleadoDTO) {
        // 1. Validar los datos de entrada (los mismos checks que en INSERT)
        validarEmpleadoDTO(empleadoDTO);

        // *Validación de ID*: Asegurar que el DTO tiene un ID para buscar.
        if (empleadoDTO.getIdEmpleado() == null) {
            throw new IllegalArgumentException("El ID del Empleado es obligatorio para la actualización.");
        }

        try {
            // 2. Buscar el empleado existente por ID
            EmpleadoEntity empleadoExistente = empleadoRepository.findById(empleadoDTO.getIdEmpleado())
                    .orElseThrow(() -> new EmpleadoExceptions("Empleado con ID " + empleadoDTO.getIdEmpleado() + " no encontrado para actualizar."));

            // 3. Aplicar los cambios al objeto existente (Mapeo DTO -> Entity)
            //    Nota: Se utiliza un método de mapeo de actualización, no el de creación.
            actualizarEntityConDTO(empleadoExistente, empleadoDTO);

            // 4. Si la contraseña se cambió, hashearla (Opcional, pero recomendado)
            //    Si el DTO trae una contraseña nueva, la hasheamos.
            //    Si el DTO trae la misma contraseña hasheada, la lógica a continuación la ignorará (siempre que el mapeo la ignore).
            if (empleadoDTO.getContrasena() != null && !empleadoDTO.getContrasena().isBlank()) {
                String encodedPass = passwordEncoder.HashPassword(empleadoDTO.getContrasena());
                empleadoExistente.setContrasena(encodedPass);
            }

            // 5. Guardar la Entity actualizada. Spring JPA sabe que debe hacer un UPDATE
            //    porque el objeto 'empleadoExistente' tiene un ID asignado.
            EmpleadoEntity empleadoActualizado = empleadoRepository.save(empleadoExistente);

            // 6. Retornar la Entity convertida de nuevo a DTO (sin la contraseña)
            return ConvertirADTOEmpleado(empleadoActualizado);

        } catch (Exception e) {
            log.error("Error al actualizar empleado: " + e.getMessage());
            // Propagar nuestra excepción personalizada
            throw new EmpleadoExceptions("Error al actualizar el empleado: " + e.getMessage());
        }
    }

    private void actualizarEntityConDTO(EmpleadoEntity target, EmpleadoDTO source) {
        target.setEmpleadoNombre(source.getEmpleadoNombre());
        target.setEmpleadoApellido(source.getEmpleadoApellido());
        target.setNacimientoEmpleado(source.getNacimientoEmpleado());
        target.setEmpleadoTelefono(source.getEmpleadoTelefono());
        target.setEmpleadoDui(source.getEmpleadoDui());
        target.setCorreoElectronico(source.getCorreoElectronico());
        target.setEmpleadoDireccion(source.getEmpleadoDireccion());
        target.setEmpleadoFoto(source.getEmpleadoFoto());
        if (target.getIdRol()!= null){
            RolEntity rol = rolRepository.findById(source.getIdRol()).orElseThrow( () -> new IllegalArgumentException("ID no encontrado"));
            target.setIdRol(rol);
        }
    }

    public EmpleadoDTO BuscarPorId(Long id) {

        // 1. Usa el metodo findById proporcionado por JpaRepository/CrudRepository
        Optional<EmpleadoEntity> empleadoEntityOptional = empleadoRepository.findById(id);

        // 2. Verifica si el empleado existe
        if (empleadoEntityOptional.isPresent()) {
            // Si existe, lo obtiene
            EmpleadoEntity empleadoEntity = empleadoEntityOptional.get();

            // 3. Convierte la Entity a DTO y retorna
            // NOTA: Debes reemplazar 'new EmpleadoDTO(empleadoEntity)' con tu lógica de mapeo real.
            // Si usas MapStruct o un constructor, ajusta esta línea.
            return ConvertirADTOEmpleado(empleadoEntity);

        } else {
            // Si no existe, retorna null. Esto será manejado por el controlador para dar 404.
            return null;
        }
    }

    // Metodo para Actualizar Empleado (con PATCH)
    @Transactional
    public EmpleadoDTO ActualizarEmpleadoPatch(EmpleadoDTO empleadoDTO) {
        // 1. Validación de ID: Asegurar que el DTO tiene un ID para buscar.
        if (empleadoDTO.getIdEmpleado() == null) {
            throw new IllegalArgumentException("El ID del Empleado es obligatorio para la actualización.");
        }

        try {
            // 2. Buscar el empleado existente por ID
            EmpleadoEntity empleadoExistente = empleadoRepository.findById(empleadoDTO.getIdEmpleado())
                    .orElseThrow(() -> new EmpleadoExceptions("Empleado con ID " + empleadoDTO.getIdEmpleado() + " no encontrado para actualizar."));

            // 3. APLICAR CAMBIOS PARCIALES (Lógica de PATCH)
            // Solo actualizamos los campos si NO son nulos o vacíos en el DTO de entrada.

            // Nombres
            if (empleadoDTO.getEmpleadoNombre() != null && !empleadoDTO.getEmpleadoNombre().isBlank()) {
                empleadoExistente.setEmpleadoNombre(empleadoDTO.getEmpleadoNombre());
            }

            // Apellidos
            if (empleadoDTO.getEmpleadoApellido() != null && !empleadoDTO.getEmpleadoApellido().isBlank()) {
                empleadoExistente.setEmpleadoApellido(empleadoDTO.getEmpleadoApellido());
            }

            // Correo
            if (empleadoDTO.getCorreoElectronico() != null && !empleadoDTO.getCorreoElectronico().isBlank()) {
                empleadoExistente.setCorreoElectronico(empleadoDTO.getCorreoElectronico());
            }

            // Teléfono (Asumiendo que es un String)
            if (empleadoDTO.getEmpleadoTelefono() != null && !empleadoDTO.getEmpleadoTelefono().isBlank()) {
                empleadoExistente.setEmpleadoTelefono(empleadoDTO.getEmpleadoTelefono());
            }

            // Dirección
            if (empleadoDTO.getEmpleadoDireccion() != null && !empleadoDTO.getEmpleadoDireccion().isBlank()) {
                empleadoExistente.setEmpleadoDireccion(empleadoDTO.getEmpleadoDireccion());
            }

            // Fecha de Nacimiento (Asumiendo que es un objeto que puede ser nulo)
            if (empleadoDTO.getNacimientoEmpleado() != null) {
                empleadoExistente.setNacimientoEmpleado(empleadoDTO.getNacimientoEmpleado());
            }

            // DUI
            if (empleadoDTO.getEmpleadoDui() != null && !empleadoDTO.getEmpleadoDui().isBlank()) {
                empleadoExistente.setEmpleadoDui(empleadoDTO.getEmpleadoDui());
            }

            // Estado (Asumiendo que es un valor booleano o numérico para Activo/Inactivo)
            // Usamos Integer para manejar 0/1 o nulo
            if (empleadoDTO.getEstado() != null) {
                empleadoExistente.setEstado(empleadoDTO.getEstado());
            }


            // 4. Contraseña: Solo si se proporciona una nueva (no se debe enviar en actualizaciones parciales típicas)
            if (empleadoDTO.getContrasena() != null && !empleadoDTO.getContrasena().isBlank()) {
                String encodedPass = passwordEncoder.HashPassword(empleadoDTO.getContrasena());
                empleadoExistente.setContrasena(encodedPass);
            }

            if(empleadoDTO.getEmpleadoFoto() != null && !empleadoDTO.getEmpleadoFoto().isBlank()){
                empleadoExistente.setEmpleadoFoto(empleadoDTO.getEmpleadoFoto());
            }

//            validarEmpleadoEntity(empleadoExistente);

            // 6. Guardar la Entity actualizada.
            EmpleadoEntity empleadoActualizado = empleadoRepository.save(empleadoExistente);

            // 7. Retornar la Entity convertida de nuevo a DTO
            return ConvertirADTOEmpleado(empleadoActualizado);

        } catch (EmpleadoExceptions e) {
            // Propagar EmpleadoExceptions directamente (e.g., el 'no encontrado' del paso 2)
            throw e;
        }
        catch (Exception e) {
            log.error("Error al actualizar empleado: " + e.getMessage());
            // Propagar nuestra excepción personalizada para errores genéricos
            throw new EmpleadoExceptions("Error interno al actualizar el empleado: " + e.getMessage());
        }
    }

    // Metodo de validación para el objeto final de la BASE DE DATOS (Entity)
    private void validarEmpleadoEntity(EmpleadoEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("El objeto EmpleadoEntity no puede ser nulo.");
        }

        // Nota: La validación de la contraseña aquí debe ser cuidadosa.
        // Si la contraseña es hasheada, solo se valida que exista, no su contenido.

        if (entity.getEmpleadoNombre() == null || entity.getEmpleadoNombre().isBlank()) {
            throw new IllegalArgumentException("El Nombre del Empleado es obligatorio.");
        }
        if (entity.getEmpleadoApellido() == null || entity.getEmpleadoApellido().isBlank()) {
            throw new IllegalArgumentException("El Apellido del Empleado es obligatorio.");
        }
        if (entity.getNacimientoEmpleado() == null) {
            throw new IllegalArgumentException("La Fecha de Nacimiento es obligatoria.");
        }
        if (entity.getEmpleadoTelefono() == null || entity.getEmpleadoTelefono().isBlank()) {
            throw new IllegalArgumentException("El Teléfono del Empleado es obligatorio.");
        }
        if (entity.getEmpleadoDui() == null || entity.getEmpleadoDui().isBlank()) {
            throw new IllegalArgumentException("El DUI del Empleado es obligatorio.");
        }
        if (entity.getCorreoElectronico() == null || entity.getCorreoElectronico().isBlank()) {
            throw new IllegalArgumentException("El Correo del Empleado es obligatorio.");
        }
        if (entity.getContrasena() == null || entity.getContrasena().isBlank()) {
            throw new IllegalArgumentException("La Contraseña es obligatoria.");
        }
        if (entity.getEmpleadoDireccion() == null || entity.getEmpleadoDireccion().isBlank()) {
            throw new IllegalArgumentException("La Dirección del Empleado es obligatoria.");
        }
        if (entity.getEmpleadoFoto() == null || entity.getEmpleadoFoto().isBlank()) {
            throw new IllegalArgumentException("La Foto del Empleado es obligatoria.");
        }
    }

    //Metodo para eliminar

    public void EliminarEmpleado(Long idEmpleado) {
        // 1. Verificar si el empleado existe
        if (!empleadoRepository.existsById(idEmpleado)) {
            // 2. Si no existe, lanzar una excepción controlada
            log.error("Empleado no encontrado con ID: " + idEmpleado);
            throw new EmpleadoExceptions("No se puede eliminar. Empleado con ID " + idEmpleado + " no encontrado.");
        }
        try {
            // 3. Si existe, proceder con la eliminación
            empleadoRepository.deleteById(idEmpleado);
            log.info("Empleado con ID " + idEmpleado + " eliminado exitosamente.");

        } catch (Exception e) {
            // 4. Capturar cualquier error inesperado durante la operación (ej. restricciones de clave foránea)
            log.error("Error al eliminar empleado con ID " + idEmpleado + ": " + e.getMessage());
            throw new EmpleadoExceptions("Error al eliminar el empleado: " + e.getMessage());
        }
    }

    private void validarEmpleadoDTO(EmpleadoDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El objeto EmpleadoDTO no puede ser nulo.");
        }

        if (dto.getEmpleadoNombre() == null || dto.getEmpleadoNombre().isBlank()) {
            throw new IllegalArgumentException("El Nombre del Empleado es obligatorio.");
        }
        if (dto.getEmpleadoApellido() == null || dto.getEmpleadoApellido().isBlank()) {
            throw new IllegalArgumentException("El Apellido del Empleado es obligatorio.");
        }
        // Asumiendo que todos estos campos son obligatorios:
        if (dto.getNacimientoEmpleado() == null) {
            throw new IllegalArgumentException("La Fecha de Nacimiento es obligatoria.");
        }
        if (dto.getEmpleadoTelefono() == null || dto.getEmpleadoTelefono().isBlank()) {
            throw new IllegalArgumentException("El Teléfono del Empleado es obligatorio.");
        }
        if (dto.getEmpleadoDui() == null || dto.getEmpleadoDui().isBlank()) {
            throw new IllegalArgumentException("El DUI del Empleado es obligatorio.");
        }
        if (dto.getCorreoElectronico() == null || dto.getCorreoElectronico().isBlank()) {
            throw new IllegalArgumentException("El Correo del Empleado es obligatorio.");
        }
        if (dto.getContrasena() == null || dto.getContrasena().isBlank()) {
            throw new IllegalArgumentException("La Contraseña es obligatoria.");
        }
        if (dto.getEmpleadoDireccion() == null || dto.getEmpleadoDireccion().isBlank()) {
            throw new IllegalArgumentException("La Dirección del Empleado es obligatoria.");
        }
        // Nota: Si la Foto es opcional, elimina su validación de este bloque.
        if (dto.getEmpleadoFoto() == null || dto.getEmpleadoFoto().isBlank()) {
            throw new IllegalArgumentException("La Foto del Empleado es obligatoria.");
        }
    }

    // Metodo para pasar los datos DTO del frontEnd a datos Entity del BackEnd
    private EmpleadoEntity convertToEntityEmpleado(EmpleadoDTO objEmpleadoDTO){
        EmpleadoEntity objEmpleadoEntity = new EmpleadoEntity();
        objEmpleadoEntity.setEmpleadoNombre(objEmpleadoDTO.getEmpleadoNombre());
        objEmpleadoEntity.setEmpleadoApellido(objEmpleadoDTO.getEmpleadoApellido());
        objEmpleadoEntity.setNacimientoEmpleado(objEmpleadoDTO.getNacimientoEmpleado());
        objEmpleadoEntity.setEmpleadoTelefono(objEmpleadoDTO.getEmpleadoTelefono());
        objEmpleadoEntity.setEmpleadoDui(objEmpleadoDTO.getEmpleadoDui());
        objEmpleadoEntity.setCorreoElectronico(objEmpleadoDTO.getCorreoElectronico());
        objEmpleadoEntity.setContrasena(objEmpleadoDTO.getContrasena());
        objEmpleadoEntity.setEmpleadoDireccion(objEmpleadoDTO.getEmpleadoDireccion());
        objEmpleadoEntity.setEmpleadoFoto(objEmpleadoDTO.getEmpleadoFoto());
        objEmpleadoEntity.setEstado(objEmpleadoDTO.getEstado());
// Obtener llaves FK
        if (objEmpleadoDTO.getIdRol()!= null){
            RolEntity rol = rolRepository.findById(objEmpleadoDTO.getIdRol()).orElseThrow( () -> new IllegalArgumentException("ID no encontrado"));
            objEmpleadoEntity.setIdRol(rol);
        }
        return objEmpleadoEntity;
    }
}
