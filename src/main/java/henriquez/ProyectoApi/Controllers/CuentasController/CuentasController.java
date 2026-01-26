package henriquez.ProyectoApi.Controllers.CuentasController;


import henriquez.ProyectoApi.Entities.Cuentas.CuentasEntity;
import henriquez.ProyectoApi.Models.*;
import henriquez.ProyectoApi.Repositories.Cuentas.CuentasRepository;
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

    @Autowired
    private CuentasRepository cuentasRepository;


    // --- 1. LEER (READ) ---
    @GetMapping("/MostrarCuenta")
    public List<CuentasDTO> getallCuentas() {
        return service.MostrarCuenta();
    }

    // ===========================================================
    // 1. Obtener todas las cuentas por ID de cliente
    // ===========================================================
    @GetMapping("/CuentasPorClienteId/{idCliente}")
    public ResponseEntity<List<CuentasEntity>> obtenerPorCliente(@PathVariable String idCliente) {
        List<CuentasEntity> lista = cuentasRepository.findByCliente(idCliente);

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }


    // -------------------------------------------------------------------------------------
    // ENDPOINT 2: Filtrado y Detalle de Transacciones (Movimientos)
    // RUTA: GET /api/cuentas/estado-cuenta/{idCliente}?fechaInicio=YYYY-MM-DD&fechaFin=YYYY-MM-DD
    // -------------------------------------------------------------------------------------

    @GetMapping("/estado-cuenta/{idCliente}")
    public ResponseEntity<List<EstadoCuentaDetalleDTO>> getDetalleEstadoCuenta(
            @PathVariable String idCliente,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        // Validación básica (se puede mejorar)
        if (fechaInicio.isAfter(fechaFin)) {
            // Devuelve un error 400 Bad Request si el rango de fechas es inválido
            return ResponseEntity.badRequest().body(null);
        }

        // Llamar al servicio para obtener los datos filtrados y calculados
        List<EstadoCuentaDetalleDTO> detalle = service.obtenerDetalleTransacciones(
                idCliente,
                fechaInicio,
                fechaFin
        );

        if (detalle.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(detalle);
    }

    // -------------------------------------------------------------------------------------
    // ENDPOINT 3: Rango Historico
    // RUTA: GET /api/cuentas/rango-historico?clienteId={idCliente}
    // -------------------------------------------------------------------------------------
    @GetMapping("/rango-historico")
    public ResponseEntity<RangoHistoricoDTO> getRangoHistorico(
            @RequestParam String clienteId) {

        RangoHistoricoDTO rango = service.obtenerRangoHistorico(clienteId);

        if (rango == null) {
            // Podrías devolver 204 si el cliente existe pero no tiene transacciones
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(rango);
    }

    // -------------------------------------------------------------------------------------
    // ENDPOINT 4: Totales Consolidados (Resumen Histórico)
    // RUTA: GET /api/cuentas/totales/{idCliente}
    // -------------------------------------------------------------------------------------
    @GetMapping("/totales/{idCliente}")
    public monto obtenerTotalesConsolidados(@PathVariable String idCliente) {
        return service.monto(idCliente);
    }

    @GetMapping("/detallesDeFactura/{idCliente}")
    public ResponseEntity<List<CuentaDTO>> obtenerDetalle(@PathVariable String idCliente) {
        return ResponseEntity.ok(service.obtenerTop5PorCliente(idCliente));
    }
}
