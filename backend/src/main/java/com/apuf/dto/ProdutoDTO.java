package com.apuf.dto;

import com.apuf.model.Produto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProdutoDTO {

    @Data
    public static class Request {
        @NotBlank(message = "Nome é obrigatório")
        private String nome;

        private String descricao;

        @NotNull(message = "Preço é obrigatório")
        @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
        private BigDecimal preco;

        @NotNull(message = "Categoria é obrigatória")
        private Produto.Categoria categoria;

        @NotNull(message = "Estoque é obrigatório")
        @Min(value = 0, message = "Estoque não pode ser negativo")
        private Integer estoque;

        private String unidade;

        private boolean ativo = true;

        // ✅ BUG FIX: campo imagemUrl estava faltando no Request
        // Necessário para salvar imagem junto com produto via base64 ou URL externa
        private String imagemUrl;
    }

    @Data
    public static class Response {
        private Long id;
        private String nome;
        private String descricao;
        private BigDecimal preco;
        private Produto.Categoria categoria;
        private Integer estoque;
        private String unidade;
        private String imagemUrl;
        private boolean ativo;
        private LocalDateTime createdAt;

        public static Response from(Produto p) {
            Response r = new Response();
            r.setId(p.getId());
            r.setNome(p.getNome());
            r.setDescricao(p.getDescricao());
            r.setPreco(p.getPreco());
            r.setCategoria(p.getCategoria());
            r.setEstoque(p.getEstoque());
            r.setUnidade(p.getUnidade());
            r.setImagemUrl(p.getImagemUrl());
            r.setAtivo(p.isAtivo());
            r.setCreatedAt(p.getCreatedAt());
            return r;
        }
    }

    @Data
    public static class ImagemResponse {
        private String url;
        private String publicId;
    }
}
