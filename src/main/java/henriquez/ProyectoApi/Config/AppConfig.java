package henriquez.ProyectoApi.Config;

import henriquez.ProyectoApi.Utils.JWT.JwtCookieAuthFilter;
import henriquez.ProyectoApi.Utils.JWT.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public JwtCookieAuthFilter jwtCookieAuthFilter(JwtUtils jwtUtils){ return new JwtCookieAuthFilter(jwtUtils);}
    // Registra un bean llamado jwtCookieAuthFilter de tipo JwtCookieAuthFilter.
    // Esto es una clase de configuraciòn
}
