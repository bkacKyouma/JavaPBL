
package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EstadoJogoTest {

    private EstadoJogo construir() {
        EstadoJogo estado = new EstadoJogo(
                new Protagonista(1, "Katarina", "SANGUE_FRIO")
        );

        Capitulo um = new Capitulo(1, "Primeiro");
        Capitulo dois = new Capitulo(2, "Segundo");

        um.adicionarCena(
                new Cena(101, "Inicio", "Narrativa", "NARRATIVA")
        );

        dois.adicionarCena(
                new Cena(201, "Meio", "Narrativa", "NARRATIVA")
        );

        estado.adicionarCapitulo(um);
        estado.adicionarCapitulo(dois);

        return estado;
    }

    @Test
    void iniciaAvancaEncerraEReiniciaPosicao() {
        EstadoJogo e = construir();

        e.iniciarJogo();

        assertEquals(101, e.getCenaAtual().getId());
        assertEquals(1, e.getCapituloAtual().getId());

        assertTrue(e.irParaCena(201));
        assertEquals(2, e.getCapituloAtual().getId());

        assertFalse(e.irParaCena(999));
        assertEquals(201, e.getCenaAtual().getId());

        e.encerrarJogo("FINAL_TESTE");

        assertTrue(e.isJogoEncerrado());
        assertEquals("FINAL_TESTE", e.getFinalAlcancado());

        e.reiniciarPosicao();

        assertFalse(e.isJogoEncerrado());
        assertNull(e.getFinalAlcancado());
        assertEquals(101, e.getCenaAtual().getId());
    }

    @Test
    void guardaPersonagensESuaProtagonista() {
        EstadoJogo e = construir();
        Psecundario fauan = new Psecundario(2, "Fauan");

        e.adicionarPersonagem(fauan);

        assertSame(fauan, e.getPersonagem(2));
        assertNull(e.getPersonagem(42));
        assertEquals(1, e.getPersonagens().size());
        assertEquals(2, e.getCapitulos().size());
        assertEquals("Katarina", e.getProtagonista().getNome());
        assertNotNull(e.getCapitulo(2));
        assertNull(e.getCapitulo(7));
    }
}
