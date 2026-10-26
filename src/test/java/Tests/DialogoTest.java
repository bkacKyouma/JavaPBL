
package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DialogoTest {

    @Test
    void preservaOsDadosDaFala() {
        Dialogo d = new Dialogo(
                "Fauan",
                "O que aconteceu?",
                "FALA"
        );

        assertEquals("Fauan", d.getPersonagem());
        assertEquals("O que aconteceu?", d.getConteudo());
        assertEquals("FALA", d.getTipo());
    }
}
