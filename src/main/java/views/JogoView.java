package views;

import models.Cena;
import models.Dialogo;
import models.Escolha;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;


public class JogoView {

    private static final Scanner scanner = new Scanner(System.in);

    /*
     * Exibe uma cena e as escolhas que o Controller
     * determinou que estão disponíveis.
     */
    public static int mostrarCena(
            Cena cena,
            List<Escolha> escolhasDisponiveis) {

        Terminal.limpaTerminal();

        // =========================
        // TÍTULO DA CENA
        // =========================

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

        // =========================
        // NARRATIVA
        // =========================

        if (cena.getTextoNarrativa() != null) {

            System.out.println(
                    cena.getTextoNarrativa()
            );

            System.out.println();
        }


        // =========================
        // DIÁLOGOS
        // =========================

        for (Dialogo dialogo : cena.getDialogos()) {

            /*
             * Se existir personagem associado,
             * mostramos:
             *
             * FAUAN: Texto...
             */
            if (dialogo.getPersonagem() != null) {
                System.out.println(
                        dialogo.getPersonagem()
                                + ": "
                                + dialogo.getConteudo()
                );

            } else {

                /*
                 * Permite utilizar Dialogo também
                 * para descrições sem personagem.
                 */
                System.out.println(
                        dialogo.getConteudo()
                );
            }

            System.out.println();
        }


        // =========================
        // CENA SEM ESCOLHAS
        // =========================

        if (escolhasDisponiveis == null
                || escolhasDisponiveis.isEmpty()) {

            System.out.println(
                    "0 - Voltar ao menu principal"
            );

            System.out.println(
                    "[Pressione ENTER para continuar]"
            );

            String input = scanner.nextLine();

            if (input.trim().equals("0")) {
                return 0;
            }

            return -1;
        }


        // =========================
        // ESCOLHAS
        // =========================

        System.out.println(
                "----------- ESCOLHAS -----------"
        );

        System.out.println(
                "0 - Voltar ao menu principal\n"
        );


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


        System.out.print(
                "\nEscolha uma opção: "
        );


        // =========================
        // LEITURA DA OPÇÃO
        // =========================

        try {
            int opcao = scanner.nextInt();

            /*
             * Remove o ENTER deixado pelo nextInt().
             */
            scanner.nextLine();

            return opcao;

        } catch (InputMismatchException e) {
            /*
             * Remove a entrada inválida.
             */
            scanner.nextLine();
            /*
             * O Controller tratará uma entrada vazia ou letras como inválido.
             */
            return -99;
        }
    }

    public static void apresentaErro(String mensagem) {
        System.out.println();
        System.out.println(mensagem);
        Terminal.aplicaDelay(900);
    }
}