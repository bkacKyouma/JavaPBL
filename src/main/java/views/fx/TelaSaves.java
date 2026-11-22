package views.fx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import controllers.JogoController;

import java.io.IOException;
import java.util.Optional;

// Gerenciamento de saves: salvar (com confirmação de sobrescrita), carregar e excluir (com confirmação).
public class TelaSaves {

    private final VBox raiz = new VBox(14);
    private final VBox listaSlots = new VBox(10);
    private final FlowPane feedback = Componentes.painelFeedback();
    private final Navegador nav;
    private final JogoController jogo;
    private final Runnable aoVoltar;

    public TelaSaves(Navegador nav, Runnable aoVoltar) {
        this.nav = nav;
        this.jogo = nav.getJogo();
        this.aoVoltar = aoVoltar;

        raiz.setPadding(new Insets(40, 80, 30, 80));
        raiz.getChildren().addAll(
                Componentes.rotulo("SALVAR / CARREGAR PARTIDA", "tela-titulo"),
                Componentes.divisor(),
                listaSlots,
                feedback,
                Componentes.botao("Voltar", "botao-pequeno", aoVoltar));

        atualizarLista();
    }

    // Redesenha os slots lendo os metadados (apelido, data/hora e progresso) direto da persistência.
    private void atualizarLista() {
        listaSlots.getChildren().clear();
        String[] saves = jogo.listarSaves();

        for (int i = 0; i < saves.length; i++) {
            final int slot = i + 1;
            boolean ocupado = !saves[i].equals("Vazio");

            Label nome = Componentes.rotulo("Slot " + slot, "secao");
            Label detalhe = Componentes.rotulo(ocupado ? saves[i] : "Vazio", ocupado ? "fala" : "suave");
            VBox texto = new VBox(3, nome, detalhe);
            HBox.setHgrow(texto, Priority.ALWAYS);

            Button salvar = Componentes.botao("Salvar", "botao-pequeno", () -> salvar(slot));
            salvar.setDisable(!jogo.podeSalvar());
            Button carregar = Componentes.botao("Carregar", "botao-pequeno", () -> carregar(slot));
            carregar.setDisable(!ocupado);
            Button excluir = Componentes.botao("Excluir", "botao-pequeno", () -> excluir(slot));
            excluir.setDisable(!ocupado);

            // Os botões nunca encolhem; quem cede espaço é o texto do save.
            salvar.setMinWidth(Region.USE_PREF_SIZE);
            carregar.setMinWidth(Region.USE_PREF_SIZE);
            excluir.setMinWidth(Region.USE_PREF_SIZE);
            texto.setMinWidth(0);

            HBox linha = new HBox(10, texto, salvar, carregar, excluir);
            linha.setAlignment(Pos.CENTER_LEFT);
            linha.getStyleClass().add("slot");
            listaSlots.getChildren().add(linha);
        }
    }

    private void salvar(int slot) {
        if (jogo.slotOcupado(slot)
                && !nav.confirmar("Sobrescrever save", "O slot " + slot + " já possui um save. Substituir?")) {
            return;
        }

        TextInputDialog dialogo = new TextInputDialog("Katarina");
        dialogo.setTitle("Salvar partida");
        dialogo.setHeaderText("Informe o apelido do save");
        dialogo.setContentText("Apelido:");
        dialogo.getDialogPane().getStylesheets().addAll(raiz.getScene().getStylesheets());
        ((Button) dialogo.getDialogPane().lookupButton(ButtonType.OK)).setText("Salvar");
        ((Button) dialogo.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Cancelar");
        Optional<String> apelido = dialogo.showAndWait();
        if (!apelido.isPresent()) {
            return;
        }

        try {
            jogo.salvarPartida(slot, apelido.get());
            mostrar(Componentes.chip("Partida salva no slot " + slot + "."));
        } catch (IOException e) {
            mostrar(Componentes.chipErro(e.getMessage()));
        }
        atualizarLista();
    }

    private void carregar(int slot) {
        if (jogo.podeSalvar()
                && !nav.confirmar("Carregar partida", "O progresso não salvo da partida atual será perdido. Continuar?")) {
            return;
        }

        try {
            jogo.carregarPartida(slot);
            nav.mostrarJogo("Partida carregada do slot " + slot + ".");
        } catch (IOException e) {
            mostrar(Componentes.chipErro(e.getMessage()));
        }
    }

    private void excluir(int slot) {
        if (!nav.confirmar("Excluir save", "Tem certeza de que deseja excluir o slot " + slot + "?")) {
            return;
        }

        try {
            jogo.excluirSave(slot);
            mostrar(Componentes.chip("Slot " + slot + " excluído."));
        } catch (IOException e) {
            mostrar(Componentes.chipErro(e.getMessage()));
        }
        atualizarLista();
    }

    private void mostrar(Label chip) {
        feedback.getChildren().clear();
        feedback.getChildren().add(chip);
    }

    public Parent getRaiz() {
        return raiz;
    }
}
