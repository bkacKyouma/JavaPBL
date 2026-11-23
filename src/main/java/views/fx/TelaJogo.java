package views.fx;

import controllers.JogoController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import models.Cena;
import models.Dialogo;
import models.Escolha;
import models.Personagem;
import models.Protagonista;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

// Tela principal: mostra cenário, falas, escolhas, progresso e feedback. As regras ficam no JogoController.
public class TelaJogo {

    private final StackPane camadaFundo = new StackPane();
    private final ImageView imagemCenario = new ImageView();
    private final BorderPane raiz = new BorderPane();
    private final Navegador nav;
    private final JogoController jogo;

    private final VBox conteudo = new VBox(14);
    private final ScrollPane rolagem = new ScrollPane(conteudo);
    private final FlowPane feedback = Componentes.painelFeedback();

    public TelaJogo(Navegador nav) {
        this.nav = nav;
        this.jogo = nav.getJogo();

        imagemCenario.setPreserveRatio(false);
        imagemCenario.fitWidthProperty().bind(camadaFundo.widthProperty());
        imagemCenario.fitHeightProperty().bind(camadaFundo.heightProperty());

        conteudo.setPadding(new Insets(10, 40, 20, 40));
        conteudo.setStyle("-fx-background-color: transparent;");

        rolagem.setFitToWidth(true);
        rolagem.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        raiz.setTop(criarBarraSuperior());
        raiz.setCenter(rolagem);

        VBox rodape = new VBox(feedback);
        rodape.setPadding(new Insets(0, 40, 14, 40));
        raiz.setBottom(rodape);

        camadaFundo.getChildren().addAll(imagemCenario, raiz);

        atualizarCena(new ArrayList<>());
    }

    // Barra com capítulo, indicador de progresso e acesso ao status, aos saves e ao menu.
    private HBox criarBarraSuperior() {
        int capitulo = jogo.getCapituloAtual().getId();
        int total = jogo.getTotalCapitulos();

        Label titulo = Componentes.rotulo(
                "Capítulo " + capitulo + " — " + jogo.getCapituloAtual().getTitulo(), "secao");
        titulo.setWrapText(false);

        ProgressBar progresso = new ProgressBar((double) capitulo / total);
        progresso.setPrefWidth(100);
        progresso.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        Label textoProgresso = Componentes.rotulo(capitulo + "/" + total, "suave");

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        Button status = Componentes.botao("Status", "botao-pequeno", nav::mostrarStatus);
        Button salvar = Componentes.botao("Salvar", "botao-pequeno",
                () -> nav.mostrarSaves(nav::mostrarJogo));
        salvar.setDisable(!jogo.podeSalvar());
        Button reiniciar = Componentes.botao("Reiniciar", "botao-pequeno", this::reiniciar);
        Button menu = Componentes.botao("Menu", "botao-pequeno", this::voltarAoMenu);

        // Só o título cede espaço; botões e progresso mantêm o tamanho natural.
        titulo.setMinWidth(0);
        for (javafx.scene.control.Control c : new javafx.scene.control.Control[]{
                textoProgresso, status, salvar, reiniciar, menu}) {
            c.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        }
        textoProgresso.setWrapText(false);

        HBox barra = new HBox(12, titulo, espaco, progresso, textoProgresso, status, salvar, reiniciar, menu);
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setPadding(new Insets(14, 40, 10, 40));
        return barra;
    }

    // Recomeça a história com o mesmo temperamento, depois de confirmar.
    private void reiniciar() {
        if (nav.confirmar("Reiniciar partida",
                "A história recomeça do início e o progresso não salvo será perdido. Reiniciar?")) {
            jogo.reiniciarPartida();
            nav.mostrarJogo("Partida reiniciada.");
        }
    }

    private void voltarAoMenu() {
        if (jogo.isJogoEncerrado()
                || nav.confirmar("Voltar ao menu", "Mudanças não salvas serão perdidas. Voltar ao menu?")) {
            nav.voltarAoMenu();
        }
    }

    private void atualizarImagemCena(Cena cena) {
        imagemCenario.setImage(null);
        if (cena == null || cena.getImagem() == null || cena.getImagem().isBlank()) {
            return;
        }
        Image img = carregarImagem("/cenario/" + cena.getImagem().trim());
        if (img != null) {
            imagemCenario.setImage(img);
        }
    }

    private ImageView criarSpritePersonagem(Dialogo dialogo) {
        if (dialogo == null || dialogo.getPersonagem() == null || dialogo.getPersonagem().isBlank()) {
            return null;
        }
        Personagem personagem = jogo.buscarPersonagemPorNome(dialogo.getPersonagem());
        if (personagem == null) {
            return null;
        }
        String arquivoSprite = personagem.getSprite(dialogo.getHumor());
        if (arquivoSprite == null || arquivoSprite.isBlank()) {
            return null;
        }
        Image img = carregarImagem("/personagens/" + arquivoSprite.trim());
        if (img == null) {
            return null;
        }
        ImageView view = new ImageView(img);
        view.setPreserveRatio(true);
        view.setFitHeight(96);
        return view;
    }

    private ImageView criarSpriteProtagonista(Cena cena) {
        if (cena == null || cena.getHumorProtagonista() == null || cena.getHumorProtagonista().isBlank()) {
            return null;
        }
        Protagonista protagonista = jogo.getProtagonista();
        if (protagonista == null) {
            return null;
        }
        String arquivoSprite = protagonista.getSprite(cena.getHumorProtagonista());
        if (arquivoSprite == null || arquivoSprite.isBlank()) {
            return null;
        }
        Image img = carregarImagem("/personagens/" + arquivoSprite.trim());
        if (img == null) {
            return null;
        }
        ImageView view = new ImageView(img);
        view.setPreserveRatio(true);
        view.setFitHeight(120);
        return view;
    }

    private Image carregarImagem(String caminhoClasspath) {
        try (InputStream is = getClass().getResourceAsStream(caminhoClasspath)) {
            if (is == null) {
                return null;
            }
            return new Image(is);
        } catch (Exception e) {
            return null;
        }
    }

    // Reconstrói o conteúdo central com a cena atual e mostra o feedback da última ação.
    private void atualizarCena(List<String> mensagens) {
        jogo.prepararCenaAtual();
        Cena cena = jogo.getCenaAtual();
        List<Escolha> escolhas = jogo.getEscolhasDisponiveis();

        atualizarImagemCena(cena);
        conteudo.getChildren().clear();

        Label selo = new Label(nomeDoTipo(cena.getTipo()));
        selo.getStyleClass().add("selo");
        conteudo.getChildren().add(selo);
        conteudo.getChildren().add(Componentes.rotulo(cena.getTitulo(), "cena-titulo"));

        if (cena.getTextoNarrativa() != null) {
            conteudo.getChildren().add(Componentes.rotulo(cena.getTextoNarrativa(), "narrativa"));
        }

        for (Dialogo dialogo : cena.getDialogos()) {
            ImageView sprite = criarSpritePersonagem(dialogo);
            boolean semFala = dialogo.getConteudo() == null || dialogo.getConteudo().isBlank();

            if (semFala) {
                if (sprite != null) {
                    conteudo.getChildren().add(sprite);
                }
                continue;
            }

            VBox balao = new VBox(4);
            balao.getStyleClass().add("balao");
            if (dialogo.getPersonagem() != null) {
                balao.getChildren().add(Componentes.rotulo(dialogo.getPersonagem(), "falante"));
            }
            balao.getChildren().add(Componentes.rotulo(dialogo.getConteudo(), "fala"));

            if (sprite != null) {
                HBox linhaDialogo = new HBox(12, sprite, balao);
                linhaDialogo.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(balao, Priority.ALWAYS);
                conteudo.getChildren().add(linhaDialogo);
            } else {
                conteudo.getChildren().add(balao);
            }
        }

        conteudo.getChildren().add(Componentes.divisor());

        VBox caixaAcoes = new VBox(10);
        if (escolhas.isEmpty()) {
            if (jogo.isJogoEncerrado()) {
                caixaAcoes.getChildren().add(Componentes.rotulo("Fim da investigação.", "secao"));
                caixaAcoes.getChildren().add(
                        Componentes.botao("Voltar ao menu", "botao-menu", nav::voltarAoMenu));
            } else {
                caixaAcoes.getChildren().add(Componentes.botao("Continuar ▶", "botao-menu", this::continuar));
            }
        } else {
            for (int i = 0; i < escolhas.size(); i++) {
                final int indice = i;
                Button botao = Componentes.botao(
                        escolhas.get(i).getTextoOpcao(), "botao-escolha", () -> escolher(indice));
                botao.setWrapText(true);
                botao.setMaxWidth(Double.MAX_VALUE);
                caixaAcoes.getChildren().add(botao);
            }
        }

        ImageView spriteProtagonista = criarSpriteProtagonista(cena);
        if (spriteProtagonista != null) {
            HBox blocoAcoes = new HBox(14, spriteProtagonista, caixaAcoes);
            blocoAcoes.setAlignment(Pos.TOP_LEFT);
            HBox.setHgrow(caixaAcoes, Priority.ALWAYS);
            conteudo.getChildren().add(blocoAcoes);
        } else {
            conteudo.getChildren().add(caixaAcoes);
        }

        Componentes.preencherFeedback(feedback, mensagens);
        rolagem.setVvalue(0);
    }

    private void escolher(int indice) {
        try {
            List<String> mensagens = jogo.escolherOpcao(indice);
            recarregar(mensagens);
        } catch (RuntimeException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void continuar() {
        try {
            jogo.avancarCena();
            recarregar(new ArrayList<>());
        } catch (RuntimeException e) {
            mostrarErro(e.getMessage());
        }
    }

    // O capítulo pode mudar com a cena, então a tela inteira (incluindo a barra superior) é refeita.
    private void recarregar(List<String> mensagens) {
        raiz.setTop(criarBarraSuperior());
        atualizarCena(mensagens);
    }

    public void mostrarMensagem(String mensagem) {
        if (mensagem != null) {
            feedback.getChildren().add(Componentes.chip(mensagem));
        }
    }

    private void mostrarErro(String mensagem) {
        feedback.getChildren().add(Componentes.chipErro(mensagem));
    }

    private String nomeDoTipo(String tipo) {
        if ("INTERROGATORIO".equals(tipo)) return "INTERROGATÓRIO";
        if ("FLASHBACK".equals(tipo)) return "FLASHBACK";
        if ("MEMORIA".equals(tipo)) return "MEMÓRIA";
        if ("FINAL".equals(tipo)) return "FINAL";
        return "NARRATIVA";
    }

    public Parent getRaiz() {
        return camadaFundo;
    }
}