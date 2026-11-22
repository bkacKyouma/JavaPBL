package views.fx.feedback;

// Estratégia padrão: qualquer outra mensagem (nova pista, salvamento, aviso) aparece sem alteração.
public class FeedbackInformativo implements EstrategiaFeedback {

    @Override
    public boolean aplicaA(String mensagem) {
        return true;
    }

    @Override
    public String classeCss() {
        return "chip-info";
    }

    @Override
    public String formatar(String mensagem) {
        return mensagem;
    }
}
