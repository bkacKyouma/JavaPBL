
package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProtagonistaTest {

    @Test
    void iniciaComOsValoresDoJogo() {
        Protagonista p = new Protagonista(1, "Katarina", "SANGUE_FRIO");

        assertEquals(0, p.getMemoria());
        assertEquals(50, p.getEstabilidadeEmocional());
        assertEquals(50, p.getCulpaPercebida());
        assertEquals(50, p.getConfiancaFauan());
        assertEquals(50, p.getVinculoPassos());
        assertEquals(50, p.getSuspeitaToin());
        assertEquals("INDEFINIDA", p.getPosturaPaternidade());
        assertTrue(p.getPistas().isEmpty());
    }

    @Test
    void limitaAtributosERelacionamentosEntreZeroECem() {
        Protagonista p = new Protagonista(1, "Katarina", "EQUILIBRADO");

        p.ajustarMemoria(500);
        p.ajustarEstabilidade(-500);
        p.ajustarCulpa(500);
        p.ajustarConfiancaFauan(-500);
        p.ajustarVinculoPassos(500);
        p.ajustarSuspeitaToin(-500);

        assertEquals(100, p.getMemoria());
        assertEquals(0, p.getEstabilidadeEmocional());
        assertEquals(100, p.getCulpaPercebida());
        assertEquals(0, p.getConfiancaFauan());
        assertEquals(100, p.getVinculoPassos());
        assertEquals(0, p.getSuspeitaToin());
    }

    @Test
    void impedeRepeticaoDePistasPeloNome() {
        Protagonista p = new Protagonista(1, "Katarina", "SANGUE_QUENTE");

        p.adicionarPista(new Pista("Cheiro de gasolina", "Primeira descricao"));
        p.adicionarPista(new Pista("Cheiro de gasolina", "Outra descricao"));

        assertTrue(p.possuiPista("Cheiro de gasolina"));
        assertFalse(p.possuiPista("Sombra desconhecida"));
        assertEquals(1, p.getPistas().size());
    }

    @Test
    void atualizaPosturaSobrePaternidade() {
        Protagonista p = new Protagonista(1, "Katarina", "SANGUE_FRIO");

        p.setPosturaPaternidade("DUVIDA_ABERTA");

        assertEquals("DUVIDA_ABERTA", p.getPosturaPaternidade());
    }
}
