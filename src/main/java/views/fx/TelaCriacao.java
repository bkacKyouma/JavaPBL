package views.fx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

// Tela de criação: o jogador escolhe o temperamento de Katarina, que afeta escolhas e consequências.
public class TelaCriacao {

    private final VBox raiz = new VBox(18);

    public TelaCriacao(Navegador nav) {
        raiz.setPadding(new Insets(40));
        raiz.setAlignment(Pos.TOP_CENTER);

        ToggleGroup grupo = new ToggleGroup();
        ToggleButton frio = carta(grupo, 1, "Sangue frio",
                "Calculista e contida. Mantém a voz firme mesmo quando tudo desmorona.");
        ToggleButton equilibrado = carta(grupo, 2, "Equilíbrio",
                "Pondera antes de reagir. Tenta manter a cabeça no lugar.");
        ToggleButton quente = carta(grupo, 3, "Sangue quente",
                "Reage no impulso. Diz o que sente, mesmo quando isso custa caro.");
        equilibrado.setSelected(true);

        HBox cartas = new HBox(16, frio, equilibrado, quente);
        cartas.setAlignment(Pos.CENTER);

        Button comecar = Componentes.botao("Começar investigação", "botao-menu", () -> {
            ToggleButton escolhido = (ToggleButton) grupo.getSelectedToggle();
            nav.getJogo().prepararNovaPartida((Integer) escolhido.getUserData());
            nav.mostrarJogo();
        });
        Button voltar = Componentes.botao("Voltar", "botao-pequeno", nav::mostrarMenu);

        raiz.getChildren().addAll(
                Componentes.rotulo("TEMPERAMENTO DE KATARINA", "tela-titulo"),
                Componentes.rotulo("Escolha como Katarina costuma reagir diante de situações de pressão. "
                        + "Isso muda o peso emocional das suas respostas durante o interrogatório.", "suave"),
                Componentes.divisor(),
                cartas, comecar, voltar);
    }

    // Cada carta é um ToggleButton; o número da opção fica em userData (1, 2 ou 3, como no modo terminal).
    private ToggleButton carta(ToggleGroup grupo, int opcao, String nome, String descricao) {
        ToggleButton carta = new ToggleButton(nome + "\n\n" + descricao);
        carta.getStyleClass().add("carta");
        carta.setWrapText(true);
        carta.setToggleGroup(grupo);
        carta.setUserData(opcao);
        carta.setPrefWidth(270);
        carta.setPrefHeight(200);
        HBox.setHgrow(carta, Priority.ALWAYS);

        // Impede que clicar na carta já selecionada deixe o grupo sem seleção.
        carta.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, e -> {
            if (carta.isSelected()) e.consume();
        });
        return carta;
    }

    public Parent getRaiz() {
        return raiz;
    }
}
