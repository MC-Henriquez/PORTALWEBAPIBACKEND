package henriquez.ProyectoApi.Utils.JWT;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Component
public class JwtCookieAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtCookieAuthFilter.class);
    private static final String AUTH_COOKIE_NAME = "authToken";
    private final JwtUtils jwtUtils;

    @Autowired
    public JwtCookieAuthFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        //si es publico se deja pasar sin pedirle el token
        if (isPublicEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = extractTokenFromCookies(request);
            //cuando no hay token publico y el endpoint no es publico no se puede pasar
            if (token == null || token.isBlank()) {
                if (!isPublicEndpoint(request)) {
                    sendError(response, "Token no encontrado", HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
                //aqui si es publico si puede pasar
                filterChain.doFilter(request, response);
                return;
            }
            //aqui se lee el contenido que hay en el token y se verific si es valido
            Claims claims = jwtUtils.parseToken(token);

            //se saca el rol que tiene el usuario que esta en el token
            String rol = jwtUtils.extractRol(token);

            //se crea una lista de permisos segun el  rol que tenga el usuario
            Collection<? extends GrantedAuthority> authorities =
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol));

            //se crea el usuario autenticado
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            claims.getSubject(), // nombre del usuario
                            null,
                            authorities // permisos para el usuario
                    );

            //se guarda la autenticacion para que sprin pueda reconocerlo
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            //aqui se ve si el token ya se vencio
            log.warn("Token expirado: {}", e.getMessage());
            sendError(response, "Token expirado", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (MalformedJwtException e) {
            //se ve si el token esta mal o mal escrito
            log.warn("Token malformado: {}", e.getMessage());
            sendError(response, "Token inválido", HttpServletResponse.SC_FORBIDDEN);
        } catch (Exception e) {
            //error inesperado
            log.error("Error de autenticación", e);
            sendError(response, "Error de autenticación", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    //se busca la cookie "authtoken" y se duvuelve su valor
    private String extractTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        return Arrays.stream(cookies)
                .filter(c -> AUTH_COOKIE_NAME.equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)//se obtiene el valor o sea el token
                .orElse(null);
    }

    //se envia una respuesta del error al usuario en formato JSON
    private void sendError(HttpServletResponse response, String message, int status) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);
        response.getWriter().write(String.format(
                "{\"error\": \"%s\", \"status\": %d}", message, status));
    }

    //se verifica si la ruta que se esta pidiendo es pública sin la autenticación
    private boolean isPublicEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // esta es la lista de los endpoints públicos
        return (path.equals("/api/authCliente/login") && "POST".equals(method))   ||
                (path.equals("/api/authEmpleado/login") && "POST".equals(method)) ||
                (path.equals("/api/public/") && "GET".equals(method));
    }

}

