package com.apuf.config;

import com.apuf.model.Produto;
import com.apuf.model.Usuario;
import com.apuf.repository.ProdutoRepository;
import com.apuf.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        criarAdminPadrao();
        criarProdutosIniciais();
    }

    private void criarAdminPadrao() {
        if (usuarioRepository.existsByEmail("admin@apuf.com.br")) return;

        Usuario admin = Usuario.builder()
            .nome("Administrador APUF")
            .email("admin@apuf.com.br")
            .senha(passwordEncoder.encode("apuf@2025"))
            .role(Usuario.Role.ADMIN)
            .ativo(true)
            .build();

        usuarioRepository.save(admin);
        log.info("✅ Admin criado: admin@apuf.com.br / apuf@2025");
    }

    private void criarProdutosIniciais() {
        if (produtoRepository.count() > 0) return;

        List<Produto> produtos = List.of(
            Produto.builder().nome("Alface Crespa").descricao("Fresca e crocante, colhida hoje cedo pelos nossos produtores").preco(new BigDecimal("3.50")).categoria(Produto.Categoria.VERDURAS).estoque(20).unidade("por pé").ativo(true).build(),
            Produto.builder().nome("Couve Manteiga").descricao("Folhas largas e macias, ideal para refogado ou vitamina").preco(new BigDecimal("4.00")).categoria(Produto.Categoria.VERDURAS).estoque(15).unidade("maço").ativo(true).build(),
            Produto.builder().nome("Rúcula").descricao("Picante e aromática, perfeita para saladas gourmet").preco(new BigDecimal("3.00")).categoria(Produto.Categoria.VERDURAS).estoque(8).unidade("maço").ativo(true).build(),
            Produto.builder().nome("Espinafre").descricao("Rico em ferro e vitaminas, colhido fresquinho").preco(new BigDecimal("3.50")).categoria(Produto.Categoria.VERDURAS).estoque(5).unidade("maço").ativo(true).build(),
            Produto.builder().nome("Cebolinha").descricao("Aromática e saborosa, essencial na cozinha brasileira").preco(new BigDecimal("2.50")).categoria(Produto.Categoria.VERDURAS).estoque(30).unidade("maço").ativo(true).build(),
            Produto.builder().nome("Salsinha").descricao("Tempero fresco, colhido no dia").preco(new BigDecimal("2.00")).categoria(Produto.Categoria.VERDURAS).estoque(25).unidade("maço").ativo(true).build(),
            Produto.builder().nome("Cenoura").descricao("Cenouras grandes e doces, direto da horta").preco(new BigDecimal("4.90")).categoria(Produto.Categoria.LEGUMES).estoque(30).unidade("500g").ativo(true).build(),
            Produto.builder().nome("Tomate Italiano").descricao("Carnudo e adocicado, perfeito para molhos caseiros").preco(new BigDecimal("6.00")).categoria(Produto.Categoria.LEGUMES).estoque(3).unidade("kg").ativo(true).build(),
            Produto.builder().nome("Pimentão Vermelho").descricao("Doce e crocante, colhido no ponto ideal").preco(new BigDecimal("7.50")).categoria(Produto.Categoria.LEGUMES).estoque(12).unidade("unidade").ativo(true).build(),
            Produto.builder().nome("Pepino Caipira").descricao("Crocante e refrescante, ótimo para salada").preco(new BigDecimal("2.50")).categoria(Produto.Categoria.LEGUMES).estoque(0).unidade("unidade").ativo(true).build(),
            Produto.builder().nome("Abobrinha").descricao("Tenra e saborosa, versátil na cozinha").preco(new BigDecimal("3.80")).categoria(Produto.Categoria.LEGUMES).estoque(18).unidade("unidade").ativo(true).build(),
            Produto.builder().nome("Laranja Pera").descricao("Suculenta e adocicada, ótima para suco natural").preco(new BigDecimal("5.50")).categoria(Produto.Categoria.FRUTAS).estoque(25).unidade("kg").ativo(true).build(),
            Produto.builder().nome("Mamão Formosa").descricao("Maduro no ponto, doce e macio").preco(new BigDecimal("8.00")).categoria(Produto.Categoria.FRUTAS).estoque(7).unidade("unidade").ativo(true).build(),
            Produto.builder().nome("Banana Prata").descricao("Doce e nutritiva, perfeita para o café da manhã").preco(new BigDecimal("4.50")).categoria(Produto.Categoria.FRUTAS).estoque(20).unidade("kg").ativo(true).build(),
            Produto.builder().nome("Kit Verde da Semana").descricao("Alface, couve, espinafre, rúcula, cebolinha e salsinha. Tudo fresquinho para a semana toda!").preco(new BigDecimal("35.00")).categoria(Produto.Categoria.KITS).estoque(10).unidade("kit semanal").ativo(true).build(),
            Produto.builder().nome("Kit Salada Completa").descricao("Alface, tomate, pepino, cenoura e rúcula. Ingredientes selecionados para a salada perfeita!").preco(new BigDecimal("28.00")).categoria(Produto.Categoria.KITS).estoque(8).unidade("kit").ativo(true).build(),
            Produto.builder().nome("Kit Família").descricao("Seleção completa de verduras, legumes e frutas para uma semana saudável em família. Rendimento para 4 pessoas.").preco(new BigDecimal("55.00")).categoria(Produto.Categoria.KITS).estoque(5).unidade("kit").ativo(true).build()
        );

        produtoRepository.saveAll(produtos);
        log.info("✅ {} produtos iniciais criados", produtos.size());
    }
}
