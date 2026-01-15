package henriquez.ProyectoApi.Services.AuthenticationCliente;

import henriquez.ProyectoApi.Config.Argon2.Argon2PasswordEncoder;
import henriquez.ProyectoApi.Entities.Cliente.ClienteEntity;
import henriquez.ProyectoApi.Repositories.Cliente.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationClienteService {

    @Autowired
    private ClienteRepository repo; // Sirve para inyectar el ClienteRepository y poder acceder a sus metodos

    // Se crea este metodo para hacer el proceso de inicio de sesión
    public Boolean Login(String correoElectronico , String contrasena){
        Argon2PasswordEncoder objHash = new Argon2PasswordEncoder(); // Se accede a los metodos de la clase Argon2
        Optional<ClienteEntity> list =repo.findByCorreoElectronico(correoElectronico).stream().findFirst(); // Accede al metodo de buscar por correo Electronico
        // Se verifica que la list contenga datos
        if (list.isPresent() ){
            ClienteEntity user =list.get(); // Obtiene los datos de la list
            String nameRole =user.getIdRol().getNombreRol(); // Aca se obtiene el nombre del Rol del cliente
            System.out.println("Cliente encontrado ID: " + user.getIdCliente() +
                    ", correo electronico: " + user.getCorreoElectronico() +
                    ", rol: " + nameRole +
                    " Cliente ID: " + user.getIdCliente()
            ); // Se imprimira los datos de inicio de sesión

            String passwordHashBD =user.getContrasena(); // Aca se obtiene la contraseña del cliente que registro en la base de datos
            // Aca se compara la contraseña que existe en la base de datos con la contraseña ingresada en el frontEnd
            boolean verify =objHash.verifyPassword(passwordHashBD,contrasena);
            return verify;
        }
        return false; // Retorna false si la list no contiene los datos
    }

    public Optional<ClienteEntity>getUser(String correoElectronico){
        // Buscar el cliente en la base de datos
        if (correoElectronico == null || correoElectronico.isBlank()) return Optional.empty(); // Se valida que la consulta no sea null o datos en blanco
        return repo.findByCorreoElectronico(correoElectronico.trim()); // Luego se retorna el metodo
    }
}
