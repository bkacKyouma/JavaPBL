package views.fx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.InputStream;

// Tela inicial: título, identidade visual e acesso a todos os fluxos do jogo.
public class TelaInicial {

    private final StackPane camadaFundo = new StackPane();
    private final ImageView imagemMenu = new ImageView();
    private final VBox raiz = new VBox(8);
    private final Label aviso = new Label();

    public TelaInicial(Navegador nav) {
        imagemMenu.setPreserveRatio(false);
        imagemMenu.fitWidthProperty().bind(camadaFundo.widthProperty());
        imagemMenu.fitHeightProperty().bind(camadaFundo.heightProperty());
        carregarFundoMenu();

        raiz.setAlignment(Pos.CENTER);
        raiz.setPadding(new Insets(16));
        raiz.setStyle("-fx-background-color: transparent;");

        Label titulo = Componentes.rotulo("A NOITE QUE\nNÃO SE APAGA", "titulo");
        titulo.setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        Label subtitulo = Componentes.rotulo("um jogo narrativo de investigação", "subtitulo");

        Button novo = Componentes.botao("Nova partida", "botao-menu", nav::mostrarCriacao);

        Button continuar = Componentes.botao("Continuar", "botao-menu", () -> continuar(nav));
        continuar.setDisable(nav.getJogo().slotMaisRecente() == 0);

        Button carregar = Componentes.botao("Carregar partida", "botao-menu",
                () -> nav.mostrarSaves(nav::mostrarMenu));
        Button galeria = Componentes.botao("Galeria", "botao-menu", nav::mostrarGaleria);
        Button instrucoes = Componentes.botao("Instruções", "botao-menu", nav::mostrarInstrucoes);
        Button config = Componentes.botao("Configurações", "botao-menu", nav::mostrarConfig);
        Button creditos = Componentes.botao("Créditos", "botao-menu", nav::mostrarCreditos);
        Button sair = Componentes.botao("Sair", "botao-menu", () -> {
            if (nav.confirmar("Sair do jogo", "Deseja realmente sair?")) {
                nav.sair();
            }
        });

        aviso.getStyleClass().addAll("chip", "chip-erro");
        aviso.setVisible(false);

        raiz.getChildren().addAll(
                titulo, subtitulo, Componentes.divisor(),
                novo, continuar, carregar, galeria, instrucoes, config, creditos, sair, aviso);
        VBox.setMargin(subtitulo, new Insets(0, 0, 6, 0));

        camadaFundo.getChildren().addAll(imagemMenu, raiz);
    }

    private void carregarFundoMenu() {
        try (InputStream is = getClass().getResourceAsStream("/cenario/tela_menu.jpg")) {
            if (is != null) {
                imagemMenu.setImage(new Image(is));
            }
        } catch (Exception ignorado) {
            // Mantém o fundo padrão do CSS se a imagem não estiver disponível.
        }
    }

    // Retoma o save mais recente (inclui o auto save).
    private void continuar(Navegador nav) {
        int slot = nav.getJogo().slotMaisRecente();
        try {
            nav.getJogo().carregarPartida(slot);
            nav.mostrarJogo();
        } catch (IOException e) {
            aviso.setText("Erro: " + e.getMessage());
            aviso.setVisible(true);
        }
    }

    public Parent getRaiz() {
        return camadaFundo;
    }
}