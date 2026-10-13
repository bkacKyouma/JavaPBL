package views;

import java.util.Scanner;

// Renderiza as artes ASCII e os textos dos conteúdos desbloqueáveis.
public class Colecionaveis {

    private final Scanner scanner = new Scanner(System.in);
    private final String reset = Terminal.ANSI_RESET;
    private final String corDestaque = Terminal.ANSI_YELLOW;
    private final String corTexto = Terminal.ANSI_GREEN;

    // Exibe o colecionável desbloqueado ao concluir a história pela primeira vez.
    public void exibirConquistaFim() {
        Terminal.limpaTerminal();

        String corArte = Terminal.ANSI_BOLD + Terminal.ANSI_PURPLE;

        System.out.println(corDestaque + "========================================" + reset);
        System.out.println(corDestaque + "              FIM DA LINHA              " + reset);
        System.out.println(corDestaque + "========================================" + reset);
        System.out.println();

        System.out.println(corArte + "                                **-                         " + reset);
        System.out.println(corArte + "                               *..*   .                     " + reset);
        System.out.println(corArte + "                             .*-..** **-*  **.              " + reset);
        System.out.println(corArte + "                            .*.....+*...*...::=             " + reset);
        System.out.println(corArte + "                            *......*+.+.*+. .*:*  ...       " + reset);
        System.out.println(corArte + "                           +:.=.+.. *....*..+*.*=*=..*      " + reset);
        System.out.println(corArte + "       *-**.:=+-...+***=  .*.-.*.....*. .* ...****:*.*=     " + reset);
        System.out.println(corArte + "       **..........=.-+****..:*.:....*=...*. .*.**.*.=*     " + reset);
        System.out.println(corArte + "    **..*.*......#..:******.*::-..*..**...:*..*.*.=* .*.    " + reset);
        System.out.println(corArte + "   *.*=:**.........::+***++*..=..*....#*...*..*.--:.+.=*    " + reset);
        System.out.println(corArte + ".*:=.*:...*......:+*****:..*..:.*.+..:.*:. * ..-.*:.* .*    " + reset);
        System.out.println(corArte + ":*=*=.*...+=..............***..+.*.=...**:. +..*.*:.**.*.   " + reset);
        System.out.println(corArte + " *.*..-*....*. ............*.=..+.*.-...*+.:*..=*.*..*.*-   " + reset);
        System.out.println(corArte + " :+.*:.*-...*............:**:*.*.*.*....+** *:..*.=.-*::*   " + reset);
        System.out.println(corArte + "  *..*..*-...*...... . ...**.#*.*.*.-....**..-..+*..*-*.*   " + reset);
        System.out.println(corArte + "  :*..*..*....*.... ....-**..**..*.*..--.:*-.*=..* .*.*.*.  " + reset);
        System.out.println(corArte + "   .=.*...:....+..... .......***..+..*. **....*..*.*.*=**+  " + reset);
        System.out.println(corArte + "    *:.*..*....*.... .......:**-*...-.*+.*.:***...*..*.*.-  " + reset);
        System.out.println(corArte + "    .*.-*..*....*......:....+**-*..-.*+=:*-.-.....*..*++:*: " + reset);
        System.out.println(corArte + "     +*.*+..*....*......:::.*#*==:..*-*+*..**+.....*..*.+.* " + reset);
        System.out.println(corArte + "     .*..*...**:.**.-**********#:*.****=...=**:.....:****.+." + reset);
        System.out.println(corArte + "      **..*.-.*.........+***********:*- :*****+:..........*+" + reset);
        System.out.println(corArte + "      .*=..*.*:*=***=.....-*-...******...-=+********=*****. " + reset);
        System.out.println(corArte + "       +*. ++=.*.-*=.:*****+=******.**.                     " + reset);
        System.out.println(corArte + "        *..+***.***:.. ...*********                         " + reset);
        System.out.println(corArte + "        *........+**+**:.                                   " + reset);
        System.out.println(corArte + "         *******:..                                         " + reset);
        System.out.println(corArte + "                            ## @ @%                         " + reset);
        System.out.println();

        System.out.println(corTexto + "Alcance pelo menos um final: obrigado por jogar e parabéns por zerar o jogo!" + reset);
        System.out.println();
        System.out.println(corDestaque + "[Pressione ENTER para voltar]" + reset);
        scanner.nextLine();
    }

    // Exibe o colecionável referente ao brinquedo de Bruno e aguarda ENTER para retornar.
    public void exibirConquistaBruno() {
        Terminal.limpaTerminal();

        String v = Terminal.ANSI_RED;
        String b = Terminal.ANSI_WHITE + Terminal.ANSI_BOLD;
        String p = "\u001B[90m";

        System.out.println(corDestaque + "========================================" + reset);
        System.out.println(corDestaque + "          REALIDADE INDIGESTA           " + reset);
        System.out.println(corDestaque + "========================================" + reset);
        System.out.println();

        System.out.println("             " + p + "@@@@@@@@@@@@@@" + reset);
        System.out.println("         " + p + "@@@@" + v + "+++++++++++++*" + p + "@@@@" + reset);
        System.out.println("         " + p + "@@@@" + v + "++++++++++++++" + p + "@@@@" + reset);
        System.out.println("      " + p + "@@@" + v + "++++++++++++++++++++***" + p + "@@" + reset);
        System.out.println("    " + p + "@@" + v + "++++++++++++++++++++++++++**" + p + "@@" + reset);
        System.out.println("  " + p + "@@" + v + "+++++" + b + "-----" + v + "++++++++++++++++++****" + p + "@@" + reset);
        System.out.println("  " + p + "@@" + v + "+++++" + b + "-----" + v + "++++++++++++++++++****" + p + "@@" + reset);
        System.out.println(p + "@@" + v + "++++++++++++++++++++++++++++++*****" + p + "%@@" + reset);
        System.out.println(p + "@@" + v + "++++++++++++++++++++++++++*********" + p + "@@@" + reset);
        System.out.println(p + "@@" + v + "++++++++++++++++++++++++++*********" + p + "@@@" + reset);
        System.out.println(p + "@@" + v + "++++++++++++++++++++++++++*********" + p + "@@@" + reset);
        System.out.println(p + "@@%%" + v + "++++++++++++++++++++++*********" + p + "%%@@@" + reset);
        System.out.println(p + "@@##%%" + v + "+++++" + p + "#######" + v + "++++***********" + p + "%%##@@@" + reset);
        System.out.println(p + "@@########" + b + "--   --" + p + "##" + v + "*******" + p + "%%#########@@@" + reset);
        System.out.println("  " + p + "@@++#####" + b + "       " + p + "################++@@" + reset);
        System.out.println("  " + p + "@@" + b + "::   " + p + "++" + b + "--   --" + p + "###########" + b + "+++::--" + p + "@@" + reset);
        System.out.println("    " + p + "@@" + b + ":::   +++++++:::::::::::::--" + p + "@@" + reset);
        System.out.println("      " + p + "@@@" + b + "--::::::::::::::::-----" + p + "@@" + reset);
        System.out.println("      " + p + "@@@" + b + "--::::::::::::::::-----" + p + "@@" + reset);
        System.out.println("         " + p + "@@@@" + b + "--------------" + p + "@@@@" + reset);
        System.out.println("             " + p + "@@@@@@@@@@@@@@" + reset);
        System.out.println();

        System.out.println(corTexto + "Era o jogo favorito dele..." + reset);
        System.out.println();
        System.out.println(corDestaque + "[Pressione ENTER para voltar]" + reset);
        scanner.nextLine();
    }
}