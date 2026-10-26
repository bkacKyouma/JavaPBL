
package controllers;

import models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class JogoControllerTest {

    private JogoController controller;
    private EstadoJogo estado;

    // Executa um método privado apenas durante o teste.
    private Object invocar(
            String nome,
            Class<?>[] tipos,
            Object... argumentos) throws Exception {

        Method metodo = JogoController.class.getDeclaredMethod(
                nome, tipos
        );

        metodo.setAccessible(true);

        return metodo.invoke(controller, argumentos);
    }

    // Define a sobrevivência de Bruno sem alterar o Controller.
    private void brunoSobrevive(boolean sobrevive) throws Exception {
        Field campo = JogoController.class.getDeclaredField(
                "brunoSobreviveu"
        );

        campo.setAccessible(true);
        campo.setBoolean(controller, sobrevive);
    }

    @BeforeEach
    void construirHistoriaSemAbrirOTerminal() throws Exception {
        controller = new JogoController("", "", "");

        estado = new EstadoJogo(
                new Protagonista(1, "Katarina", "SANGUE_FRIO")
        );

        Field campo = JogoController.class.getDeclaredField(
                "estadoJogo"
        );

        campo.setAccessible(true);
        campo.set(controller, estado);

        invocar("criarPersonagens", new Class<?>[]{});
        invocar("criarPistas", new Class<?>[]{});
        invocar("construirHistoria", new Class<?>[]{});

        estado.iniciarJogo();
    }

    @Test
    void possuiCincoCapitulosEQuatroFinais() {
        assertEquals(5, estado.getCapitulos().size());
        assertEquals(101, estado.getCenaAtual().getId());

        for (int id : new int[]{591, 592, 593, 594}) {
            assertTrue(estado.irParaCena(id));
            assertEquals(5, estado.getCapituloAtual().getId());
        }
    }

    @Test
    void concedePistaAutomaticamenteAoEntrarNaCena404()
            throws Exception {

        Protagonista p = estado.getProtagonista();

        estado.irParaCena(404);

        invocar(
                "processarEventoAoEntrarNaCena",
                new Class<?>[]{Cena.class, Protagonista.class},
                estado.getCenaAtual(),
                p
        );

        assertTrue(p.possuiPista("Alarme desligado manualmente"));
        assertEquals(1, p.getPistas().size());

        invocar(
                "processarEventoAoEntrarNaCena",
                new Class<?>[]{Cena.class, Protagonista.class},
                estado.getCenaAtual(),
                p
        );

        assertEquals(1, p.getPistas().size());
    }

    @Test
    void atribuiPistaDaPerguntaQuandoSuspeitaAnteriorEhPeloMenos50()
            throws Exception {

        Protagonista p = estado.getProtagonista();

        estado.irParaCena(403);

        Escolha e = estado.getCenaAtual().getEscolhas().get(0);

        invocar(
                "processarConsequenciaEspecialAntesDaEscolha",
                new Class<?>[]{
                        Cena.class,
                        Escolha.class,
                        Protagonista.class
                },
                estado.getCenaAtual(),
                e,
                p
        );

        assertTrue(p.possuiPista("Pergunta sobre o alarme"));
        assertEquals(60, p.getSuspeitaToin());
    }

    @Test
    void decideEntreOsQuatroFinaisUsandoSuspeitaEBruno()
            throws Exception {

        Protagonista p = estado.getProtagonista();

        // Suspeita máxima reduz a credibilidade de Toin.
        p.ajustarSuspeitaToin(50);

        brunoSobrevive(true);

        invocar(
                "redirecionarParaFinal",
                new Class<?>[]{Protagonista.class},
                p
        );

        assertEquals(591, estado.getCenaAtual().getId());

        brunoSobrevive(false);

        invocar(
                "redirecionarParaFinal",
                new Class<?>[]{Protagonista.class},
                p
        );

        assertEquals(592, estado.getCenaAtual().getId());

        // Suspeita e confiança zeradas levam à acusação de Katarina.
        p.ajustarSuspeitaToin(-100);
        p.ajustarConfiancaFauan(-100);

        brunoSobrevive(true);

        invocar(
                "redirecionarParaFinal",
                new Class<?>[]{Protagonista.class},
                p
        );

        assertEquals(593, estado.getCenaAtual().getId());

        brunoSobrevive(false);

        invocar(
                "redirecionarParaFinal",
                new Class<?>[]{Protagonista.class},
                p
        );

        assertEquals(594, estado.getCenaAtual().getId());
    }

    @Test
    void nomeiaCorretamenteAsCenasFinais() throws Exception {
        Object isFinal = invocar(
                "ehCenaFinal",
                new Class<?>[]{int.class},
                591
        );

        assertEquals(true, isFinal);

        assertEquals(
                false,
                invocar("ehCenaFinal", new Class<?>[]{int.class}, 501)
        );

        assertEquals(
                "FINAL_TOIN_INDICIADO_BRUNO_VIVO",
                invocar("nomeDoFinal", new Class<?>[]{int.class}, 591)
        );
    }
}
