
package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PistaTest {

    @Test
    void preservaNomeEDescricao() {
        Pista pista = new Pista(
                "Pergunta sobre o alarme",
                "Toin perguntou pelo alarme."
        );

        assertEquals("Pergunta sobre o alarme", pista.getNome());
        assertEquals("Toin perguntou pelo alarme.", pista.getDescricao());
    }
}
