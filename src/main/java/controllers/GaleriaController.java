package controllers;

import models.DadosGlobais;
import models.ItemGaleria;
import persistence.PersistenciaGlobal;

import java.util.ArrayList;
import java.util.List;

// Monta a galeria cruzando os finais e extras do perfil global com o catálogo de conteúdos do jogo.
public class GaleriaController {

    public static final String CATEGORIA_FINAIS = "Finais";
    public static final String CATEGORIA_CONQUISTAS = "Conquistas";

    private final PersistenciaGlobal persistencia = new PersistenciaGlobal();

    // Lê o perfil atual do disco e devolve todos os itens, marcando quais estão desbloqueados.
    public List<ItemGaleria> listarItens() {
        return montar(persistencia.carregar());
    }

    // Regra pura (sem disco): relaciona o perfil com o catálogo fixo de finais e conquistas.
    public static List<ItemGaleria> montar(DadosGlobais perfil) {
        List<ItemGaleria> itens = new ArrayList<>();

        itens.add(final_(perfil, "FINAL_TOIN_INDICIADO_BRUNO_VIVO",
                "A verdade que restou", "Toin indiciado, Bruno vivo."));
        itens.add(final_(perfil, "FINAL_TOIN_INDICIADO_BRUNO_MORTO",
                "Depois da fumaça", "Toin indiciado, Bruno morto."));
        itens.add(final_(perfil, "FINAL_KATARINA_INDICIADA_BRUNO_VIVO",
                "Sob suspeita", "Katarina indiciada, Bruno vivo."));
        itens.add(final_(perfil, "FINAL_KATARINA_INDICIADA_BRUNO_MORTO",
                "O peso da noite", "Katarina indiciada, Bruno morto."));

        itens.add(extra(perfil, "FIM?", "O Fim",
                "Você alcançou pelo menos um final. Obrigado por jogar e parabéns por zerar o jogo!"));
        itens.add(extra(perfil, "ISQUEIRO_TOIN", "Isqueiro de Toin",
                "Um objeto que não consta em nenhum registro."));
        itens.add(extra(perfil, "BRINQUEDO_BRUNO", "Brinquedo de Bruno",
                "Era o jogo favorito dele..."));

        return itens;
    }

    // Conta quantos itens de uma categoria já foram desbloqueados.
    public static int contarDesbloqueados(List<ItemGaleria> itens, String categoria) {
        int total = 0;
        for (ItemGaleria item : itens) {
            if (item.getCategoria().equals(categoria) && item.isDesbloqueado()) {
                total++;
            }
        }
        return total;
    }

    public static int contarTotal(List<ItemGaleria> itens, String categoria) {
        int total = 0;
        for (ItemGaleria item : itens) {
            if (item.getCategoria().equals(categoria)) {
                total++;
            }
        }
        return total;
    }

    private static ItemGaleria final_(DadosGlobais perfil, String chave, String titulo, String descricao) {
        boolean ok = perfil.finaisDesbloqueados != null && perfil.finaisDesbloqueados.contains(chave);
        return new ItemGaleria(CATEGORIA_FINAIS, titulo, descricao, ok);
    }

    private static ItemGaleria extra(DadosGlobais perfil, String chave, String titulo, String descricao) {
        boolean ok = perfil.extrasDesbloqueados != null && perfil.extrasDesbloqueados.contains(chave);
        return new ItemGaleria(CATEGORIA_CONQUISTAS, titulo, descricao, ok);
    }
}
