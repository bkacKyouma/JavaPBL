
package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PsecundarioTest {

    @Test
    void herdaIdNomeEAlteracaoDeNome() {
        Psecundario toin = new Psecundario(4, "Toin");

        assertEquals(4, toin.getId());
        assertEquals("Toin", toin.getNome());

        toin.setNome("Antonio");

        assertEquals("Antonio", toin.getNome());
    }
}
