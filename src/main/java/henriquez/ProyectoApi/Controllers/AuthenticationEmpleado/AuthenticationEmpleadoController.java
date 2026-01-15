package henriquez.ProyectoApi.Controllers.AuthenticationEmpleado;


import henriquez.ProyectoApi.Entities.Empleado.EmpleadoEntity;
import henriquez.ProyectoApi.Models.EmpleadoDTO;
import henriquez.ProyectoApi.Services.AuthenticationEmpleado.AuthenticationEmpleadoService;
import henriquez.ProyectoApi.Utils.JWT.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/authEmpleado")
public class AuthenticationEmpleadoController {
    @Autowired
    private AuthenticationEmpleadoService service;

    @Autowired
    private JwtUtils jwtUtils;   // Inyeccion para acceder a los metodos de la clase JWTUtils


    /**
     * Proceso para la creaciòn del inicio de sesiòn
     */
    @PostMapping("/login")
    private ResponseEntity<String> login(@RequestBody EmpleadoDTO data, HttpServletResponse response) {
        if (data.getCorreoElectronico() == null || data.getCorreoElectronico().isBlank() ||
                data.getContrasena() == null || data.getContrasena().isBlank()) {
            return ResponseEntity.status(401).body("Error: Credenciales incompletas");
        } // Ahi se hacen las validaciones si vienen los campos nulos o sin datos

        if (service.Login(data.getCorreoElectronico(), data.getContrasena())) {
            addTokenCookie(response, data.getCorreoElectronico()); // Pasar solo el correo
            return ResponseEntity.ok("Inicio de sesión exitoso");
        } // Aca se verificó que las credenciales son correctas y inicia sesiòn

        return ResponseEntity.status(401).body("Credenciales incorrectas");  // Aca se dice que el nombreUsuario o la contraseña son incorrectas
    }

    /**
     * Se genera el token y se guarda en la Cookie
     */
    private void addTokenCookie(HttpServletResponse response, String correoEstudiantil) {
        // Obtener el cliente completo de la base de datos
        Optional<EmpleadoEntity> userOpt = service.getUser(correoEstudiantil);

        if (userOpt.isPresent()) {
            EmpleadoEntity user = userOpt.get();
            String token = jwtUtils.createJWTEmpleado( // Metodo para crear el JWT
                    String.valueOf(user.getIdEmpleado()), // ID del token
                    user.getCorreoElectronico(),    // Obtiene el correo electronico
                    user.getIdRol().getNombreRol(), // Obtiene el ROL
                    user.getIdEmpleado(), // Obtiene el ID del empleado
                    user.getIdRol().getIdRol() // Obtiene el ID del rol
            );

            String cookieValue = String.format(
                    "authToken=%s; " +
                            "Path=/; " +
                            "HttpOnly; " +
                            "Secure; " +
                            "SameSite=None; " +
                            "MaxAge=86400; "+
                            "Domain=https://portalwebhenriquez2025-feb6aad59691.herokuapp.com",
                    token
            );

            response.addHeader("Set-Cookie", cookieValue);
            response.addHeader("Access-Control-Expose-Headers", "Set-Cookie");
        }
    }


    /**
     * Para ver la informaciòn completa del token y la cookie
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "authenticated", false,
                                "message", "No autenticado"
                        ));
            }

            // Manejar diferentes tipos de Principal
            String username;
            Collection<? extends GrantedAuthority> authorities;

            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                username = userDetails.getUsername();
                authorities = userDetails.getAuthorities();
            } else {
                username = authentication.getName();
                authorities = authentication.getAuthorities();
            }

            Optional<EmpleadoEntity> userOpt = service.getUser(username);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "authenticated", false,
                                "message", "Usuario no encontrado"
                        ));
            }

            EmpleadoEntity user = userOpt.get();

            return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "user", Map.of(
                            "id", user.getIdEmpleado(),
                            "nombre", user.getEmpleadoNombre(),
                            "apellido", user.getEmpleadoApellido(),
                            "correo", user.getCorreoElectronico(),
                            "idRol", user.getIdRol().getIdRol(),
                            "authorities", authorities.stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.toList())
                    )
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "authenticated", false,
                            "message", "Error obteniendo datos del cliente"
                    ));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        // Crear cookie de expiración con SameSite=None
        String cookieValue = "authToken=; Path=/; HttpOnly; Secure; SameSite=None; MaxAge=0; Domain=https://portalwebhenriquez2025-feb6aad59691.herokuapp.com";

        response.addHeader("Set-Cookie", cookieValue);
        response.addHeader("Access-Control-Expose-Headers", "Set-Cookie");

        // También agregar headers CORS para la respuesta
        String origin = request.getHeader("Origin");
        if (origin != null &&
                (origin.contains("localhost") || origin.contains("herokuapp.com"))) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }
        return ResponseEntity.ok()
                .body("Logout exitoso");
    }




}
