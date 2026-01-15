package henriquez.ProyectoApi.Services.Cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {
    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;
    private static final String[]ALLOWED_EXTENSIONS = {".jpeg",".jpg",".dng",".webp",".png"};
    private static final Logger logger = LoggerFactory.getLogger(CloudinaryService.class);
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) { this.cloudinary = cloudinary; }

    public String uploadImage (MultipartFile file, String folder) throws IOException {
        // 1. Validar la imagen (tamaño, tipo, etc.)
        valideImage(file);

        // 2. Generar un nombre de archivo único para evitar colisiones
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uniqueFileName = "img_" + UUID.randomUUID().toString() + fileExtension;

        // 3. Configurar las opciones de subida
        Map<String, Object> options = ObjectUtils.asMap(
                "folder", folder,                 // Carpeta destino
                "public_id", uniqueFileName,      // Nombre único para el archivo
                "resource_type", "auto",          // Autodetecta el tipo de recurso
                "quality", "auto:good"            // Optimiza la calidad automáticamente
        );

        // 4. Intentar subir el archivo y manejar posibles errores
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            String secureUrl = (String) uploadResult.get("secure_url");

            // Registrar en consola que la subida fue exitosa
            logger.info("Imagen subida exitosamente. URL: {}", secureUrl);

            return secureUrl;

        } catch (Exception e) {
            // Si algo falla durante la subida, se registra el error detallado en la consola
            logger.error("Error al subir la imagen a Cloudinary: {}", e.getMessage(), e);

            // Se lanza una nueva excepción para notificar al resto de la aplicación que algo salió mal
            throw new RuntimeException("No se pudo subir la imagen al servidor externo. Por favor, inténtalo de nuevo.", e);
        }
    }

    private void valideImage(MultipartFile file){
        if (file.isEmpty()){
            throw new IllegalArgumentException("El archivo no puede estar vacio");
        }

        if (file.getSize()> MAX_FILE_SIZE){
            throw new IllegalArgumentException("El archivo no puede ser mayor a 5 MB");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null){
            throw new IllegalArgumentException("Nombre del archivo invalido");
        }

        String extension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
        if (!Arrays.asList(ALLOWED_EXTENSIONS).contains(extension)){
            throw new IllegalArgumentException("Solo se permiten archivos JPG, JPEG y PNG");
        }

        if (!file.getContentType().startsWith("image/")){
            throw new IllegalArgumentException("El archivo debe ser una imagen valida.");
        }
    }
}
