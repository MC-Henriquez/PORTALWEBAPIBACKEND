package henriquez.ProyectoApi.Config.Argon2;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.stereotype.Component;

@Component // <-- ¡Añade esta línea!
public class Argon2PasswordEncoder {
    private static final int ITERATIONS = 5;
    private static final int MEMORY = 65536;
    private static final int PARALLELISM = 2;

    private Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    public String HashPassword(String rawPassword){
        return argon2.hash(ITERATIONS,MEMORY,PARALLELISM,rawPassword);
    }

    public boolean verifyPassword(String passwordBD, String password){
        return argon2.verify(passwordBD,password);
    }
}