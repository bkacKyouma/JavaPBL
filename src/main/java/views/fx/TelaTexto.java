package views.fx;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

// Tela simples de leitura, reutilizada para instruções e créditos.
public class TelaTexto {

    public static final String[] INSTRUCOES = {
            "Você acompanha Katarina, uma mulher que tenta reconstruir os acontecimentos de uma noite marcada por um incêndio em sua própria casa.",
            "Durante um interrogatório conduzido pelo detetive Fauan, lembranças do passado começam a se misturar ao presente.",
            "Suas escolhas determinam como Katarina responde, interpreta suas memórias e se relaciona com as pessoas envolvidas.",
            "Suas decisões podem alterar a memória de Katarina, sua estabilidade emocional, sua percepção de culpa, sua relação com os outros personagens e as pistas encontradas.",
            "Algumas respostas e caminhos só estarão disponíveis caso determinadas condições tenham sido alcançadas anteriormente.",
            "Clique na opção desejada para decidir. Use os botões do topo para ver o status, salvar a partida ou voltar ao menu. O jogo também salva automaticamente no slot 1 a cada escolha.",
            "Não existe um único caminho. As decisões tomadas durante a investigação podem levar a diferentes conclusões."
    };

    public static final String[] CREDITOS = {
            "Desenvolvido por:",
            "Bruno Carneiro\nRaphael Henrique",
            "UEFS - Engenharia de Computação\nEXA863 - MI Programação",
            "Obrigado por jogar!"
    };

    private final VBox raiz = new VBox(16);

    public TelaTexto(Navegador nav, String titulo, String[] paragrafos) {
        raiz.setPadding(new Insets(40, 80, 30, 80));

        VBox conteudo = new VBox(14);
        for (String paragrafo : paragrafos) {
            conteudo.getChildren().add(Componentes.rotulo(paragrafo, "fala"));
        }

        ScrollPane rolagem = new ScrollPane(conteudo);
        rolagem.setFitToWidth(true);
        VBox.setVgrow(rolagem, javafx.scene.layout.Priority.ALWAYS);

        raiz.getChildren().addAll(
                Componentes.rotulo(titulo, "tela-titulo"),
                Componentes.divisor(),
                rolagem,
                Componentes.botao("Voltar", "botao-pequeno", nav::mostrarMenu));
    }

    public Parent getRaiz() {
        return raiz;
    }
}
