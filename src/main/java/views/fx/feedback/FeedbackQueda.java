package views.fx.feedback;

// Mensagens de queda de valor, como "Confiança em Fauan -10".
public class FeedbackQueda implements EstrategiaFeedback {

    @Override
    public boolean aplicaA(String mensagem) {
        return mensagem.matches(".* -\\d+");
    }

    @Override
    public String classeCss() {
        return "chip-negativo";
    }

    @Override
    public String formatar(String mensagem) {
        return "▼ " + mensagem;
    }
}
