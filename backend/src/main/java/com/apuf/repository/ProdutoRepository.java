package com.apuf.repository;

import com.apuf.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByAtivoTrue();

    List<Produto> findByCategoriaAndAtivoTrue(Produto.Categoria categoria);

    @Query("""
        SELECT p FROM Produto p
        WHERE p.ativo = true
        AND (:busca IS NULL
             OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
             OR LOWER(p.descricao) LIKE LOWER(CONCAT('%', :busca, '%')))
        AND (:categoria IS NULL OR p.categoria = :categoria)
        ORDER BY p.nome
    """)
    List<Produto> buscar(
        @Param("busca") String busca,
        @Param("categoria") Produto.Categoria categoria
    );

    List<Produto> findByEstoqueLessThanEqualAndAtivoTrue(int estoque);

    @Query("""
        SELECT p, COALESCE(SUM(ip.quantidade), 0) as totalVendido
        FROM Produto p
        LEFT JOIN ItemPedido ip ON ip.produto = p
        WHERE p.ativo = true
        GROUP BY p
        ORDER BY totalVendido DESC
        LIMIT :limit
    """)
    List<Object[]> findMaisVendidosComQuantidade(@Param("limit") int limit);

    @Query("SELECT COUNT(p) FROM Produto p WHERE p.ativo = true")
    long countAtivos();
}
