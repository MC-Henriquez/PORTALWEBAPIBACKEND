package henriquez.ProyectoApi.Config.Cors;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Configuración esencial para el FrontEnd
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost"); // Para desarrollo (XAMPP)
        config.addAllowedOrigin("https://localhost"); // Para Aplicación móvil
        config.addAllowedOrigin("http://localhost:8080"); // Para API LOCAL
        config.addAllowedOrigin("https://portal-web-frontend.vercel.app/"); // Sistema web alojada en Vercel
        config.addAllowedOrigin("https://portalwebhenriquez2025-feb6aad59691.herokuapp.com/"); // Donde se aloja la API HEROKU

        // Métodos  HTTP permitidos
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("PATCH");

        // Cabeceras permitidas
        config.addAllowedHeader("Origin");
        config.addAllowedHeader("Content-Type");
        config.addAllowedHeader("Accept");
        config.addAllowedHeader("Authorization");
        config.addAllowedHeader("X-Requested-With");
        config.addAllowedHeader("Access-Control-Request-Method");
        config.addAllowedHeader("Access-Control-Request-Headers");
        config.addAllowedHeader("Cookie");
        config.addAllowedHeader("Set-Cookie");

        config.setExposedHeaders(Arrays.asList(
                "Set-Cookie", "Cookie", "Authorization", "Content-Disposition"
        ));

        // Tiempo de cache para preflight requests
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    // También crea el CorsConfigurationSource para SecurityConfig
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("http://localhost:8080");
        configuration.addAllowedOrigin("http://localhost:4200");
        configuration.addAllowedOrigin("http://localhost"); // XAMPP
        configuration.addAllowedOrigin("https://localhost"); // Apliacación Móvil
        configuration.addAllowedOrigin("http://localhost:8080"); // Para API LOCAL
        configuration.addAllowedOrigin("https://*.herokuapp.com");   // Dominio
        configuration.addAllowedOrigin("https://portal-web-frontend.vercel.app/"); // Sistema web en vercel
        configuration.addAllowedOrigin("https://portalwebhenriquez2025-feb6aad59691.herokuapp.com/"); // Donde se aloja la API HEROKU


        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");

        configuration.addExposedHeader("Set-Cookie");
        configuration.addExposedHeader("Cookie");
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Content-Disposition");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }



}
