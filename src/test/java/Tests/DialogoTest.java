package Tests;


import models.Dialogo;
import models.Psecundario;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DialogoTest {

    @Test
    void testeCriacaoDialogo() {
        Psecundario npc = new Psecundario(2, "Toin");
        Dialogo dialogo = new Dialogo("npc", "Onde você estava?", "Interrogação");

        assertEquals(npc, dialogo.getPersonagem());
        assertEquals("Onde você estava?", dialogo.getConteudo());
        assertEquals("Interrogação", dialogo.getTipo());
    }
}