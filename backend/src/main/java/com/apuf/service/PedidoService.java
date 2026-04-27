package com.apuf.service;

import com.apuf.dto.PedidoDTO;
import com.apuf.model.ItemPedido;
import com.apuf.model.Pedido;
import com.apuf.model.Produto;
import com.apuf.model.Usuario;
import com.apuf.repository.PedidoRepository;
import com.apuf.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    @Transactional
    public PedidoDTO.Response criar(PedidoDTO.CreateRequest request, Usuario usuarioLogado) {
        // Build Pedido
        PedidoDTO.ClienteInfo c = request.getCliente();

        Pedido pedido = Pedido.builder()
            .clienteNome(c.getNome())
            .clienteTelefone(c.getTelefone())
            .clienteRua(c.getRua())
            .clienteNumero(c.getNumero())
            .clienteBairro(c.getBairro())
            .clienteReferencia(c.getReferencia())
            .usuario(usuarioLogado)
            .total(request.getTotal())
            .status(Pedido.Status.PENDENTE)
            .itens(new ArrayList<>())
            .build();

        // Build itens and decrement stock
        for (PedidoDTO.ItemRequest itemReq : request.getItens()) {
            Produto produto = produtoRepository.findById(itemReq.getProdutoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + itemReq.getProdutoId()));

            if (produto.getEstoque() < itemReq.getQuantidade()) {
                throw new IllegalStateException(
                    "Estoque insuficiente para: " + produto.getNome() +
                    ". Disponível: " + produto.getEstoque()
                );
            }

            // Decrement stock
            produto.setEstoque(produto.getEstoque() - itemReq.getQuantidade());
            produtoRepository.save(produto);

            ItemPedido item = ItemPedido.builder()
                .pedido(pedido)
                .produto(produto)
                .nomeProduto(produto.getNome())
                .quantidade(itemReq.getQuantidade())
                .precoUnitario(itemReq.getPrecoUnitario())
                .build();

            pedido.getItens().add(item);
        }

        // Recalculate total server-side for security
        BigDecimal totalCalculado = pedido.getItens().stream()
            .map(ItemPedido::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        pedido.setTotal(totalCalculado);

        Pedido salvo = pedidoRepository.save(pedido);
        log.info("Pedido criado: {} | Cliente: {} | Total: R$ {}",
            salvo.getNumeroPedido(), salvo.getClienteNome(), salvo.getTotal());

        return PedidoDTO.Response.from(salvo);
    }

    public List<PedidoDTO.Response> listar(String status) {
        List<Pedido> pedidos;
        if (status != null && !status.isBlank()) {
            try {
                pedidos = pedidoRepository.findByStatusOrderByCreatedAtDesc(Pedido.Status.valueOf(status));
            } catch (IllegalArgumentException e) {
                pedidos = pedidoRepository.findByOrderByCreatedAtDesc();
            }
        } else {
            pedidos = pedidoRepository.findByOrderByCreatedAtDesc();
        }
        return pedidos.stream().map(PedidoDTO.Response::from).toList();
    }

    public PedidoDTO.Response buscarPorId(Long id) {
        return pedidoRepository.findById(id)
            .map(PedidoDTO.Response::from)
            .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + id));
    }

    @Transactional
    public PedidoDTO.Response atualizarStatus(Long id, Pedido.Status novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + id));

        // If cancelling, return stock
        if (novoStatus == Pedido.Status.CANCELADO && pedido.getStatus() != Pedido.Status.CANCELADO) {
            for (ItemPedido item : pedido.getItens()) {
                if (item.getProduto() != null) {
                    Produto produto = item.getProduto();
                    produto.setEstoque(produto.getEstoque() + item.getQuantidade());
                    produtoRepository.save(produto);
                }
            }
        }

        pedido.setStatus(novoStatus);
        return PedidoDTO.Response.from(pedidoRepository.save(pedido));
    }

    public List<PedidoDTO.Response> meusPedidos(Usuario usuario) {
        return pedidoRepository.findByUsuarioOrderByCreatedAtDesc(usuario)
            .stream().map(PedidoDTO.Response::from).toList();
    }
}
