package henriquez.ProyectoApi.Exceptions.Rol;

public class TransactionRecordsNotFoundException extends RuntimeException {
    public TransactionRecordsNotFoundException(Long clienteId) {
        // Mensaje que se mostrará
        super("No se encontraron registros de transacciones para el cliente con ID: " + clienteId);
    }

    public TransactionRecordsNotFoundException(String message) {
        super(message);
    }
}
