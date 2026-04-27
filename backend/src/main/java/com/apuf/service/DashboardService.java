package com.apuf.service;

import com.apuf.dto.DashboardDTO;
import com.apuf.repository.PedidoRepository;
import com.apuf.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    public DashboardDTO.Metricas getMetricas() {
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime fimDia   = inicioDia.plusDays(1);

        DashboardDTO.Metricas m = new DashboardDTO.Metricas();
        m.setTotalVendido(pedidoRepository.totalVendido());
        m.setPedidosHoje(pedidoRepository.countPedidosHoje(inicioDia, fimDia));
        m.setProdutosAtivos(produtoRepository.countAtivos());
        m.setEstoqueAlerta((long) produtoRepository.findByEstoqueLessThanEqualAndAtivoTrue(5).size());
        return m;
    }

    public List<DashboardDTO.VendaDia> getVendasPorDia() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);

        return pedidoRepository.vendasPorDia(inicio).stream().map(row -> {
            DashboardDTO.VendaDia v = new DashboardDTO.VendaDia();
            v.setDia(row[0].toString());
            v.setTotal(new BigDecimal(row[1].toString()));
            return v;
        }).toList();
    }

    public List<DashboardDTO.ProdutoVendido> getMaisVendidos() {
        return produtoRepository.findMaisVendidosComQuantidade(10).stream().map(row -> {
            com.apuf.model.Produto p = (com.apuf.model.Produto) row[0];
            long qtd = row[1] instanceof Number ? ((Number) row[1]).longValue() : 0L;

            DashboardDTO.ProdutoVendido pv = new DashboardDTO.ProdutoVendido();
            pv.setId(p.getId());
            pv.setNome(p.getNome());
            pv.setQuantidade(qtd);
            return pv;
        }).toList();
    }
}
