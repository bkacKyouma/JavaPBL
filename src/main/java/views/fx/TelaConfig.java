package views.fx;

import controllers.ConfigController;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

// Tela de configurações: cor de destaque, negrito e itálico, salvos no perfil entre execuções.
public class TelaConfig {

    private final VBox raiz = new VBox(16);
    private final Navegador nav;
    private final Label aviso = new Label();

    public TelaConfig(Navegador nav) {
        this.nav = nav;
        ConfigController config = nav.getConfig();
        raiz.setPadding(new Insets(40, 80, 30, 80));

        ToggleGroup cores = new ToggleGroup();
        HBox linhaCores = new HBox(10,
                corToggle(cores, "Verde", ConfigController.ANSI_VERDE),
                corToggle(cores, "Ciano", ConfigController.ANSI_CIANO),
                corToggle(cores, "Amarelo", ConfigController.ANSI_AMARELO),
                corToggle(cores, "Branco", ConfigController.ANSI_BRANCO));

        for (javafx.scene.Node no : linhaCores.getChildren()) {
            ToggleButton botao = (ToggleButton) no;
            if (botao.getUserData().equals(config.getCorPrimariaAnsi())) {
                botao.setSelected(true);
            }
        }

        CheckBox negrito = new CheckBox("Texto em negrito");
        negrito.setSelected(config.isNegrito());
        negrito.setOnAction(e -> {
            config.setNegrito(negrito.isSelected());
            salvarEAplicar();
        });

        CheckBox italico = new CheckBox("Texto em itálico");
        italico.setSelected(config.isItalico());
        italico.setOnAction(e -> {
            config.setItalico(italico.isSelected());
            salvarEAplicar();
        });

        aviso.getStyleClass().addAll("chip", "chip-erro");
        aviso.setVisible(false);

        raiz.getChildren().addAll(
                Componentes.rotulo("CONFIGURAÇÕES", "tela-titulo"),
                Componentes.divisor(),
                Componentes.rotulo("Cor de destaque", "secao"), linhaCores,
                Componentes.rotulo("Estilo do texto", "secao"), negrito, italico,
                aviso,
                Componentes.botao("Voltar", "botao-pequeno", nav::mostrarMenu));
    }

    private ToggleButton corToggle(ToggleGroup grupo, String nome, String ansi) {
        ToggleButton botao = new ToggleButton(nome);
        botao.getStyleClass().add("carta");
        botao.setToggleGroup(grupo);
        botao.setUserData(ansi);
        botao.setStyle("-fx-text-fill: " + ConfigController.corParaHex(ansi) + ";");
        botao.setOnAction(e -> {
            if (botao.isSelected()) {
                nav.getConfig().setCor(ansi);
                salvarEAplicar();
            } else {
                botao.setSelected(true);
            }
        });
        return botao;
    }

    // Grava o perfil e reaplica o tema na hora, para o jogador ver o efeito da mudança.
    private void salvarEAplicar() {
        try {
            nav.getConfig().salvar();
            aviso.setVisible(false);
        } catch (IOException e) {
            aviso.setText("Erro: não foi possível salvar as preferências.");
            aviso.setVisible(true);
        }
    }

    public Parent getRaiz() {
        return raiz;
    }
}
