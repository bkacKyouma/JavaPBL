
package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DadosGlobaisTest {

    @Test
    void guardaConfiguracoesEImpedeConquistasRepetidas() {
        DadosGlobais d = DadosGlobais.criarPadrao(
                "verde", "amarelo", "negrito"
        );

        assertEquals(1, d.versao);
        assertEquals("verde", d.corPrimaria);
        assertEquals("amarelo", d.corSecundaria);
        assertEquals("negrito", d.modo);
        assertTrue(d.dataUltimaModificacao > 0);

        d.registrarFinal("FINAL_TOIN_INDICIADO_BRUNO_VIVO");
        d.registrarFinal("FINAL_TOIN_INDICIADO_BRUNO_VIVO");
        d.registrarExtra("FIM?");
        d.registrarExtra("FIM?");

        assertEquals(1, d.finaisDesbloqueados.size());
        assertEquals(1, d.extrasDesbloqueados.size());
    }
}
