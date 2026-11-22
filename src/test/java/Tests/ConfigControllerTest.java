package controllers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Testa as regras de preferências em memória (sem gravar em disco).
class ConfigControllerTest {

    @Test
    void alternarNegritoLigaEDesliga() {
        ConfigController config = new ConfigController();

        config.setNegrito(true);
        assertTrue(config.isNegrito());

        config.setNegrito(false);
        assertFalse(config.isNegrito());
    }

    @Test
    void negritoEItalicoSaoIndependentes() {
        ConfigController config = new ConfigController();
        config.setNegrito(false);
        config.setItalico(false);

        config.setNegrito(true);
        config.setNegrito(true);

        assertTrue(config.isNegrito());
        assertFalse(config.isItalico());
        assertEquals(1, config.getModo().split("\u001B\\[1m", -1).length - 1);
    }

    @Test
    void definirCorAtualizaOHexCorrespondente() {
        ConfigController config = new ConfigController();

        config.setCor(ConfigController.ANSI_CIANO);

        assertEquals(ConfigController.corParaHex(ConfigController.ANSI_CIANO), config.getCorHex());
        assertNotEquals(ConfigController.corParaHex(ConfigController.ANSI_AMARELO), config.getCorHex());
    }

    @Test
    void corDesconhecidaUsaVerdePadrao() {
        assertEquals(
                ConfigController.corParaHex(ConfigController.ANSI_VERDE),
                ConfigController.corParaHex("qualquer coisa"));
    }

    @Test
    void singletonDevolveSempreAMesmaInstancia() {
        assertSame(ConfigController.getInstance(), ConfigController.getInstance());
    }

    @Test
    void observadorEhNotificadoQuandoAPreferenciaMuda() {
        ConfigController config = new ConfigController();
        int[] chamadas = {0};
        config.adicionarObservador(() -> chamadas[0]++);

        config.setCor(ConfigController.ANSI_AMARELO);
        config.setNegrito(true);
        config.setItalico(true);

        assertEquals(3, chamadas[0]);
    }

    @Test
    void observadorRemovidoNaoEhMaisNotificado() {
        ConfigController config = new ConfigController();
        int[] chamadas = {0};
        ConfigObserver observador = () -> chamadas[0]++;
        config.adicionarObservador(observador);
        config.removerObservador(observador);

        config.setNegrito(true);

        assertEquals(0, chamadas[0]);
    }
}
