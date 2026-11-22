package views.fx.feedback;

import java.util.Arrays;
import java.util.List;

// Escolhe a primeira estratégia que reconhece a mensagem (a informativa é sempre a última).
public class SeletorFeedback {

    private static final List<EstrategiaFeedback> ESTRATEGIAS = Arrays.asList(
            new FeedbackAumento(),
            new FeedbackQueda(),
            new FeedbackInformativo());

    private SeletorFeedback() {
    }

    public static EstrategiaFeedback escolher(String mensagem) {
        for (EstrategiaFeedback estrategia : ESTRATEGIAS) {
            if (estrategia.aplicaA(mensagem)) {
                return estrategia;
            }
        }
        return ESTRATEGIAS.get(ESTRATEGIAS.size() - 1);
    }
}
