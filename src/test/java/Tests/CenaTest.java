
package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CenaTest {

    @Test
    void filtraEscolhasQueNaoCumpriramCondicoes() {
        Protagonista p = new Protagonista(1, "Katarina", "EQUILIBRADO");

        Cena cena = new Cena(404, "O alarme", "Texto", "INTERROGATORIO");

        Escolha livre = new Escolha(1, "Investigar", 405);
        Escolha bloqueada = new Escolha(2, "Acusar", 405);

        bloqueada.definirCondicao("Toin", 70, null);

        cena.adicionarEscolha(livre);
        cena.adicionarEscolha(bloqueada);

        assertEquals(1, cena.getEscolhasDisponiveis(p).size());
        assertSame(livre, cena.getEscolhasDisponiveis(p).get(0));

        p.ajustarSuspeitaToin(20);

        assertEquals(2, cena.getEscolhasDisponiveis(p).size());
    }

    @Test
    void guardaDialogosETransicaoAutomatica() {
        Cena cena = new Cena(106, "Transicao", "Texto", "NARRATIVA");

        assertEquals(-1, cena.getProximaCenaId());

        cena.definirProximaCena(201);
        cena.adicionarDialogo(
                new Dialogo("Fauan", "Vamos continuar", "FALA")
        );

        assertEquals(106, cena.getId());
        assertEquals("Transicao", cena.getTitulo());
        assertEquals("Texto", cena.getTextoNarrativa());
        assertEquals("NARRATIVA", cena.getTipo());
        assertEquals(201, cena.getProximaCenaId());
        assertEquals(1, cena.getDialogos().size());
    }
}
