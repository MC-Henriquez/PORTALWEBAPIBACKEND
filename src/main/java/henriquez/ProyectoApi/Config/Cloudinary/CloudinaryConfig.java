package henriquez.ProyectoApi.Config.Cloudinary;


import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    // 1. Inyectamos directamente los valores del entorno usando @Value.
    //    Si la variable de entorno está nombrada 'CLOUDINARY_CLOUD_NAME',
    //    Spring la mapea automáticamente a 'cloudinary.cloud-name'.

    // Si tus variables de entorno tienen otro nombre, ajusta el valor dentro de ${...}

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();


        // Obtiene las credenciales desde las variables de entorno y las guarda en el Map
        config.put("cloud_name", System.getenv("CLOUDINARY_CLOUD_NAME"));  // Nombre de la nube en Cloudinary
        config.put("api_key", System.getenv("CLOUDINARY_API_KEY"));        // API Key para autenticación
        config.put("api_secret", System.getenv("CLOUDINARY_API_SECRET"));  // API Secret (clave secreta)


        return new Cloudinary(config);
    }
}