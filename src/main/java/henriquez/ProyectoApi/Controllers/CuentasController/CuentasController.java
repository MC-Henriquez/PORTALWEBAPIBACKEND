package henriquez.ProyectoApi.Controllers.CuentasController;

import henriquez.ProyectoApi.Models.*;
import henriquez.ProyectoApi.Services.Cuentas.CuentasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
@CrossOrigin
public class CuentasController {

    @Autowired
    private CuentasService service;

    // ==========================================================
    // 1️⃣ ESTADO DE CUENTA (DOCUMENTOS POR PERIODO)
    // ==========================================================
    @GetMapping("/estado-cuenta/{idCliente}")
    public ResponseEntity<Cuentas> obtenerEstadoCuenta(
            @PathVariable String idCliente,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        if (fechaInicio.isAfter(fechaFin)) {
            return ResponseEntity.badRequest().build();
        }

        Cuentas response =
                service.Cuentas(idCliente, fechaInicio, fechaFin);

        if (response.getDocuments().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

    // ==========================================================
    // 2️⃣ TOTALES CONSOLIDADOS
    // ==========================================================
    @GetMapping("/totales/{idCliente}")
    public ResponseEntity<Total> obtenerTotales(
            @PathVariable String idCliente) {

        return ResponseEntity.ok(
                service.Total(idCliente)
        );
    }

    // ==========================================================
    // 3️⃣ RANGO HISTÓRICO (AÑOS DISPONIBLES)
    // ==========================================================
    @GetMapping("/rango-historico")
    public ResponseEntity<Rango> obtenerRangoHistorico(
            @RequestParam String clienteId) {

        Rango rango = service.Rango(clienteId);

        if (rango == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(rango);
    }

    // ==========================================================
    // 4️⃣ INFO DEL CLIENTE
    // ==========================================================
    @GetMapping("/cliente-info/{idCliente}")
    public ResponseEntity<Cliente> obtenerClienteInfo(
            @PathVariable String idCliente) {

        Cliente info = service.Cliente(idCliente);

        if (info == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(info);
    }

    // ==========================================================
    // 5️⃣ TOP FACTURAS RECIENTES
    // ==========================================================
    @GetMapping("/detallesDeFactura/{idCliente}")
    public ResponseEntity<List<Cuentas>> obtenerDetalle(
            @PathVariable String idCliente) {

        return ResponseEntity.ok(
                service.obtenerTop5PorCliente(idCliente)
        );
    }
}
