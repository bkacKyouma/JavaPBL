
package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CapituloTest {

    @Test
    void guardaCenasEEncontraPeloId() {
        Capitulo capitulo = new Capitulo(2, "Tres dias antes");

        Cena primeira = new Cena(201, "Visita", "Texto", "FLASHBACK");
        Cena segunda = new Cena(202, "Perfume", "Texto", "FLASHBACK");

        capitulo.adicionarCena(primeira);
        capitulo.adicionarCena(segunda);

        assertEquals(2, capitulo.getId());
        assertEquals("Tres dias antes", capitulo.getTitulo());
        assertSame(primeira, capitulo.getCenaInicial());
        assertSame(segunda, capitulo.getCena(202));
        assertNull(capitulo.getCena(999));
        assertEquals(2, capitulo.getCenas().size());
    }
}

