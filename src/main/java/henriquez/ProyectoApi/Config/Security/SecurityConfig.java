package henriquez.ProyectoApi.Config.Security;


import henriquez.ProyectoApi.Utils.JWT.JwtCookieAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtCookieAuthFilter jwtCookieAuthFilter; // Variable statica
    private final CorsConfigurationSource corsConfigurationSource; // Inyecta CorsConfigurationSource

    // Complemento de las variables de arriba
    public SecurityConfig(JwtCookieAuthFilter jwtCookieAuthFilter,
                          CorsConfigurationSource corsConfigurationSource) {
        this.jwtCookieAuthFilter = jwtCookieAuthFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    // Configuración de seguridad HTTP
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource)) // Con esto se esta configurando los CORS
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() //  Permite los  preflight requests
                        .requestMatchers(HttpMethod.POST,"/api/authCliente/login").permitAll() // Esto es para que a este endpoint acceda cualquiera
                        .requestMatchers(HttpMethod.POST,"/api/authEmpleado/login").permitAll() // Esto es para que a este endpoint acceda cualquiera
                        .requestMatchers(HttpMethod.POST, "/api/authCliente/logout").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/authEmpleado/logout").authenticated()
                        .requestMatchers("/api/authEmpleado/me").authenticated()   // Este endpoint es para ver la info del usuario logeado
                        .requestMatchers("/api/authCliente/me").authenticated()   // Este endpoint es para ver la info del usuario logeado


                        //Endpoints POST,GET,PUT para Clientes
                        .requestMatchers(HttpMethod.GET, "/api/cliente/mostrarCliente") .hasAnyAuthority(
                                "ROLE_Administrador","ROLE_Gerente")

                        .requestMatchers(HttpMethod.POST, "/api/cliente/agregarCliente") .hasAnyAuthority(
                                "ROLE_Administrador","ROLE_Gerente")

                        .requestMatchers(HttpMethod.PUT, "/api/cliente/actualizarCliente/{id}") .hasAnyAuthority(
                                "ROLE_Administrador","ROLE_Gerente")


                        // Endpoints POST,PATCH,GET para Empleados
                        .requestMatchers(HttpMethod.GET, "/api/empleado/MostrarEmpleado") .hasAnyAuthority(
                                "ROLE_Administrador","ROLE_Gerente")

                        .requestMatchers(HttpMethod.POST, "/api/empleado/CrearEmpleado") .hasAnyAuthority(
                                "ROLE_Administrador","ROLE_Gerente")

                        .requestMatchers(HttpMethod.PATCH, "/api/empleado/actualizarEmpleado/{id}") .hasAnyAuthority(
                                "ROLE_Administrador","ROLE_Gerente")

                        .requestMatchers(HttpMethod.GET, "/api/empleado/mostrarEmpleadoPorId/{id}") .hasAnyAuthority(
                                "ROLE_Administrador","ROLE_Gerente")

                        // Endpoints get del rol
                        .requestMatchers(HttpMethod.GET, "/api/rol/mostrarRoles") .hasAnyAuthority(
                                "ROLE_Administrador","ROLE_Gerente")

                        //Endpoints cloudinary

                        .requestMatchers(HttpMethod.POST, "/api/imagen/subirImagen") .hasAnyAuthority(
                                "ROLE_Administrador","ROLE_Gerente")

                        .requestMatchers(HttpMethod.POST, "/api/imagen/SubirImagenCarpeta") .hasAnyAuthority(
                                "ROLE_Administrador","ROLE_Gerente")


                        // Endpoints cuentas por cobrar
                        .requestMatchers(HttpMethod.GET, "/api/cuentas/MostrarCuenta") .hasAnyAuthority(
                                "ROLE_Administrador","ROLE_Gerente")

                        .requestMatchers(HttpMethod.GET, "/api/cuentas/CuentasPorClienteId/{idCliente}") .hasAnyAuthority(
                                "ROLE_Administrador","ROLE_Gerente")


                        .requestMatchers(HttpMethod.GET, "/api/cliente/{idCliente}/clienteInformacion") .hasAnyAuthority(
                                "ROLE_Administrador","ROLE_Gerente","ROLE_Cliente")

                        .requestMatchers(HttpMethod.GET, "/api/cuentas/estado-cuenta/{idCliente}") .hasAnyAuthority(
                                "ROLE_Administrador","ROLE_Gerente","ROLE_Cliente")

                        .requestMatchers(HttpMethod.GET, "/api/cuentas/totales/{idCliente}") .hasAnyAuthority(
                                "ROLE_Administrador","ROLE_Gerente","ROLE_Cliente")

                        .requestMatchers(HttpMethod.GET, "/api/cuentas/rango-historico") .hasAnyAuthority(
                                "ROLE_Administrador","ROLE_Gerente","ROLE_Cliente")

                        .requestMatchers(HttpMethod.GET, "/api/cuentas/detallesDeFactura/{idCliente}") .hasAnyAuthority(
                                "ROLE_Administrador","ROLE_Gerente","ROLE_Cliente")
















                )
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtCookieAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    // Exponer el AuthenticationManager como bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }



}
