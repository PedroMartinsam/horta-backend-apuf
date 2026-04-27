package com.apuf.dto;

import com.apuf.model.Pedido;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoDTO {

    @Data
    public static class ClienteInfo {
        @NotBlank(message = "Nome do cliente é obrigatório")
        private String nome;

        @NotBlank(message = "Telefone é obrigatório")
        private String telefone;

        private String rua;
        private String numero;
        private String bairro;
        private String referencia;
    }

    @Data
    public static class ItemRequest {
        @NotNull(message = "ID do produto é obrigatório")
        private Long produtoId;

        @NotNull
        @Min(value = 1, message = "Quantidade mínima é 1")
        private Integer quantidade;

        @NotNull(message = "Preço unitário é obrigatório")
        private BigDecimal precoUnitario;
    }

    @Data
    public static class CreateRequest {
        @NotNull(message = "Dados do cliente são obrigatórios")
        private ClienteInfo cliente;

        @NotEmpty(message = "Pedido deve ter pelo menos 1 item")
        private List<ItemRequest> itens;

        @NotNull
        private BigDecimal total;
    }

    @Data
    public static class StatusRequest {
        @NotNull(message = "Status é obrigatório")
        private Pedido.Status status;
    }

    @Data
    public static class ItemResponse {
        private Long id;
        private String nome;
        private Integer quantidade;
        private BigDecimal precoUnitario;
        private BigDecimal subtotal;
    }

    @Data
    public static class Response {
        private Long id;
        private String numeroPedido;
        private ClienteInfo cliente;
        private List<ItemResponse> itens;
        private BigDecimal total;
        private Pedido.Status status;
        private LocalDateTime createdAt;

        public static Response from(Pedido p) {
            Response r = new Response();
            r.setId(p.getId());
            r.setNumeroPedido(p.getNumeroPedido());
            r.setTotal(p.getTotal());
            r.setStatus(p.getStatus());
            r.setCreatedAt(p.getCreatedAt());

            ClienteInfo c = new ClienteInfo();
            c.setNome(p.getClienteNome());
            c.setTelefone(p.getClienteTelefone());
            c.setRua(p.getClienteRua());
            c.setNumero(p.getClienteNumero());
            c.setBairro(p.getClienteBairro());
            c.setReferencia(p.getClienteReferencia());
            r.setCliente(c);

            r.setItens(p.getItens().stream().map(i -> {
                ItemResponse ir = new ItemResponse();
                ir.setId(i.getId());
                ir.setNome(i.getNomeProduto());
                ir.setQuantidade(i.getQuantidade());
                ir.setPrecoUnitario(i.getPrecoUnitario());
                ir.setSubtotal(i.getSubtotal());
                return ir;
            }).toList());

            return r;
        }
    }
}
