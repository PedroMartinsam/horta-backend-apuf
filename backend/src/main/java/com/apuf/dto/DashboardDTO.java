package com.apuf.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

public class DashboardDTO {

    @Data
    public static class Metricas {
        private BigDecimal totalVendido;
        private long pedidosHoje;
        private long produtosAtivos;
        private long estoqueAlerta;
    }

    @Data
    public static class VendaDia {
        private String dia;
        private BigDecimal total;
    }

    @Data
    public static class ProdutoVendido {
        private Long id;
        private String nome;
        private long quantidade;
    }

    @Data
    public static class ResumoVendas {
        private BigDecimal totalPeriodo;
        private BigDecimal mediaDiaria;
        private String melhorDia;
        private BigDecimal melhorDiaTotal;
        private List<VendaDia> vendasPorDia;
        private List<ProdutoVendido> maisVendidos;
    }
}
