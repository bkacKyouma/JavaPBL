package views.fx.feedback;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Testa o padrão Strategy do feedback: cada mensagem recebe o estilo e o texto da estratégia certa.
class EstrategiaFeedbackTest {

    @Test
    void aumentoRecebeSetaParaCimaEEstiloPositivo() {
        EstrategiaFeedback e = SeletorFeedback.escolher("Memória +15");

        assertTrue(e instanceof FeedbackAumento);
        assertEquals("chip-positivo", e.classeCss());
        assertEquals("▲ Memória +15", e.formatar("Memória +15"));
    }

    @Test
    void quedaRecebeSetaParaBaixoEEstiloNegativo() {
        EstrategiaFeedback e = SeletorFeedback.escolher("Confiança em Fauan -10");

        assertTrue(e instanceof FeedbackQueda);
        assertEquals("chip-negativo", e.classeCss());
        assertEquals("▼ Confiança em Fauan -10", e.formatar("Confiança em Fauan -10"));
    }

    @Test
    void outrasMensagensSaoInformativasESemAlteracao() {
        EstrategiaFeedback e = SeletorFeedback.escolher("Nova pista: Cheiro de gasolina");

        assertTrue(e instanceof FeedbackInformativo);
        assertEquals("chip-info", e.classeCss());
        assertEquals("Nova pista: Cheiro de gasolina", e.formatar("Nova pista: Cheiro de gasolina"));
    }

    @Test
    void numeroNoMeioDoTextoNaoEhConfundidoComVariacao() {
        assertTrue(SeletorFeedback.escolher("Salvo no slot 1.") instanceof FeedbackInformativo);
    }
}
