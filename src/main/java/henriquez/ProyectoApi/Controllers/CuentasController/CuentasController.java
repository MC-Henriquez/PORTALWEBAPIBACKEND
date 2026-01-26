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

    // -----------------------------------------------------------
    // 1. MOSTRAR CUENTAS
    // -----------------------------------------------------------
    @GetMapping("/MostrarCuenta")
    public List<CuentasDTO> getallCuentas() {
        return service.MostrarCuenta();
    }

    // -----------------------------------------------------------
    // 2. CUENTAS POR CLIENTE
    // -----------------------------------------------------------
    @GetMapping("/CuentasPorClienteId/{idCliente}")
    public ResponseEntity<List<CuentasEntity>> obtenerPorCliente(
            @PathVariable String idCliente) {

        List<CuentasEntity> lista = cuentasRepository.findByCliente(idCliente);

        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lista);
    }

    // -----------------------------------------------------------
    // 3. ESTADO DE CUENTA (MOVIMIENTOS)
    // -----------------------------------------------------------
    @GetMapping("/estado-cuenta/{idCliente}")
    public ResponseEntity<List<EstadoCuentaDetalleDTO>> getDetalleEstadoCuenta(
            @PathVariable String idCliente,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        if (fechaInicio.isAfter(fechaFin)) {
            return ResponseEntity.badRequest().body(null);
        }

        List<EstadoCuentaDetalleDTO> detalle =
                service.obtenerDetalleTransacciones(idCliente, fechaInicio, fechaFin);

        if (detalle.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(detalle);
    }

    // -----------------------------------------------------------
    // 4. RANGO HISTÓRICO
    // -----------------------------------------------------------
    @GetMapping("/rango-historico")
    public ResponseEntity<RangoHistoricoDTO> getRangoHistorico(
            @RequestParam String clienteId) {

        RangoHistoricoDTO rango = service.obtenerRangoHistorico(clienteId);

        if (rango == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(rango);
    }

    // -----------------------------------------------------------
    // 5. TOTALES CONSOLIDADOS (🔑 AJUSTE CLAVE AQUÍ)
    // -----------------------------------------------------------
    @GetMapping("/totales/{idCliente}")
    public TotalConsolidadoDTO obtenerTotalesConsolidados(
            @PathVariable String idCliente) {

        TotalConsolidadoDTO totales = service.obtenerTotalesConsolidados(idCliente);

        if (totales != null) {
            // 🔴 AJUSTE CLAVE:
            // Forzamos que el saldoTotal sea EXACTAMENTE
            // el saldo que el cliente debe (cargos - abonos)
            totales.setSaldoTotal(
                    totales.getTotalCargos() - totales.getTotalAbonos()
            );
        }

        return totales;
    }

    // -----------------------------------------------------------
    // 6. TOP 5 FACTURAS
    // -----------------------------------------------------------
    @GetMapping("/detallesDeFactura/{idCliente}")
    public ResponseEntity<List<CuentaDTO>> obtenerDetalle(
            @PathVariable String idCliente) {

        return ResponseEntity.ok(service.obtenerTop5PorCliente(idCliente));
    }
}

