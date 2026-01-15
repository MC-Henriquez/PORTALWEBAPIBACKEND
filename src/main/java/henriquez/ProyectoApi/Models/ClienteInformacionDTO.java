package henriquez.ProyectoApi.Models;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO específico para el endpoint de información de cliente,
 * utilizando las claves que el Front-end requiere (sinceClient, attn, etc.).
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Data
@Builder
public class ClienteInformacionDTO {
    // Corresponde al id_cliente o cod_cliente formateado (Ej: 05086)
    private String sinceClient;
    private String untilClient;

    // Razón Social o Nombre Completo de Persona Natural
    private String clientName;

    // Nombre y Apellido de la persona de contacto (Attn)
    private String attn;

    // Tipo de persona: "Natural" o "Jurídica"
    private String type;

    // NIT formateado (Ej: 0614-110666-001-0)
    private String nit;

    private String phone;

    private String address;

    private BigDecimal limiteCredito;

}
