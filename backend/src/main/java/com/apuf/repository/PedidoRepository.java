package com.apuf.repository;

import com.apuf.model.Pedido;
import com.apuf.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByStatusOrderByCreatedAtDesc(Pedido.Status status);

    List<Pedido> findByOrderByCreatedAtDesc();

    List<Pedido> findByUsuarioOrderByCreatedAtDesc(Usuario usuario);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.createdAt >= :inicio AND p.createdAt < :fim")
    long countPedidosHoje(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.status != 'CANCELADO'")
    BigDecimal totalVendido();

    @Query("""
        SELECT CAST(p.createdAt AS date) as dia, SUM(p.total) as total
        FROM Pedido p
        WHERE p.createdAt >= :inicio AND p.status != 'CANCELADO'
        GROUP BY CAST(p.createdAt AS date)
        ORDER BY CAST(p.createdAt AS date)
    """)
    List<Object[]> vendasPorDia(@Param("inicio") LocalDateTime inicio);
}
