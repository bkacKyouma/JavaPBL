package views.fx;

import controllers.ConfigController;
import controllers.JogoController;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

// Controla qual tela está visível e oferece diálogos de confirmação e erro para todas as telas.
public class Navegador {

    private final Stage stage;
    private final Scene scene;
    private final ConfigController config = ConfigController.getInstance();
    private final JogoController jogo;

    public Navegador(Stage stage) {
        this.stage = stage;
        this.jogo = new JogoController(
                config.getCorPrimariaAnsi(),
                config.getCorSecundariaAnsi(),
                config.getModo());

        this.scene = new Scene(new javafx.scene.layout.Pane(), 1000, 680);
        this.scene.getStylesheets().add(getClass().getResource("/estilo.css").toExternalForm());

        // Observer: quando uma preferência muda, o tema da tela atual é reaplicado na hora.
        config.adicionarObservador(() -> aplicarTema(scene.getRoot()));

        // Fechar a janela no meio de uma partida pede confirmação.
        stage.setOnCloseRequest(evento -> {
            if (jogo.podeSalvar()
                    && !confirmar("Sair do jogo", "Há uma partida em andamento. Sair sem salvar?")) {
                evento.consume();
            }
        });

        stage.setScene(scene);
        stage.setTitle("A Noite Que Não Se Apaga");
        stage.setMinWidth(820);
        stage.setMinHeight(600);
    }

    public JogoController getJogo() {
        return jogo;
    }

    public ConfigController getConfig() {
        return config;
    }

    // Troca a tela atual aplicando o tema (cor de destaque, negrito e itálico) do perfil.
    private void mostrar(Parent raiz) {
        aplicarTema(raiz);
        scene.setRoot(raiz);
    }

    public void aplicarTema(Parent raiz) {
        String estilo = "-destaque: " + config.getCorHex() + ";";
        if (config.isNegrito()) estilo += " -fx-font-weight: bold;";
        if (config.isItalico()) estilo += " -fx-font-style: italic;";
        raiz.setStyle(estilo);
    }

    public void mostrarMenu() {
        mostrar(new TelaInicial(this).getRaiz());
    }

    public void mostrarCriacao() {
        mostrar(new TelaCriacao(this).getRaiz());
    }

    public void mostrarJogo() {
        mostrar(new TelaJogo(this).getRaiz());
    }

    public void mostrarJogo(String mensagemInicial) {
        TelaJogo tela = new TelaJogo(this);
        tela.mostrarMensagem(mensagemInicial);
        mostrar(tela.getRaiz());
    }

    public void mostrarStatus() {
        mostrar(new TelaStatus(this).getRaiz());
    }

    public void mostrarSaves(Runnable aoVoltar) {
        mostrar(new TelaSaves(this, aoVoltar).getRaiz());
    }

    public void mostrarGaleria() {
        mostrar(new TelaGaleria(this).getRaiz());
    }

    public void mostrarConfig() {
        mostrar(new TelaConfig(this).getRaiz());
    }

    public void mostrarInstrucoes() {
        mostrar(new TelaTexto(this, "INSTRUÇÕES", TelaTexto.INSTRUCOES).getRaiz());
    }

    public void mostrarCreditos() {
        mostrar(new TelaTexto(this, "CRÉDITOS", TelaTexto.CREDITOS).getRaiz());
    }

    // Volta ao menu descartando a partida em andamento (as telas já pediram confirmação quando necessário).
    public void voltarAoMenu() {
        jogo.encerrarSessao();
        mostrarMenu();
    }

    public void sair() {
        Platform.exit();
    }

    // Exibe uma janela Sim/Não e devolve true somente se o jogador confirmar.
    public boolean confirmar(String titulo, String mensagem) {
        ButtonType sim = new ButtonType("Sim", javafx.scene.control.ButtonBar.ButtonData.YES);
        ButtonType nao = new ButtonType("Não", javafx.scene.control.ButtonBar.ButtonData.NO);
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensagem, sim, nao);
        alerta.setTitle(titulo);
        alerta.setHeaderText(titulo);
        alerta.initOwner(stage);
        alerta.getDialogPane().setMinWidth(480);
        alerta.getDialogPane().setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        alerta.getDialogPane().getStylesheets().addAll(scene.getStylesheets());

        Optional<ButtonType> resposta = alerta.showAndWait();
        return resposta.isPresent() && resposta.get() == sim;
    }
}
