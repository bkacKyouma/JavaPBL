package views.fx.feedback;

// Mensagens de aumento de valor, como "Memória +15".
public class FeedbackAumento implements EstrategiaFeedback {

    @Override
    public boolean aplicaA(String mensagem) {
        return mensagem.matches(".* \\+\\d+");
    }

    @Override
    public String classeCss() {
        return "chip-positivo";
    }

    @Override
    public String formatar(String mensagem) {
        return "▲ " + mensagem;
    }
}
