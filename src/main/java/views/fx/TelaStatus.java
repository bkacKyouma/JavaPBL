package views.fx;

import controllers.JogoController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import models.Pista;
import models.Protagonista;

// Tela de status: atributos, relacionamentos, inventário de pistas e decisões relevantes de Katarina.
public class TelaStatus {

    private final VBox raiz = new VBox(14);

    public TelaStatus(Navegador nav) {
        JogoController jogo = nav.getJogo();
        Protagonista k = jogo.getProtagonista();

        VBox conteudo = new VBox(14);
        conteudo.setPadding(new Insets(0, 10, 10, 0));

        conteudo.getChildren().add(secao("Atributos",
                barra("Memória", k.getMemoria()),
                barra("Estabilidade emocional", k.getEstabilidadeEmocional()),
                barra("Culpa percebida", k.getCulpaPercebida())));

        conteudo.getChildren().add(secao("Relacionamentos",
                barra("Confiança em Fauan", k.getConfiancaFauan()),
                barra("Vínculo com Passos", k.getVinculoPassos()),
                barra("Suspeita sobre Toin", k.getSuspeitaToin())));

        VBox pistas = secao("Inventário de pistas");
        if (k.getPistas().isEmpty()) {
            pistas.getChildren().add(Componentes.rotulo("Nenhuma pista encontrada ainda.", "suave"));
        }
        for (Pista pista : k.getPistas()) {
            VBox item = new VBox(2,
                    Componentes.rotulo(pista.getNome(), "falante"),
                    Componentes.rotulo(pista.getDescricao(), "fala"));
            item.getStyleClass().add("balao");
            pistas.getChildren().add(item);
        }
        conteudo.getChildren().add(pistas);

        conteudo.getChildren().add(secao("Decisões relevantes",
                Componentes.rotulo("Temperamento: " + nomeTemperamento(k.getTemperamento()), "fala"),
                Componentes.rotulo("Postura sobre a paternidade de Bruno: "
                        + nomePostura(k.getPosturaPaternidade()), "fala")));

        conteudo.getChildren().add(secao("Objetivo atual",
                Componentes.rotulo("Descobrir quem desligou o alarme e o que realmente "
                        + "aconteceu na noite do incêndio.", "fala")));

        ScrollPane rolagem = new ScrollPane(conteudo);
        rolagem.setFitToWidth(true);
        VBox.setVgrow(rolagem, Priority.ALWAYS);

        raiz.setPadding(new Insets(30, 80, 24, 80));
        raiz.getChildren().addAll(
                Componentes.rotulo("STATUS DE " + k.getNome().toUpperCase(), "tela-titulo"),
                Componentes.divisor(),
                rolagem,
                Componentes.botao("Voltar ao jogo", "botao-pequeno", nav::mostrarJogo));
    }

    private VBox secao(String titulo, javafx.scene.Node... filhos) {
        VBox caixa = new VBox(8);
        caixa.getStyleClass().add("painel");
        caixa.getChildren().add(Componentes.rotulo(titulo, "secao"));
        caixa.getChildren().addAll(filhos);
        return caixa;
    }

    // Linha com nome, barra proporcional (0 a 100) e o valor numérico.
    private HBox barra(String nome, int valor) {
        Label rotulo = Componentes.rotulo(nome, "fala");
        rotulo.setMinWidth(200);

        ProgressBar barra = new ProgressBar(valor / 100.0);
        barra.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(barra, Priority.ALWAYS);

        Label numero = Componentes.rotulo(String.valueOf(valor), "suave");
        numero.setMinWidth(30);

        HBox linha = new HBox(12, rotulo, barra, numero);
        linha.setAlignment(Pos.CENTER_LEFT);
        return linha;
    }

    private String nomeTemperamento(String temperamento) {
        if ("SANGUE_FRIO".equals(temperamento)) return "Sangue frio";
        if ("SANGUE_QUENTE".equals(temperamento)) return "Sangue quente";
        return "Equilíbrio";
    }

    private String nomePostura(String postura) {
        if ("NEGACAO_TOTAL".equals(postura)) return "Negação total";
        if ("DUVIDA_ABERTA".equals(postura)) return "Dúvida aberta";
        return "Ainda não definida";
    }

    public Parent getRaiz() {
        return raiz;
    }
}
