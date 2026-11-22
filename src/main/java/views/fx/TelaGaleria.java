package views.fx;

import controllers.GaleriaController;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import models.ItemGaleria;

import java.util.List;

// Galeria: mostra finais e conquistas já desbloqueados; o que falta aparece bloqueado como "???".
public class TelaGaleria {

    private final VBox raiz = new VBox(14);

    public TelaGaleria(Navegador nav) {
        List<ItemGaleria> itens = new GaleriaController().listarItens();

        VBox conteudo = new VBox(10);
        conteudo.setPadding(new Insets(0, 10, 10, 0));

        adicionarSecao(conteudo, itens, GaleriaController.CATEGORIA_FINAIS);
        adicionarSecao(conteudo, itens, GaleriaController.CATEGORIA_CONQUISTAS);

        ScrollPane rolagem = new ScrollPane(conteudo);
        rolagem.setFitToWidth(true);
        VBox.setVgrow(rolagem, Priority.ALWAYS);

        raiz.setPadding(new Insets(30, 80, 24, 80));
        raiz.getChildren().addAll(
                Componentes.rotulo("GALERIA", "tela-titulo"),
                Componentes.divisor(),
                rolagem,
                Componentes.botao("Voltar", "botao-pequeno", nav::mostrarMenu));
    }

    private void adicionarSecao(VBox conteudo, List<ItemGaleria> itens, String categoria) {
        int ok = GaleriaController.contarDesbloqueados(itens, categoria);
        int total = GaleriaController.contarTotal(itens, categoria);
        conteudo.getChildren().add(Componentes.rotulo(categoria + " (" + ok + "/" + total + ")", "secao"));

        for (ItemGaleria item : itens) {
            if (!item.getCategoria().equals(categoria)) {
                continue;
            }

            VBox cartao = new VBox(3);
            cartao.getStyleClass().add("balao");

            if (item.isDesbloqueado()) {
                cartao.getChildren().add(Componentes.rotulo(item.getTitulo(), "falante"));
                cartao.getChildren().add(Componentes.rotulo(item.getDescricao(), "fala"));
            } else {
                cartao.getChildren().add(Componentes.rotulo("???", "suave"));
                cartao.getChildren().add(Componentes.rotulo("Bloqueado", "suave"));
            }
            conteudo.getChildren().add(cartao);
        }
    }

    public Parent getRaiz() {
        return raiz;
    }
}
