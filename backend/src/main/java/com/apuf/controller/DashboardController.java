package com.apuf.controller;

import com.apuf.dto.DashboardDTO;
import com.apuf.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/metricas")
    public ResponseEntity<DashboardDTO.Metricas> metricas() {
        return ResponseEntity.ok(dashboardService.getMetricas());
    }

    @GetMapping("/vendas-por-dia")
    public ResponseEntity<List<DashboardDTO.VendaDia>> vendasPorDia() {
        return ResponseEntity.ok(dashboardService.getVendasPorDia());
    }

    @GetMapping("/mais-vendidos")
    public ResponseEntity<List<DashboardDTO.ProdutoVendido>> maisVendidos() {
        return ResponseEntity.ok(dashboardService.getMaisVendidos());
    }
}
