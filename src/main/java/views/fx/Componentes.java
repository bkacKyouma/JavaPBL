package views.fx;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

import views.fx.feedback.EstrategiaFeedback;
import views.fx.feedback.SeletorFeedback;

import java.util.List;

// Fábrica de pequenos elementos visuais repetidos nas telas (botões, títulos e chips de feedback).
public class Componentes {

    private Componentes() {
    }

    public static Button botao(String texto, String classe, Runnable acao) {
        Button botao = new Button(texto);
        botao.getStyleClass().add(classe);
        botao.setOnAction(e -> acao.run());
        return botao;
    }

    public static Label rotulo(String texto, String classe) {
        Label label = new Label(texto);
        label.getStyleClass().add(classe);
        label.setWrapText(true);
        return label;
    }

    public static Region divisor() {
        Region linha = new Region();
        linha.getStyleClass().add("divisor");
        return linha;
    }

    // Cria uma etiqueta usando a estratégia de feedback que reconhece a mensagem.
    public static Label chip(String mensagem) {
        EstrategiaFeedback estrategia = SeletorFeedback.escolher(mensagem);

        Label chip = new Label(estrategia.formatar(mensagem));
        chip.getStyleClass().addAll("chip", estrategia.classeCss());
        return chip;
    }

    public static Label chipErro(String mensagem) {
        Label chip = new Label("Erro: " + mensagem);
        chip.getStyleClass().addAll("chip", "chip-erro");
        return chip;
    }

    // Painel horizontal que quebra linha, usado para mostrar as mudanças causadas por uma escolha.
    public static FlowPane painelFeedback() {
        FlowPane painel = new FlowPane(8, 6);
        painel.setPadding(new Insets(8, 0, 0, 0));
        return painel;
    }

    public static void preencherFeedback(FlowPane painel, List<String> mensagens) {
        painel.getChildren().clear();
        for (String mensagem : mensagens) {
            painel.getChildren().add(chip(mensagem));
        }
    }
}
