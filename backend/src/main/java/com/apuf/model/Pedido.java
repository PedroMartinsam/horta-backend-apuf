package com.apuf.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String numeroPedido;

    // Cliente info (denormalized for guest checkout)
    @Column(nullable = false)
    private String clienteNome;

    @Column(nullable = false)
    private String clienteTelefone;

    @Column
    private String clienteRua;

    @Column
    private String clienteNumero;

    @Column
    private String clienteBairro;

    @Column
    private String clienteReferencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario; // null if guest

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemPedido> itens = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.PENDENTE;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void gerarNumeroPedido() {
        if (numeroPedido == null) {
            numeroPedido = "#" + String.format("%05d", System.currentTimeMillis() % 100000);
        }
    }

    public enum Status { PENDENTE, PAGO, ENVIADO, ENTREGUE, CANCELADO }
}
