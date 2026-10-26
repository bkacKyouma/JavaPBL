package test.java.Tests;

import models.Escolha;
import models.Protagonista;
import models.Pista;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EscolhaTest {

    private Protagonista protagonista;
    private Escolha escolha;

    @BeforeEach
    void setUp() {
        protagonista = new Protagonista(1, "Kat", "SANGUE_QUENTE");
        // Construtor padrão da Escolha iniciando sem exigir pistas
        escolha = new Escolha(10, "Gritar com Fauan", 2);
    }

    @Test
    void testeDisponibilidadePorTemperamento() {
        escolha.definirCondicao(null, 0, "SANGUE_FRIO");

        assertFalse(escolha.estaDisponivel(protagonista), "Não deve estar disponível pois o temperamento exigido é diferente");

        escolha.definirCondicao(null, 0, "SANGUE_QUENTE");
        assertTrue(escolha.estaDisponivel(protagonista));
    }

    @Test
    void testeDisponibilidadePorPista() {
        escolha.adicionarPistaNecessaria("Faca");
        assertFalse(escolha.estaDisponivel(protagonista), "Deve bloquear se a protagonista não tem a pista");

        protagonista.adicionarPista(new Pista("Faca", "Cozinha"));
        assertTrue(escolha.estaDisponivel(protagonista), "Deve liberar após a protagonista adquirir a pista exigida");
    }

    @Test
    void testeAplicarConsequenciasTomIgualTemperamento() {
        int estabilidadeInicial = protagonista.getEstabilidadeEmocional();
        escolha.definirTom("SANGUE_QUENTE");

        escolha.aplicarConsequencias(protagonista);

        // Tom == Temperamento resulta em +5 Estabilidade[cite: 3]
        assertEquals(estabilidadeInicial + 5, protagonista.getEstabilidadeEmocional());
    }

    @Test
    void testeAplicarConsequenciasTomDiferenteTemperamento() {
        int estabilidadeInicial = protagonista.getEstabilidadeEmocional();
        int confiancaInicial = protagonista.getConfiancaFauan();

        escolha.definirTom("SANGUE_FRIO"); // Protagonista é SANGUE_QUENTE
        escolha.aplicarConsequencias(protagonista);

        // Tom != Temperamento resulta em -5 Estabilidade e +5 Confiança Fauan[cite: 3]
        assertEquals(estabilidadeInicial - 5, protagonista.getEstabilidadeEmocional());
        assertEquals(confiancaInicial + 5, protagonista.getConfiancaFauan());
    }
}
