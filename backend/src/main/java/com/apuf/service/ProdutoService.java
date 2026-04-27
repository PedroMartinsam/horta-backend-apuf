package com.apuf.service;

import com.apuf.dto.ProdutoDTO;
import com.apuf.model.Produto;
import com.apuf.repository.ProdutoRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final Cloudinary cloudinary;

    // =========================
    // LISTAR
    // =========================
    public List<ProdutoDTO.Response> listar(String busca, String categoria, Boolean ativo) {
        List<Produto> produtos;

        if (busca != null || categoria != null) {
            Produto.Categoria cat = null;

            if (categoria != null && !categoria.isBlank()) {
                try {
                    cat = Produto.Categoria.valueOf(categoria.toUpperCase());
                } catch (IllegalArgumentException ignored) {}
            }

            produtos = produtoRepository.buscar(busca, cat);

        } else if (Boolean.TRUE.equals(ativo)) {
            produtos = produtoRepository.findByAtivoTrue();

        } else {
            produtos = produtoRepository.findAll();
        }

        return produtos.stream()
                .map(ProdutoDTO.Response::from)
                .toList();
    }

    // =========================
    // BUSCAR POR ID
    // =========================
    public ProdutoDTO.Response buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .map(ProdutoDTO.Response::from)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));
    }

    // =========================
    // CRIAR
    // =========================
    @Transactional
    public ProdutoDTO.Response criar(ProdutoDTO.Request request) {

        Produto produto = Produto.builder()
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .preco(request.getPreco())
                .categoria(request.getCategoria())
                .estoque(request.getEstoque())
                .unidade(request.getUnidade() != null ? request.getUnidade() : "unidade")
                .ativo(request.isAtivo())
                .imagemUrl(request.getImagemUrl()) // mantém imagem se vier
                .build();

        return ProdutoDTO.Response.from(produtoRepository.save(produto));
    }

    // =========================
    // ATUALIZAR
    // =========================
    @Transactional
    public ProdutoDTO.Response atualizar(Long id, ProdutoDTO.Request request) {

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));

        produto.setNome(request.getNome());
        produto.setDescricao(request.getDescricao());
        produto.setPreco(request.getPreco());
        produto.setCategoria(request.getCategoria());
        produto.setEstoque(request.getEstoque());

        if (request.getUnidade() != null) {
            produto.setUnidade(request.getUnidade());
        }

        produto.setAtivo(request.isAtivo());

        // só atualiza imagem se vier nova
        if (request.getImagemUrl() != null && !request.getImagemUrl().isBlank()) {
            produto.setImagemUrl(request.getImagemUrl());
        }

        return ProdutoDTO.Response.from(produtoRepository.save(produto));
    }

    // =========================
    // DELETAR
    // =========================
    @Transactional
    public void deletar(Long id) {

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));

        if (produto.getImagemPublicId() != null) {
            removerImagemCloudinary(produto.getImagemPublicId());
        }

        produtoRepository.delete(produto);
    }

    // =========================
    // UPLOAD DE IMAGEM
    // =========================
    @Transactional
    public ProdutoDTO.Response uploadImagem(Long id, MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Arquivo de imagem não enviado ou vazio");
        }

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));

        try {

            // remove imagem antiga
            if (produto.getImagemPublicId() != null) {
                removerImagemCloudinary(produto.getImagemPublicId());
            }

            Map<String, Object> options = ObjectUtils.asMap(
                    "folder", "apuf/produtos",
                    "resource_type", "image"
            );

            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), options);

            String url = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");

            if (url == null) {
                throw new RuntimeException("Cloudinary não retornou URL");
            }

            produto.setImagemUrl(url);
            produto.setImagemPublicId(publicId);

            return ProdutoDTO.Response.from(produtoRepository.save(produto));

        } catch (Exception e) {
            log.error("Erro no upload da imagem", e);
            throw new RuntimeException("Erro no upload da imagem: " + e.getMessage());
        }
    }

    // =========================
    // REMOVER IMAGEM
    // =========================
    private void removerImagemCloudinary(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            log.warn("Erro ao remover imagem do Cloudinary: {}", publicId);
        }
    }

    // =========================
    // LISTAR CATEGORIAS
    // =========================
    public List<String> listarCategorias() {
        return List.of("VERDURAS", "LEGUMES", "FRUTAS", "KITS");
    }
}