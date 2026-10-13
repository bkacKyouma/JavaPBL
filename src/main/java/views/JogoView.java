package views;

import models.Cena;
import models.Dialogo;
import models.Escolha;

import java.util.List;
import java.util.Scanner;

// Exibe a cena atual e devolve ao Controller a opção escolhida pelo jogador.
public class JogoView {

    // Mantém um único Scanner para ler as entradas durante toda a partida.
    private final Scanner scanner = new Scanner(System.in);

    // Atributos de formatação
    private String corPrimaria;
    private String corSecundaria; // Personagens ou Destaques
    private String modo;
    private final String reset = Terminal.ANSI_RESET;

    public JogoView(String corPrimaria, String corSecundaria, String modo) {
        this.corPrimaria = corPrimaria;
        this.corSecundaria = corSecundaria;
        this.modo = modo;
    }

    private String formatar(String texto) {
        return modo + corPrimaria + texto + reset;
    }

    private String formatarDestaque(String texto) {
        return modo + corSecundaria + texto + reset;
    }

    // Recebe a cena e apenas as escolhas que já passaram pelas condições de desbloqueio.
    public int mostrarCena(Cena cena, List<Escolha> escolhasDisponiveis) {

        Terminal.limpaTerminal();

        // Mostra o título da cena atual.
        System.out.println(formatarDestaque("========================================"));
        System.out.println(formatarDestaque(cena.getTitulo()));
        System.out.println(formatarDestaque("========================================"));
        System.out.println();

        // Mostra a descrição da cena quando existe texto narrativo cadastrado.
        if (cena.getTextoNarrativa() != null) {
            System.out.println(formatar(cena.getTextoNarrativa()));
            System.out.println();
        }

        // Percorre os diálogos da cena e exibe o nome do personagem antes da fala quando houver personagem associado.
        for (Dialogo dialogo : cena.getDialogos()) {
            if (dialogo.getPersonagem() != null) {
                // Nome do personagem em destaque (secundária) e a fala na cor primária
                System.out.println(
                        formatarDestaque(dialogo.getPersonagem() + ": ") +
                                formatar(dialogo.getConteudo())
                );
            } else {
                // Permite usar Dialogo também para textos narrativos sem personagem.
                System.out.println(formatar(dialogo.getConteudo()));
            }
            System.out.println();
        }

        // Mesmo sem escolhas, S salva e M retorna ao menu.
        if (escolhasDisponiveis == null || escolhasDisponiveis.isEmpty()) {
            System.out.print(formatarDestaque("[ENTER] Continuar | [S] Salvar | [M] Menu: "));
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase("S")) return -2;
            if (entrada.equalsIgnoreCase("M")) return -3;
            if (entrada.isEmpty()) return -1;
            return 0;
        }

        System.out.println(formatarDestaque("----------- ESCOLHAS -----------"));

        // Numera somente as escolhas disponíveis, porque escolhas bloqueadas não podem aparecer para o jogador.
        for (int i = 0; i < escolhasDisponiveis.size(); i++) {
            System.out.println(
                    formatar((i + 1) + " - " + escolhasDisponiveis.get(i).getTextoOpcao())
            );
        }

        System.out.print(formatarDestaque("\nEscolha uma opção | [S] Salvar | [M] Menu: "));

        // nextLine permite tanto números quanto os comandos S e M.
        String entrada = scanner.nextLine().trim();
        if (entrada.equalsIgnoreCase("S")) return -2;
        if (entrada.equalsIgnoreCase("M")) return -3;

        try {
            return Integer.parseInt(entrada);
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    // Exibe a mensagem recebida pelo Controller antes de aguardar um curto intervalo.
    public void apresentaErro(String mensagem) {
        System.out.println();
        System.out.println(modo + Terminal.ANSI_RED + mensagem + reset);
        Terminal.aplicaDelay(900);
    }

    // Evita sair acidentalmente e perder mudanças ainda não salvas.
    public boolean confirmarVoltarMenu() {
        System.out.print(formatarDestaque("Mudanças não salvas serão perdidas. Voltar? (S/N): "));
        return scanner.nextLine().trim().equalsIgnoreCase("S");
    }

    // Métodos para atualizar as configurações (se a view mudar durante o jogo)
    public void setCorPrimaria(String corPrimaria) {
        this.corPrimaria = corPrimaria;
    }

    public void setCorSecundaria(String corSecundaria) {
        this.corSecundaria = corSecundaria;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }
}