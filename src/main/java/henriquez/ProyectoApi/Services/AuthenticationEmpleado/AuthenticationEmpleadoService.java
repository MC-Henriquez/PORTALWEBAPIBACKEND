package henriquez.ProyectoApi.Services.AuthenticationEmpleado;

import henriquez.ProyectoApi.Config.Argon2.Argon2PasswordEncoder;
import henriquez.ProyectoApi.Entities.Empleado.EmpleadoEntity;
import henriquez.ProyectoApi.Repositories.Empleado.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationEmpleadoService {
    @Autowired
    private EmpleadoRepository repo;  // Sirve para inyectar el ClienteRepository y poder acceder a sus metodos


    // Se crea este metodo para hacer el proceso de inicio de sesión
    public Boolean Login(String correoElectronico , String contrasena){
        Argon2PasswordEncoder objHash = new Argon2PasswordEncoder(); // Se accede a los metodos de la clase Argon2
        Optional<EmpleadoEntity> list =repo.findByCorreoElectronico(correoElectronico).stream().findFirst(); // Accede al metodo de buscar por correo Electronico
        // Se verifica que la list contenga datos
        if (list.isPresent() ){
            EmpleadoEntity user =list.get(); // Obtiene los datos de la list
            String nameRole =user.getIdRol().getNombreRol(); // Aca se obtiene el nombre del Rol del empleado
            System.out.println("Cliente encontrado ID: " + user.getIdEmpleado() +
                    ", correo electronico: " + user.getCorreoElectronico() +
                    ", rol: " + nameRole +
                    " Cliente ID: " + user.getIdEmpleado()
            ); // Se imprimira los datos de inicio de sesión

            String passwordHashBD =user.getContrasena(); // Aca se obtiene la contraseña del empleado que registro en la base de datos
            // Aca se compara la contraseña que existe en la base de datos con la contraseña ingresada en el frontEnd
            boolean verify =objHash.verifyPassword(passwordHashBD,contrasena);
            return verify;
        }
        return false; // Retorna false si la list no contiene los datos
    }

    public Optional<EmpleadoEntity>getUser(String correoElectronico){
        // Buscar el cliente en la base de datos
        if (correoElectronico == null || correoElectronico.isBlank()) return Optional.empty(); // Se valida que la consulta no sea null o datos en blanco
        return repo.findByCorreoElectronico(correoElectronico.trim()); // Luego se retorna el metodo
    }
}
