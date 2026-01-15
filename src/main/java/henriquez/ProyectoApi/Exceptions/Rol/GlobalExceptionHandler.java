package henriquez.ProyectoApi.Exceptions.Rol;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TransactionRecordsNotFoundException.class)
    public ResponseEntity<String> handleTransactionRecordsNotFoundException(
            TransactionRecordsNotFoundException ex) {

        // Devolvemos un 404 (Not Found) o 400 (Bad Request) con el mensaje de error.
        // Usamos 404 para indicar que el recurso (rango histórico) no existe para el ID.
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // (Añade aquí otros ExceptionHandlers si los tienes)
}
