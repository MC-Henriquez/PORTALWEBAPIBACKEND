package henriquez.ProyectoApi.Controllers.Cloudinary;


import henriquez.ProyectoApi.Services.Cloudinary.CloudinaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController // <-- Clase de nivel superior
@RequestMapping("/api/imagen")
@CrossOrigin

public class CloudinaryController { // <-- Renombrada y simple
    private static final Logger logger = LoggerFactory.getLogger(CloudinaryController.class);


    @Autowired
    private final CloudinaryService cloudinaryService;

    public CloudinaryController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/subirImagen")
public ResponseEntity<?> uploadImage(
        @RequestParam(value = "image", required = false) MultipartFile file,
        @RequestParam(defaultValue = "general") String folder) {

    try {
        // Caso SIN imagen (permitido)
        if (file == null || file.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "message", "Empleado registrado sin imagen",
                    "url", null
            ));
        }

        // Validación básica
        if (!file.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "El archivo no es una imagen válida")
            );
        }

        String imageUrl = cloudinaryService.uploadImage(file, folder);

        return ResponseEntity.ok(Map.of(
                "message", "Imagen subida exitosamente",
                "url", imageUrl
        ));

    } catch (IOException e) {
        logger.error("Error Cloudinary", e);
        return ResponseEntity.internalServerError().body(
                Map.of("error", "No se pudo subir la imagen")
        );
    }
}

    @PostMapping("SubirImagenCarpeta")
    public ResponseEntity<?> uploadImageToFolder(@RequestParam("image") MultipartFile file, @RequestParam String folder) {

        // Se elimina el "throws IOException" porque ahora manejamos todas las excepciones adentro
        try {
            String folderName = "general";
            String imageUrl = cloudinaryService.uploadImage(file, folderName);

            // Pequeña corrección: "message" debe ser una cadena, no "message:"
            return ResponseEntity.ok(Map.of(
                    "message", "Imagen subida exitosamente",
                    "url", imageUrl
            ));

        } catch (IllegalArgumentException e) {
            // Error específico para validaciones (archivo muy grande, formato incorrecto, etc.)
            // Devuelve un error 400 Bad Request, que es más apropiado para datos inválidos del cliente.
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));

        } catch (IOException | RuntimeException e) {
            // Errores de lectura del archivo (IOException) o errores de subida a Cloudinary (RuntimeException)
            // Devuelve un error 500 Internal Server Error.
            logger.error("Error al subir imagen: {}", e.getMessage(), e); // Suponiendo que tienes un logger
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno al procesar la imagen."));
        }
    }
}

