package controllers;

import models.DadosGlobais;
import models.ItemGaleria;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Testa a regra da galeria sem ler o disco: perfil + catálogo = itens bloqueados ou desbloqueados.
class GaleriaControllerTest {

    @Test
    void perfilVazioDeixaTudoBloqueado() {
        List<ItemGaleria> itens = GaleriaController.montar(new DadosGlobais());

        assertEquals(7, itens.size());
        for (ItemGaleria item : itens) {
            assertFalse(item.isDesbloqueado());
        }
    }

    @Test
    void finalRegistradoApareceDesbloqueado() {
        DadosGlobais perfil = new DadosGlobais();
        perfil.registrarFinal("FINAL_TOIN_INDICIADO_BRUNO_VIVO");

        List<ItemGaleria> itens = GaleriaController.montar(perfil);

        assertEquals(1, GaleriaController.contarDesbloqueados(itens, GaleriaController.CATEGORIA_FINAIS));
        assertEquals(4, GaleriaController.contarTotal(itens, GaleriaController.CATEGORIA_FINAIS));
        assertTrue(itens.get(0).isDesbloqueado());
        assertEquals("A verdade que restou", itens.get(0).getTitulo());
    }

    @Test
    void extrasSaoContadosNaCategoriaConquistas() {
        DadosGlobais perfil = new DadosGlobais();
        perfil.registrarExtra("FIM?");
        perfil.registrarExtra("BRINQUEDO_BRUNO");

        List<ItemGaleria> itens = GaleriaController.montar(perfil);

        assertEquals(2, GaleriaController.contarDesbloqueados(itens, GaleriaController.CATEGORIA_CONQUISTAS));
        assertEquals(3, GaleriaController.contarTotal(itens, GaleriaController.CATEGORIA_CONQUISTAS));
        assertEquals(0, GaleriaController.contarDesbloqueados(itens, GaleriaController.CATEGORIA_FINAIS));
    }

    @Test
    void listasNulasNoPerfilNaoQuebram() {
        DadosGlobais perfil = new DadosGlobais();
        perfil.finaisDesbloqueados = null;
        perfil.extrasDesbloqueados = null;

        assertDoesNotThrow(() -> GaleriaController.montar(perfil));
    }
}
