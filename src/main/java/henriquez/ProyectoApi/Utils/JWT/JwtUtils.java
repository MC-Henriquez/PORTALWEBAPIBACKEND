package henriquez.ProyectoApi.Utils.JWT;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {
    @Value("${security.jwt.secret}")
    private String jwtSecreto;               //sirve para la firma del token
    @Value("${security.jwt.issuer}")
    private String issuer;                   //nombre de quen crea el token
    @Value("${security.jwt.expiration}")
    private long expiracionMs;                //es cuanto tiempo dura el token

    private final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    /**
     * se crea un token usando el id con el correo del usuario
     * @param id
     * @param correo
     * @return
     */
    public String createJWTCliente(String id, String correo, String nombreRol, Long idRol, String idCliente){
        //aqui se convierte la secretkey en llave valida para poder firmar el token
        SecretKey signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecreto));

        //aqui se ve  la fecha catual y calcula la fecha de expiración
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expiracionMs);

        //se arman el token con todos los datos
        return Jwts.builder()
                .setId(id)                      //se pone un id al token
                .setIssuedAt(now)              //fecha en la que se creo el token
                .setSubject(correo)           //usuario o sea el correo
                .claim("id", id)          //se guarda el id del usuario
                .claim("rol", nombreRol)       //se guarda elrol del usuario
                .claim("idRol", idRol)
                .claim("idCliente", idCliente)
                .claim("corre", correo)
                .setIssuer(issuer)         //la persona que esta generando el token
                .setExpiration(expiracionMs >= 0 ? expiration : null) //cuando vence
                .signWith(signingKey, SignatureAlgorithm.HS256) // aqui pues se firma el tooken para que no puedan alterarlo
                .compact();  //se convierte en un token en formato string
    }

    public String createJWTEmpleado(String id, String correo, String nombreRol, Long idRol, Long idEmpleado){
        //aqui se convierte la secretkey en llave valida para poder firmar el token
        SecretKey signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecreto));

        //aqui se ve  la fecha catual y calcula la fecha de expiración
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expiracionMs);

        //se arman el token con todos los datos
        return Jwts.builder()
                .setId(id)                      //se pone un id al token
                .setIssuedAt(now)              //fecha en la que se creo el token
                .setSubject(correo)           //usuario o sea el correo
                .claim("id", id)          //se guarda el id del usuario
                .claim("rol", nombreRol)       //se guarda elrol del usuario
                .claim("idRol", idRol)
                .claim("idEmpleado", idEmpleado)
                .claim("correo", correo)
                .setIssuer(issuer)         //la persona que esta generando el token
                .setExpiration(expiracionMs >= 0 ? expiration : null) //cuando vence
                .signWith(signingKey, SignatureAlgorithm.HS256) // aqui pues se firma el tooken para que no puedan alterarlo
                .compact();  //se convierte en un token en formato string
    }

    /**
     *
     * se extrae el rol del usuario desde el token
     * @param token
     * @return
     */
    public String extractRol(String token){
        Claims claims = parseToken(token);      //aqui primero se abre el token
        return claims.get("rol",String.class); //y luego se pide el dato del rol
    }

    /**
     * se obtiene el correo  que esta dentro del token
     * @param jwt Token JWT como String
     * @return String con el subject del token
     */
    public String getValue(String jwt){
        Claims claims = parseClaims(jwt);       //se abre el token
        return claims.getSubject();             // se devuelve el correo del usuario
    }

    /**
     * se obtiene el id que se guardo en el token
     * @param jwt
     * @return
     */
    public String getKey(String jwt){
        Claims claims = parseClaims(jwt);
        return claims.getId();  //se devuelve el id
    }

    /**
     * se intenta abrir el token y poder leerlo por si esta vencido o malo,  lanzara un error
     * @param jwt
     * @return
     * @throws ExpiredJwtException
     * @throws MalformedJwtException
     */
    public Claims parseToken(String jwt) throws ExpiredJwtException, MalformedJwtException {
        return parseClaims(jwt);
    }

    /**
     * se revisa si el token es valido o no
     * @param token
     * @return
     */
    public boolean validate(String token){
        try{
            parseClaims(token); //si se puede abrir sera valido
            return true;
        }catch (JwtException | IllegalArgumentException e){
            log.warn("Token inválido: {}", e.getMessage()); //si no se escribe en el log
            return false;
        }
    }


    /**
     * se abre el token y se devolvera todos los datos que  hay adentro o sea el claims
     * @param jwt
     * @return Claims del token
     */
    private Claims parseClaims(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecreto)))  // Clave de firma
                .build()
                .parseClaimsJws(jwt)        //se abrira el token
                .getBody(); //devuelve los datos

    }
}
