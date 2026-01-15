package henriquez.ProyectoApi.Services.Cliente;

import henriquez.ProyectoApi.Config.Argon2.Argon2PasswordEncoder;
import henriquez.ProyectoApi.Entities.Cliente.ClienteEntity;
import henriquez.ProyectoApi.Entities.Rol.RolEntity;
import henriquez.ProyectoApi.Exceptions.Rol.ExceptionUnregistered;
import henriquez.ProyectoApi.Models.ClienteDTO;
import henriquez.ProyectoApi.Models.ClienteInformacionDTO;
import henriquez.ProyectoApi.Repositories.Cliente.ClienteRepository;
import henriquez.ProyectoApi.Repositories.Rol.RolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteService {

    /**
     * Esto es para Inyectar y poder acceder al repository de Clientes para el manejo del CRUD
     *
     */
    @Autowired
    private ClienteRepository repositoryCliente;

    @Autowired
    private Argon2PasswordEncoder passwordEncoder;

    /**
     * Esto es para inyectar y poder acceder al repositoriy de Rol
     */
    @Autowired
    private RolRepository repositoryRol;

    /**
     * Este metodo GET es para mostrar a todos los clientes que existen en la empresa
     */

    public List<ClienteDTO>MostrarClientes(){
        List<ClienteEntity>clienteEntities = repositoryCliente.findAll();
        return clienteEntities.stream().map(this::ConvertirADTOCliente).collect(Collectors.toList());
    }

    /**
     * Este metodo POST es para agregar a los clientes que existan en la empresa
     * @param clienteDTO
     * @return
     */
    public ClienteDTO AgregarClientes(ClienteDTO clienteDTO){
        if (clienteDTO == null){
            throw new IllegalArgumentException("Ningun campo puede estar vacio");
        }
        try{
            // Esto es para acceder a la Clase Argon2 para poder ocupar el metodo HashPassword
            Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder();
            // Aca se crea la variable contrasenaEncriptada que contendra el metodo de encriptación y luego obtenedra la contraseña ingresada por el cliente
            String contrasenaEncriptada = argon2PasswordEncoder.HashPassword(clienteDTO.getContrasena());
            // Entonces luego se asignara la contraseña del cliente ya encriptada directo a la base de datos
            clienteDTO.setContrasena(contrasenaEncriptada);
            // Aca se convierte a entidad lo que el frontEnd trae
            ClienteEntity clienteEntity = ConvertirAEntityCliente(clienteDTO);
            // Aca se guarda los datos ya en Entidad en la base de datos
            ClienteEntity clienteSave = repositoryCliente.save(clienteEntity);
            // Y luego que ya se guardo en la base de datos se tienen que volver a convertir a datos DTO par mostrar en el frontEnd
            return ConvertirADTOCliente(clienteSave);
        }catch (Exception e){
            log.error("Error al registrar el cliente" + e.getMessage());
            throw  new ExceptionUnregistered("Error al registrar el cliente" + e.getMessage());
        }
    }

    /**
     * Este metodo PUT es para actualizar la información de los clientes
     */
    public ClienteDTO ActualizarClientes(String id, ClienteDTO clienteDTO){
        // Se tiene que verificar que exista el ID proporcionado a actualizar
        ClienteEntity clienteExisting = repositoryCliente.findById(id).orElseThrow(()-> new IllegalArgumentException("ID no encontrado"));

        clienteExisting.setIdCliente(clienteDTO.getClienteId());
        clienteExisting.setApellido(clienteDTO.getApellido());
        clienteExisting.setNombre(clienteDTO.getNombre());
        clienteExisting.setDui(clienteDTO.getDui());
        clienteExisting.setNit(clienteDTO.getNit());
        clienteExisting.setTelefono(clienteDTO.getTelefono());
        clienteExisting.setTelefonoFijo(clienteDTO.getTelefonoFijo());
        clienteExisting.setCorreoElectronico(clienteDTO.getCorreoElectronico());

        // 4. Contraseña: Solo si se proporciona una nueva (no se debe enviar en actualizaciones parciales típicas)
        if (clienteDTO.getContrasena() != null && !clienteDTO.getContrasena().isBlank()) {
            String encodedPass = passwordEncoder.HashPassword(clienteDTO.getContrasena());
            clienteExisting.setContrasena(encodedPass);
        }
        clienteExisting.setDireccion(clienteDTO.getDireccion());
        clienteExisting.setDepartamento(clienteDTO.getDepartamento());
        clienteExisting.setTipoPersona(clienteDTO.getTipoPersona());
        clienteExisting.setRazonDenominacionSocial(clienteDTO.getRazonDenominacionSocial());
        clienteExisting.setNombreComercial(clienteDTO.getNombreComercial());
        clienteExisting.setFoto(clienteDTO.getFoto());
        clienteExisting.setLimiteCredito(clienteDTO.getLimiteCredito());
        // Obtener el ID del rol
        // Obtener llaves FK
        if (clienteDTO.getIdRol()!= null){
            RolEntity rol = repositoryRol.findById(clienteDTO.getIdRol()).orElseThrow( () -> new IllegalArgumentException("ID no encontrado"));
            clienteExisting.setIdRol(rol);
        }
        // Si todos los campos se actualizaron correctamente entonces se tienen que guardar en la base de datos
        ClienteEntity clienteSave = repositoryCliente.save(clienteExisting);
        // Luego se tiene que retornar los datos Entidad a datos DTO
        return ConvertirADTOCliente(clienteSave);
    }

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Helper para limpiar y obtener un String del mapa, devolviendo null si está vacío o es null.
     */
    private String getCleanString(Map<String, Object> rawData, String key) {
        Object value = rawData.get(key);
        if (value instanceof String) {
            String s = ((String) value).trim();
            return s.isEmpty() ? null : s;
        }
        return null;
    }

    /**
     * Formatea el NIT de un string largo a la estructura 0000-000000-000-0.
     * Asume que el NIT tiene 14 o 15 caracteres (sin guiones).
     * @param rawNit El string del NIT sin formato (ej: "06140101901010").
     * @return El NIT formateado o null si no existe.
     */
    private String formatNit(String rawNit) {
        if (rawNit == null || rawNit.length() < 14) {
            return null;
        }
        // Asumiendo formato de 14 caracteres:
        String nit = rawNit.trim();
        if (nit.length() >= 14) {
            return nit.substring(0, 4) + "-" +
                    nit.substring(4, 10) + "-" +
                    nit.substring(10, 13) + "-" +
                    nit.substring(13);
        }
        return rawNit;
    }

    /**
     * Mapea los datos crudos del repositorio al DTO final requerido por el Front-end.
     * @param idCliente El ID del cliente a buscar.
     * @return ClienteInformacionDTO con todos los campos formateados.
     */
    public ClienteInformacionDTO obtenerInformacionClienteFormateada(String idCliente) {

        // 1. Obtener datos crudos del repositorio
        Map<String, Object> rawData = clienteRepository.findRawDataById(idCliente)
                .orElseThrow(() -> new NoSuchElementException("Cliente no encontrado con ID: " + idCliente));

        // Obtener campos base de la consulta usando la función de limpieza
        String nombre = getCleanString(rawData, "nombre");
        String apellido = getCleanString(rawData, "apellido");
        String razonSocial = getCleanString(rawData, "razonDenominacionSocial");
        String nombreComercial = getCleanString(rawData, "nombreComercial"); // <-- Nuevo campo obtenido
        String tipoPersonaRaw = getCleanString(rawData, "tipoPersona");
        String rawNit = getCleanString(rawData, "nit");
        String phone = getCleanString(rawData, "telefono"); // Usar helper para limpiar
        String address = getCleanString(rawData, "direccion"); // Usar helper para limpiar
        BigDecimal limiteCredito = (BigDecimal) rawData.get("limiteCredito");


        // FIX: Usar .longValue() para convertir de forma segura cualquier tipo Number (Integer o Long) a Long.
        String id = String.valueOf(rawData.get("idCliente"));

        // 2. Aplicar lógica de negocio y mapeo a ClienteInformacionDTO

        // --- Lógica: clientName (Prioridad: Razón Social > Nombre Comercial > Nombre Apellido) ---
        String clientName;
        if (razonSocial != null) {
            clientName = razonSocial;
        } else if (nombreComercial != null) {
            clientName = nombreComercial;
        } else if (nombre != null && apellido != null) {
            // Fallback a nombre y apellido si no hay razón social ni nombre comercial
            clientName = nombre + " " + apellido;
        } else {
            clientName = null;
        }

        // --- Lógica: attn (Siempre Nombre y Apellido si existen, sino null) ---
        String attn;
        if (nombre != null && apellido != null) {
            attn = nombre + " " + apellido;
        } else {
            attn = null;
        }

        // --- Lógica: type (Corregida con equalsIgnoreCase y mapeo de texto) ---
        String tipoPersona;
        if ("J".equalsIgnoreCase(tipoPersonaRaw)) {
            tipoPersona = "Jurídica";
        } else if ("N".equalsIgnoreCase(tipoPersonaRaw)) {
            tipoPersona = "Natural";
        } else if ("A".equalsIgnoreCase(tipoPersonaRaw)) {
        tipoPersona = "Natural";
        } else if ("B".equalsIgnoreCase(tipoPersonaRaw)) {
        tipoPersona = "Jurídica";
        } else {
            tipoPersona = null;
        }

        // Formato para sinceClient/untilClient (ID con 5 dígitos, relleno de ceros)
        //String formattedId = String.format("%05d", id);

        return ClienteInformacionDTO.builder()
                .sinceClient(id)
                .untilClient(id)
                .clientName(clientName)
                .attn(attn)
                .type(tipoPersona)
                .nit(formatNit(rawNit)) // Aplicar formato
                .phone(phone)
                .address(address)
                .limiteCredito(limiteCredito)

                .build();
    }

    /**
     * Este metodo es para obtener los datos de tipo Entidad que vienen de la base de datos y luego asignarlos
     * a datos DTO para mostrarlo en el frontEnd
     */
    private ClienteDTO ConvertirADTOCliente(ClienteEntity clienteEntity){
        ClienteDTO clienteDTO = new ClienteDTO();

        clienteDTO.setClienteId(clienteEntity.getIdCliente());
        clienteDTO.setApellido(clienteEntity.getApellido());
        clienteDTO.setNombre(clienteEntity.getNombre());
        clienteDTO.setDui(clienteEntity.getDui());
        clienteDTO.setNit(clienteEntity.getNit());
        clienteDTO.setTelefono(clienteEntity.getTelefono());
        clienteDTO.setTelefonoFijo(clienteEntity.getTelefonoFijo());
        clienteDTO.setCorreoElectronico(clienteEntity.getCorreoElectronico());
        clienteDTO.setContrasena(clienteEntity.getContrasena());
        clienteDTO.setDireccion(clienteEntity.getDireccion());
        clienteDTO.setDepartamento(clienteEntity.getDepartamento());
        clienteDTO.setTipoPersona(clienteEntity.getTipoPersona());
        clienteDTO.setRazonDenominacionSocial(clienteEntity.getRazonDenominacionSocial());
        clienteDTO.setNombreComercial(clienteEntity.getNombreComercial());
        clienteDTO.setLimiteCredito(clienteEntity.getLimiteCredito());
        clienteDTO.setFoto(clienteEntity.getFoto());

        // Obtener el idRol y nombreRol (Campos FK)
        if (clienteEntity.getIdRol()!= null){
            clienteDTO.setIdRol(clienteEntity.getIdRol().getIdRol());
            clienteDTO.setNombreRol(clienteEntity.getIdRol().getNombreRol());
        }else {
            clienteDTO.setIdRol(null);
            clienteDTO.setNombreRol("ID no asignado");
        }
        return clienteDTO;
    }

    /**
     * Este metodo es para obtener los datos de tipo DTO que vienen del frontEnd y tienen que convertirse
     * En datos Entity para poder guardarse en la base de datos
     */
    private  ClienteEntity ConvertirAEntityCliente(ClienteDTO clienteDTO){
        ClienteEntity clienteEntity = new ClienteEntity();
        clienteEntity.setIdCliente(clienteDTO.getClienteId());
        clienteEntity.setApellido(clienteDTO.getApellido());
        clienteEntity.setNombre(clienteDTO.getNombre());
        clienteEntity.setDui(clienteDTO.getDui());
        clienteEntity.setNit(clienteDTO.getNit());
        clienteEntity.setTelefono(clienteDTO.getTelefono());
        clienteEntity.setTelefonoFijo(clienteDTO.getTelefonoFijo());
        clienteEntity.setCorreoElectronico(clienteDTO.getCorreoElectronico());
        clienteEntity.setContrasena(clienteDTO.getContrasena());
        clienteEntity.setDireccion(clienteDTO.getDireccion());
        clienteEntity.setDepartamento(clienteDTO.getDepartamento());
        clienteEntity.setTipoPersona(clienteDTO.getTipoPersona());
        clienteEntity.setRazonDenominacionSocial(clienteDTO.getRazonDenominacionSocial());
        clienteEntity.setNombreComercial(clienteDTO.getNombreComercial());
        clienteEntity.setLimiteCredito(clienteDTO.getLimiteCredito());
        clienteEntity.setFoto(clienteDTO.getFoto());

        // Obtener llaves FK
        if (clienteDTO.getIdRol()!= null){
            RolEntity rol = repositoryRol.findById(clienteDTO.getIdRol()).orElseThrow( () -> new IllegalArgumentException("ID no encontrado"));
            clienteEntity.setIdRol(rol);
        }
        return clienteEntity;
    }
}
