package views;

import models.Cena;
import models.Dialogo;
import models.Escolha;

import java.util.List;
import java.util.Scanner;

// Exibe a cena atual e devolve ao Controller a opção escolhida pelo jogador.
public class JogoView {

    // Mantém um único Scanner para ler as entradas durante toda a partida.
    private static final Scanner scanner = new Scanner(System.in);


    // Recebe a cena e apenas as escolhas que já passaram pelas condições de desbloqueio.
    public static int mostrarCena(
            Cena cena,
            List<Escolha> escolhasDisponiveis) {

        Terminal.limpaTerminal();


        // Mostra o título da cena atual.
        System.out.println(
                "========================================"
        );

        System.out.println(
                cena.getTitulo()
        );

        System.out.println(
                "========================================"
        );

        System.out.println();


        // Mostra a descrição da cena quando existe texto narrativo cadastrado.
        if (cena.getTextoNarrativa() != null) {

            System.out.println(
                    cena.getTextoNarrativa()
            );

            System.out.println();
        }


        // Percorre os diálogos da cena e exibe o nome do personagem antes da fala quando houver personagem associado.
        for (Dialogo dialogo : cena.getDialogos()) {

            if (dialogo.getPersonagem() != null) {

                System.out.println(
                        dialogo.getPersonagem()
                        + ": "
                        + dialogo.getConteudo()
                );

            } else {

                // Permite usar Dialogo também para textos narrativos sem personagem.
                System.out.println(
                        dialogo.getConteudo()
                );
            }

            System.out.println();
        }


        // Mesmo sem escolhas, S salva e M retorna ao menu.
        if (escolhasDisponiveis == null || escolhasDisponiveis.isEmpty()) {
            System.out.print("[ENTER] Continuar | [S] Salvar | [M] Menu: ");
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase("S")) return -2;
            if (entrada.equalsIgnoreCase("M")) return -3;
            if (entrada.isEmpty()) return -1;
            return 0;
        }


        System.out.println(
                "----------- ESCOLHAS -----------"
        );


        // Numera somente as escolhas disponíveis, porque escolhas bloqueadas não podem aparecer para o jogador.
        for (int i = 0;
             i < escolhasDisponiveis.size();
             i++) {

            System.out.println(
                    (i + 1)
                    + " - "
                    + escolhasDisponiveis
                        .get(i)
                        .getTextoOpcao()
            );
        }


        System.out.print("\nEscolha uma opção | [S] Salvar | [M] Menu: ");

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
    public static void apresentaErro(String mensagem) {

        System.out.println();

        System.out.println(mensagem);

        Terminal.aplicaDelay(900);
    }
    // Evita sair acidentalmente e perder mudanças ainda não salvas.
    public static boolean confirmarVoltarMenu() {
        System.out.print("Mudanças não salvas serão perdidas. Voltar? (S/N): ");
        return scanner.nextLine().trim().equalsIgnoreCase("S");
    }
}
