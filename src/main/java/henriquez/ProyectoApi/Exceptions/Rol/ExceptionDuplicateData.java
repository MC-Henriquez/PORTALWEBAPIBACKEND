package henriquez.ProyectoApi.Exceptions.Rol;

import lombok.Getter;

public class ExceptionDuplicateData extends RuntimeException {
    @Getter
    private  String duplicateField;

    public ExceptionDuplicateData( String message ,String  duplicateField) {
        super(message);
        this.duplicateField = duplicateField;
    }

    public ExceptionDuplicateData(String message) {
        super(message);
    }
}
