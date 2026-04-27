package com.apuf.controller;

import com.apuf.dto.ProdutoDTO;
import com.apuf.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<List<ProdutoDTO.Response>> listar(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Boolean ativo) {
        return ResponseEntity.ok(produtoService.listar(busca, categoria, ativo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO.Response> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<String>> categorias() {
        return ResponseEntity.ok(produtoService.listarCategorias());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoDTO.Response> criar(
            @Valid @RequestBody ProdutoDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(produtoService.criar(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoDTO.Response> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoDTO.Request request) {
        return ResponseEntity.ok(produtoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/imagem")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoDTO.Response> uploadImagem(
            @PathVariable Long id,
            @RequestParam("imagem") MultipartFile file) {
        return ResponseEntity.ok(produtoService.uploadImagem(id, file));
    }
}
