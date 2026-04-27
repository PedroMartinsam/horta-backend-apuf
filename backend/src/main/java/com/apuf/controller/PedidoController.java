package com.apuf.controller;

import com.apuf.dto.PedidoDTO;
import com.apuf.model.Pedido;
import com.apuf.model.Usuario;
import com.apuf.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    /** Create order (public – guest or logged-in) */
    @PostMapping
    public ResponseEntity<PedidoDTO.Response> criar(
            @Valid @RequestBody PedidoDTO.CreateRequest request,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(pedidoService.criar(request, usuario));
    }

    /** List all orders (admin only) */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PedidoDTO.Response>> listar(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(pedidoService.listar(status));
    }

    /** Get order by id (admin only) */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoDTO.Response> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    /** Update status (admin only) */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoDTO.Response> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody PedidoDTO.StatusRequest request) {
        return ResponseEntity.ok(pedidoService.atualizarStatus(id, request.getStatus()));
    }

    /** My orders (authenticated client) */
    @GetMapping("/meus")
    public ResponseEntity<List<PedidoDTO.Response>> meusPedidos(
            @AuthenticationPrincipal Usuario usuario) {
        if (usuario == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(pedidoService.meusPedidos(usuario));
    }
}
