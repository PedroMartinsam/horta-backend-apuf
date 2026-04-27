package com.apuf.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "produtos")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @NotNull
    @DecimalMin("0.01")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    @Builder.Default
    private Integer estoque = 0;

    @Column
    @Builder.Default
    private String unidade = "unidade";

    @Column
    private String imagemUrl;

    @Column
    private String imagemPublicId; // Cloudinary public ID for deletion

    @Column(nullable = false)
    @Builder.Default
    private boolean ativo = true;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum Categoria { VERDURAS, LEGUMES, FRUTAS, KITS }
}
