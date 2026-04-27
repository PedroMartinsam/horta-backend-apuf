package com.apuf.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @Column(nullable = false)
    private String nomeProduto; // snapshot at time of order

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario; // snapshot

    public BigDecimal getSubtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}
