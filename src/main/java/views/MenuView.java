package views;

import java.util.InputMismatchException;
import java.util.Scanner;

// Mostra as telas do menu e devolve ao Controller as opções digitadas pelo jogador.
public class MenuView {

    // O mesmo Scanner é reutilizado no menu principal, temperamento, instruções e créditos.
    private static final Scanner scanner =
            new Scanner(System.in);


    // Exibe as quatro opções do menu principal e retorna a opção digitada.
    public static int openMenu() {

        Terminal.limpaTerminal();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "           A NOITE QUE NÃO SE APAGA"
        );

        System.out.println(
                "========================================"
        );

        System.out.println();

        System.out.println("1 - Nova partida");
        System.out.println("2 - Continuar");
        System.out.println("3 - Carregar partida");
        System.out.println("4 - Instruções");
        System.out.println("5 - Créditos");
        System.out.println("6 - Sair");

        System.out.print(
                "\nDigite o número da opção desejada: "
        );


        // Retorna -1 quando a entrada não é numérica para que o MenuController faça a validação.
        try {

            int opcao = scanner.nextInt();

            // Remove o ENTER que permanece no buffer depois do nextInt().
            scanner.nextLine();

            return opcao;

        } catch (InputMismatchException e) {

            scanner.nextLine();

            return -1;
        }
    }


    // Mostra os três temperamentos disponíveis antes do início de uma nova partida.
    public static int openAtributesMenu() {

        Terminal.limpaTerminal();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "        TEMPERAMENTO DE KATARINA"
        );

        System.out.println(
                "========================================"
        );

        System.out.println();

        System.out.println(
                "Escolha como Katarina costuma reagir"
        );

        System.out.println(
                "diante de situações de pressão."
        );

        System.out.println();

        System.out.println(
                "1 - Sangue frio"
        );

        System.out.println(
                "2 - Equilíbrio"
        );

        System.out.println(
                "3 - Sangue quente"
        );

        System.out.print(
                "\nDigite o número da opção desejada: "
        );


        // Retorna a opção numérica ou -1 para que o Controller rejeite uma entrada inválida.
        try {

            int opcao = scanner.nextInt();

            scanner.nextLine();

            return opcao;

        } catch (InputMismatchException e) {

            scanner.nextLine();

            return -1;
        }
    }


    // Explica o contexto da investigação e quais elementos podem mudar com as escolhas.
    public static void Instrucoes() {

        Terminal.limpaTerminal();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "              INSTRUÇÕES"
        );

        System.out.println(
                "========================================"
        );

        System.out.println();


        System.out.println(
                "Você acompanha Katarina, uma mulher"
        );

        System.out.println(
                "que tenta reconstruir os acontecimentos"
        );

        System.out.println(
                "de uma noite marcada por um incêndio"
        );

        System.out.println(
                "em sua própria casa."
        );


        System.out.println();

        System.out.println(
                "Durante um interrogatório conduzido pelo"
        );

        System.out.println(
                "detetive Fauan, lembranças do passado"
        );

        System.out.println(
                "começam a se misturar ao presente."
        );


        System.out.println();

        System.out.println(
                "Suas escolhas determinam como Katarina"
        );

        System.out.println(
                "responde, interpreta suas memórias e"
        );

        System.out.println(
                "se relaciona com as pessoas envolvidas."
        );


        System.out.println();

        System.out.println(
                "Ao longo da investigação, suas decisões"
        );

        System.out.println(
                "podem alterar:"
        );


        System.out.println();

        System.out.println(
                "- a memória de Katarina;"
        );

        System.out.println(
                "- sua estabilidade emocional;"
        );

        System.out.println(
                "- sua percepção de culpa;"
        );

        System.out.println(
                "- sua relação com outros personagens;"
        );

        System.out.println(
                "- as pistas encontradas durante a história."
        );


        System.out.println();

        System.out.println(
                "Algumas respostas e caminhos só estarão"
        );

        System.out.println(
                "disponíveis caso determinadas condições"
        );

        System.out.println(
                "tenham sido alcançadas anteriormente."
        );


        System.out.println();

        System.out.println(
                "Por isso, cada decisão pode provocar"
        );

        System.out.println(
                "consequências imediatas ou aparecer"
        );

        System.out.println(
                "novamente muito tempo depois."
        );


        System.out.println();

        System.out.println(
                "Quando uma escolha aparecer, digite o"
        );

        System.out.println(
                "número correspondente à opção desejada."
        );


        System.out.println();

        System.out.println(
                "Não existe um único caminho."
        );

        System.out.println(
                "As decisões tomadas durante a investigação"
        );

        System.out.println(
                "podem levar a diferentes conclusões."
        );


        System.out.println();

        System.out.println(
                "[Pressione ENTER para voltar ao menu]"
        );

        scanner.nextLine();
    }


    // Mostra os nomes dos desenvolvedores e aguarda ENTER antes de retornar ao menu.
    public static void Creditos() {

        Terminal.limpaTerminal();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "               CRÉDITOS"
        );

        System.out.println(
                "========================================"
        );

        System.out.println();

        System.out.println(
                "Desenvolvido por:"
        );

        Terminal.aplicaDelay(600);

        System.out.println();

        System.out.println(
                "Bruno Carneiro"
        );

        System.out.println(
                "Raphael Henrique"
        );

        System.out.println();

        System.out.println(
                "Obrigado por jogar!"
        );

        System.out.println();

        System.out.println(
                "[Pressione ENTER para voltar ao menu]"
        );

        scanner.nextLine();
    }


    // Mostra a mensagem de erro enviada pelo Controller e aplica um atraso antes da nova tentativa.
    public static void apresentaErro(
            String mensagem) {

        System.out.println();

        System.out.println(mensagem);

        Terminal.aplicaDelay(900);
    }
    // Pergunta em qual dos três slots o jogador deseja salvar ou carregar.
    public static int escolherSlot(String titulo) {
        Terminal.limpaTerminal();
        System.out.println("=== " + titulo + " ===");
        System.out.println("1 - Slot 1");
        System.out.println("2 - Slot 2");
        System.out.println("3 - Slot 3");
        System.out.println("0 - Cancelar");
        System.out.print("Escolha: ");

        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Evita substituir um save existente sem autorização do jogador.
    public static boolean confirmarSobrescrita(int slot) {
        System.out.print("O slot " + slot + " já possui um save. Substituir? (S/N): ");
        return scanner.nextLine().trim().equalsIgnoreCase("S");
    }
}
