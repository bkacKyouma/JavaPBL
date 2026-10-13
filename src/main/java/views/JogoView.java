package views;

import models.Cena;
import models.Dialogo;
import models.Escolha;

import java.util.List;
import java.util.Scanner;

// Exibe a cena atual e devolve ao Controller a opção escolhida pelo jogador.
public class JogoView {

    private final Scanner scanner = new Scanner(System.in);

    // Estado atual da formatação ANSI aplicada nas telas da partida.
    private String corPrimaria;
    private String corSecundaria;
    private String modo;
    private final String reset = Terminal.ANSI_RESET;

    public JogoView(String corPrimaria, String corSecundaria, String modo) {
        this.corPrimaria = corPrimaria;
        this.corSecundaria = corSecundaria;
        this.modo = modo;
    }

    // Envolve o texto padrão com a cor primária e o modo ativos.
    private String formatar(String texto) {
        return modo + corPrimaria + texto + reset;
    }

    // Envolve títulos, cabeçalhos e nomes de personagens com a cor secundária.
    private String formatarDestaque(String texto) {
        return modo + corSecundaria + texto + reset;
    }

    // Renderiza a narrativa, os diálogos e as escolhas liberadas da cena atual.
    public int mostrarCena(Cena cena, List<Escolha> escolhasDisponiveis) {

        Terminal.limpaTerminal();

        System.out.println(formatarDestaque("========================================"));
        System.out.println(formatarDestaque(cena.getTitulo()));
        System.out.println(formatarDestaque("========================================"));
        System.out.println();

        // Imprime a descrição de ambiente caso a cena possua texto narrativo.
        if (cena.getTextoNarrativa() != null) {
            System.out.println(formatar(cena.getTextoNarrativa()));
            System.out.println();
        }

        // Imprime os diálogos destacando o nome do personagem quando houver autor associado.
        for (Dialogo dialogo : cena.getDialogos()) {
            if (dialogo.getPersonagem() != null) {
                System.out.println(
                        formatarDestaque(dialogo.getPersonagem() + ": ") +
                                formatar(dialogo.getConteudo())
                );
            } else {
                System.out.println(formatar(dialogo.getConteudo()));
            }
            System.out.println();
        }

        // Em cenas de transição (sem escolhas), aguarda confirmação de avanço ou comando de sistema.
        if (escolhasDisponiveis == null || escolhasDisponiveis.isEmpty()) {
            System.out.print(formatarDestaque("[ENTER] Continuar | [S] Salvar | [M] Menu: "));
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase("S")) return -2;
            if (entrada.equalsIgnoreCase("M")) return -3;
            if (entrada.isEmpty()) return -1;
            return 0;
        }

        System.out.println(formatarDestaque("----------- ESCOLHAS -----------"));

        // Lista e numera sequencialmente apenas as escolhas que cumpriram suas condições.
        for (int i = 0; i < escolhasDisponiveis.size(); i++) {
            System.out.println(
                    formatar((i + 1) + " - " + escolhasDisponiveis.get(i).getTextoOpcao())
            );
        }

        System.out.print(formatarDestaque("\nEscolha uma opção | [S] Salvar | [M] Menu: "));

        // Lê a linha como texto para aceitar tanto o índice da escolha quanto os atalhos S e M.
        String entrada = scanner.nextLine().trim();
        if (entrada.equalsIgnoreCase("S")) return -2;
        if (entrada.equalsIgnoreCase("M")) return -3;

        try {
            return Integer.parseInt(entrada);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // Imprime mensagens de falha em vermelho e pausa brevemente a execução.
    public void apresentaErro(String mensagem) {
        System.out.println();
        System.out.println(modo + Terminal.ANSI_RED + mensagem + reset);
        Terminal.aplicaDelay(900);
    }

    // Solicita confirmação antes de interromper o loop da partida e voltar ao menu.
    public boolean confirmarVoltarMenu() {
        System.out.print(formatarDestaque("Mudanças não salvas serão perdidas. Voltar? (S/N): "));
        return scanner.nextLine().trim().equalsIgnoreCase("S");
    }

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