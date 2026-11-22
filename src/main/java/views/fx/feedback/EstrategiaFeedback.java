package views.fx.feedback;

// Padrão Strategy: cada implementação define como um tipo de mensagem de feedback é reconhecido e exibido.
public interface EstrategiaFeedback {

    // Indica se esta estratégia sabe tratar a mensagem recebida.
    boolean aplicaA(String mensagem);

    // Classe de estilo (CSS) usada na etiqueta.
    String classeCss();

    // Texto final exibido na etiqueta.
    String formatar(String mensagem);
}
