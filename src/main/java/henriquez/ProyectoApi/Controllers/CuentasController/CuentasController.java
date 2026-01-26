package henriquez.ProyectoApi.Controllers.Cuentas;

import henriquez.ProyectoApi.Entities.Cuentas.CuentasEntity;
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
    // 1. MOSTRAR TODAS LAS CUENTAS
    // ==========================================================
    @GetMapping("/MostrarCuenta")
    public List<CuentasDTO> getallCuentas() {
        return service.MostrarCuenta();
    }

    // ==========================================================
    // 2. CUENTAS POR CLIENTE (SIN USAR REPOSITORY DIRECTO)
    // ==========================================================
    @GetMapping("/CuentasPorClienteId/{idCliente}")
    public ResponseEntity<List<CuentasEntity>> obtenerPorCliente(
            @PathVariable String idCliente) {

        List<CuentasEntity> lista = service.obtenerPorCliente(idCliente);

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    // ==========================================================
    // 3. ESTADO DE CUENTA POR RANGO DE FECHAS
    // ==========================================================
    @GetMapping("/estado-cuenta/{idCliente}")
    public ResponseEntity<List<EstadoCuentaDetalleDTO>> getDetalleEstadoCuenta(
            @PathVariable String idCliente,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        if (fechaInicio.isAfter(fechaFin)) {
            return ResponseEntity.badRequest().build();
        }

        List<EstadoCuentaDetalleDTO> detalle =
                service.obtenerDetalleTransacciones(idCliente, fechaInicio, fechaFin);

        if (detalle.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(detalle);
    }

    // ==========================================================
    // 4. RANGO HISTÓRICO
    // ==========================================================
    @GetMapping("/rango-historico")
    public ResponseEntity<RangoHistoricoDTO> getRangoHistorico(
            @RequestParam String clienteId) {

        RangoHistoricoDTO rango = service.obtenerRangoHistorico(clienteId);

        if (rango == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(rango);
    }

    // ==========================================================
    // 5. TOTALES CONSOLIDADOS
    // ==========================================================
    @GetMapping("/totales/{idCliente}")
    public TotalConsolidadoDTO obtenerTotalesConsolidados(
            @PathVariable String idCliente) {

        return service.obtenerTotalesConsolidados(idCliente);
    }

    // ==========================================================
    // 6. TOP 5 FACTURAS
    // ==========================================================
    @GetMapping("/detallesDeFactura/{idCliente}")
    public ResponseEntity<List<CuentaDTO>> obtenerDetalle(
            @PathVariable String idCliente) {

        return ResponseEntity.ok(service.obtenerTop5PorCliente(idCliente));
    }
}

