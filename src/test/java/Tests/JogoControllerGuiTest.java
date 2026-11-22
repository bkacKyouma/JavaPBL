package controllers;

import models.Cena;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import persistence.PersistenciaJogo;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Testa a API que a interface JavaFX usa para jogar, salvar e carregar, sem abrir nenhuma janela.
class JogoControllerGuiTest {

    @TempDir
    Path pastaTemporaria;

    private JogoController controller;

    // Redireciona os saves do controller para uma pasta temporária, preservando os saves reais do projeto.
    @BeforeEach
    void prepararController() throws Exception {
        controller = new JogoController("", "", "");

        PersistenciaJogo isolada = new PersistenciaJogo();
        Field pasta = PersistenciaJogo.class.getDeclaredField("pasta");
        pasta.setAccessible(true);
        pasta.set(isolada, pastaTemporaria.resolve("saves-teste"));

        Field campo = JogoController.class.getDeclaredField("persistencia");
        campo.setAccessible(true);
        campo.set(controller, isolada);
    }

    @Test
    void semPartidaNaoPodeSalvar() {
        assertFalse(controller.temPartida());
        assertFalse(controller.podeSalvar());
    }

    @Test
    void novaPartidaComecaNaPrimeiraCenaDoCapituloUm() {
        controller.prepararNovaPartida(3);

        assertTrue(controller.temPartida());
        assertTrue(controller.podeSalvar());
        assertEquals(101, controller.getCenaAtual().getId());
        assertEquals(1, controller.getCapituloAtual().getId());
        assertEquals(5, controller.getTotalCapitulos());
        assertEquals("SANGUE_QUENTE", controller.getProtagonista().getTemperamento());
    }

    @Test
    void escolherOpcaoAplicaEfeitosEDevolveFeedback() {
        controller.prepararNovaPartida(2);

        List<String> mensagens = controller.escolherOpcao(0);

        assertTrue(mensagens.contains("Memória +15"));
        assertTrue(mensagens.contains("Culpa percebida +10"));
        assertEquals(15, controller.getProtagonista().getMemoria());
        assertEquals(111, controller.getCenaAtual().getId());
    }

    @Test
    void escolhaInvalidaLancaExcecao() {
        controller.prepararNovaPartida(2);

        assertThrows(IllegalArgumentException.class, () -> controller.escolherOpcao(99));
    }

    @Test
    void escolherOpcaoGravaAutoSaveNoSlotUm() {
        controller.prepararNovaPartida(2);

        List<String> mensagens = controller.escolherOpcao(0);

        assertTrue(controller.slotOcupado(1));
        assertTrue(mensagens.stream().anyMatch(m -> m.contains("salvo automaticamente")));
    }

    @Test
    void escolhaDeEscolhaComPistaGeraMensagemDeNovaPista() {
        controller.prepararNovaPartida(2);

        // Cena 101 -> 111 -> 102 -> 121 -> 103 -> 131 -> 104
        jogarAteCena(104);
        List<String> mensagens = controller.escolherOpcao(0);

        assertTrue(mensagens.contains("Nova pista: Cheiro de gasolina"));
        assertTrue(controller.getProtagonista().possuiPista("Cheiro de gasolina"));
    }

    @Test
    void jogandoSempreAPrimeiraOpcaoAlcancaUmDosFinais() {
        controller.prepararNovaPartida(2);

        int passos = 0;
        while (!ehFinal(controller.getCenaAtual()) && passos < 200) {
            controller.prepararCenaAtual();
            if (controller.getEscolhasDisponiveis().isEmpty()) {
                controller.avancarCena();
            } else {
                controller.escolherOpcao(0);
            }
            passos++;
        }

        assertTrue(ehFinal(controller.getCenaAtual()), "A partida deveria terminar em um final.");
        assertFalse(controller.isJogoEncerrado());
    }

    @Test
    void salvarECarregarRestauramCenaEAtributos() throws IOException {
        controller.prepararNovaPartida(1);
        controller.escolherOpcao(0);
        int memoria = controller.getProtagonista().getMemoria();
        int cena = controller.getCenaAtual().getId();

        controller.salvarPartida(2, "Teste");
        assertTrue(controller.slotOcupado(2));

        controller.prepararNovaPartida(3);
        controller.carregarPartida(2);

        assertEquals(cena, controller.getCenaAtual().getId());
        assertEquals(memoria, controller.getProtagonista().getMemoria());
        assertEquals("SANGUE_FRIO", controller.getProtagonista().getTemperamento());
    }

    @Test
    void salvarSemPartidaLancaIOException() {
        assertThrows(IOException.class, () -> controller.salvarPartida(1, "X"));
    }

    @Test
    void salvarEmSlotInvalidoLancaIOException() {
        controller.prepararNovaPartida(2);

        assertThrows(IOException.class, () -> controller.salvarPartida(9, "X"));
    }

    @Test
    void carregarSlotVazioLancaIOException() {
        assertThrows(IOException.class, () -> controller.carregarPartida(3));
    }

    @Test
    void excluirSaveLiberaOSlot() throws IOException {
        controller.prepararNovaPartida(2);
        controller.salvarPartida(4, "Apagar");

        controller.excluirSave(4);

        assertFalse(controller.slotOcupado(4));
        assertEquals("Vazio", controller.listarSaves()[3]);
    }

    @Test
    void slotMaisRecenteIndicaOUltimoSalvo() throws Exception {
        assertEquals(0, controller.slotMaisRecente());

        controller.prepararNovaPartida(2);
        controller.salvarPartida(3, "A");
        Thread.sleep(5);
        controller.salvarPartida(2, "B");

        assertEquals(2, controller.slotMaisRecente());
    }

    @Test
    void reiniciarVoltaAoInicioMantendoOTemperamento() {
        controller.prepararNovaPartida(1);
        controller.escolherOpcao(0);
        assertEquals(111, controller.getCenaAtual().getId());

        controller.reiniciarPartida();

        assertEquals(101, controller.getCenaAtual().getId());
        assertEquals(0, controller.getProtagonista().getMemoria());
        assertEquals("SANGUE_FRIO", controller.getProtagonista().getTemperamento());
    }

    @Test
    void encerrarSessaoDescartaAPartida() {
        controller.prepararNovaPartida(2);

        controller.encerrarSessao();

        assertFalse(controller.temPartida());
    }

    // Avança sempre pela primeira opção/transição até alcançar a cena desejada.
    private void jogarAteCena(int idCena) {
        int passos = 0;
        while (controller.getCenaAtual().getId() != idCena && passos < 50) {
            controller.prepararCenaAtual();
            if (controller.getEscolhasDisponiveis().isEmpty()) {
                controller.avancarCena();
            } else {
                controller.escolherOpcao(0);
            }
            passos++;
        }
        assertEquals(idCena, controller.getCenaAtual().getId());
    }

    private boolean ehFinal(Cena cena) {
        return cena.getId() >= 591 && cena.getId() <= 594;
    }
}
