package views;

import java.util.InputMismatchException;
import java.util.Scanner;

// Mostra as telas do menu e devolve ao Controller as opções digitadas pelo jogador.
public class MenuView {

    // O mesmo Scanner é reutilizado no menu principal, temperamento, instruções e créditos.
    private final Scanner scanner = new Scanner(System.in);

    // Atributos de formatação
    private String corPrimaria;
    private String corSecundaria; // Pode ser usada para destaques ou títulos
    private String modo; // Para bold, itálico, reverse (modo claro)
    private final String reset = Terminal.ANSI_RESET;

    // Construtor para receber as configurações
    public MenuView(String corPrimaria, String corSecundaria, String modo) {
        this.corPrimaria = corPrimaria;
        this.corSecundaria = corSecundaria;
        this.modo = modo;
    }

    // Método auxiliar para formatar texto padrão
    private String formatar(String texto) {
        return modo + corPrimaria + texto + reset;
    }

    // Método auxiliar para formatar destaques (títulos, avisos)
    private String formatarDestaque(String texto) {
        return modo + corSecundaria + texto + reset;
    }

    // Exibe as opções do menu principal
    public int openMenu() {
        Terminal.limpaTerminal();

        System.out.println(formatarDestaque("========================================"));
        System.out.println(formatarDestaque("           A NOITE QUE NÃO SE APAGA"));
        System.out.println(formatarDestaque("========================================"));
        System.out.println();

        System.out.println(formatar("1 - Nova partida"));
        System.out.println(formatar("2 - Carregar / Continuar"));
        System.out.println(formatar("3 - Colecionáveis"));
        System.out.println(formatar("4 - Instruções"));
        System.out.println(formatar("5 - Preferências"));
        System.out.println(formatar("6 - Créditos"));
        System.out.println(formatar("7 - Sair"));

        System.out.print(formatar("\nDigite o número da opção desejada: "));

        try {
            int opcao = scanner.nextInt();
            scanner.nextLine();
            return opcao;
        } catch (InputMismatchException e) {
            scanner.nextLine();
            return -1;
        }
    }

    // Menu Intermediário para Colecionáveis
    public int openColecionaveisMenu() {
        Terminal.limpaTerminal();

        System.out.println(formatarDestaque("========================================"));
        System.out.println(formatarDestaque("             COLECIONÁVEIS"));
        System.out.println(formatarDestaque("========================================"));
        System.out.println();

        System.out.println(formatar("1 - Galeria de Finais"));
        System.out.println(formatar("2 - Conteúdos Extras"));
        System.out.println(formatar("0 - Voltar"));

        System.out.print(formatar("\nDigite o número da opção desejada: "));

        try {
            int opcao = scanner.nextInt();
            scanner.nextLine();
            return opcao;
        } catch (InputMismatchException e) {
            scanner.nextLine();
            return -1;
        }
    }

    // Nova tela para configurações de aparência
    public int openConfigMenu() {
        Terminal.limpaTerminal();

        System.out.println(formatarDestaque("========================================"));
        System.out.println(formatarDestaque("             PREFERÊNCIAS"));
        System.out.println(formatarDestaque("========================================"));
        System.out.println();
        System.out.println(Terminal.ANSI_GREEN + "1 - Cor: Verde"+ Terminal.ANSI_RESET);
        System.out.println(Terminal.ANSI_CYAN +"2 - Cor: Ciano"+ Terminal.ANSI_RESET);
        System.out.println(Terminal.ANSI_YELLOW +"3 - Cor: Amarelo"+ Terminal.ANSI_RESET);
        System.out.println(Terminal.ANSI_WHITE +"4 - Cor: Branco"+ Terminal.ANSI_RESET);
        System.out.println(Terminal.ANSI_REVERSE + "5 - Alternar Modo Claro/Escuro" + Terminal.ANSI_RESET);
        System.out.println(Terminal.ANSI_BOLD + "6 - Alternar Texto em Negrito" + Terminal.ANSI_RESET);
        System.out.println(Terminal.ANSI_ITALIC +"7 - Alternar Texto em Itálico"+ Terminal.ANSI_RESET);
        System.out.println(formatar("0 - Voltar"));

        System.out.print(formatar("\nDigite o número da opção desejada: "));

        try {
            int opcao = scanner.nextInt();
            scanner.nextLine();
            return opcao;
        } catch (InputMismatchException e) {
            scanner.nextLine();
            return -1;
        }
    }

    // Mostra os três temperamentos disponíveis antes do início de uma nova partida.
    public int openAtributesMenu() {

        Terminal.limpaTerminal();

        System.out.println(formatarDestaque("========================================"));
        System.out.println(formatarDestaque("        TEMPERAMENTO DE KATARINA"));
        System.out.println(formatarDestaque("========================================"));
        System.out.println();

        System.out.println(formatar("Escolha como Katarina costuma reagir"));
        System.out.println(formatar("diante de situações de pressão."));
        System.out.println();

        System.out.println(formatar("1 - Sangue frio"));
        System.out.println(formatar("2 - Equilíbrio"));
        System.out.println(formatar("3 - Sangue quente"));

        System.out.print(formatar("\nDigite o número da opção desejada: "));

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
    public void Instrucoes() {

        Terminal.limpaTerminal();

        System.out.println(formatarDestaque("========================================"));
        System.out.println(formatarDestaque("              INSTRUÇÕES"));
        System.out.println(formatarDestaque("========================================"));
        System.out.println();

        System.out.println(formatar("Você acompanha Katarina, uma mulher"));
        System.out.println(formatar("que tenta reconstruir os acontecimentos"));
        System.out.println(formatar("de uma noite marcada por um incêndio"));
        System.out.println(formatar("em sua própria casa."));
        System.out.println();

        System.out.println(formatar("Durante um interrogatório conduzido pelo"));
        System.out.println(formatar("detetive Fauan, lembranças do passado"));
        System.out.println(formatar("começam a se misturar ao presente."));
        System.out.println();

        System.out.println(formatar("Suas escolhas determinam como Katarina"));
        System.out.println(formatar("responde, interpreta suas memórias e"));
        System.out.println(formatar("se relaciona com as pessoas envolvidas."));
        System.out.println();

        System.out.println(formatar("Ao longo da investigação, suas decisões"));
        System.out.println(formatar("podem alterar:"));
        System.out.println();

        System.out.println(formatar("- a memória de Katarina;"));
        System.out.println(formatar("- sua estabilidade emocional;"));
        System.out.println(formatar("- sua percepção de culpa;"));
        System.out.println(formatar("- sua relação com outros personagens;"));
        System.out.println(formatar("- as pistas encontradas durante a história."));
        System.out.println();

        System.out.println(formatar("Algumas respostas e caminhos só estarão"));
        System.out.println(formatar("disponíveis caso determinadas condições"));
        System.out.println(formatar("tenham sido alcançadas anteriormente."));
        System.out.println();

        System.out.println(formatar("Por isso, cada decisão pode provocar"));
        System.out.println(formatar("consequências imediatas ou aparecer"));
        System.out.println(formatar("novamente muito tempo depois."));
        System.out.println();

        System.out.println(formatar("Quando uma escolha aparecer, digite o"));
        System.out.println(formatar("número correspondente à opção desejada."));
        System.out.println();

        System.out.println(formatar("Não existe um único caminho."));
        System.out.println(formatar("As decisões tomadas durante a investigação"));
        System.out.println(formatar("podem levar a diferentes conclusões."));
        System.out.println();

        System.out.println(formatarDestaque("[Pressione ENTER para voltar ao menu]"));
        scanner.nextLine();
    }


    // Mostra os nomes dos desenvolvedores e aguarda ENTER antes de retornar ao menu.
    public void Creditos() {

        Terminal.limpaTerminal();

        System.out.println(formatarDestaque("========================================"));
        System.out.println(formatarDestaque("               CRÉDITOS"));
        System.out.println(formatarDestaque("========================================"));
        System.out.println();

        System.out.println(formatar("Desenvolvido por:"));
        Terminal.aplicaDelay(600);
        System.out.println();

        System.out.println(formatar("Bruno Carneiro"));
        System.out.println(formatar("Raphael Henrique"));
        System.out.println();

        System.out.println(formatarDestaque("Obrigado por jogar!"));
        System.out.println();

        System.out.println(formatarDestaque("[Pressione ENTER para voltar ao menu]"));
        scanner.nextLine();
    }


    // Mostra a mensagem de erro enviada pelo Controller e aplica um atraso antes da nova tentativa.
    public void apresentaErro(String mensagem) {
        System.out.println();
        // Erros podem usar a cor vermelha diretamente do Terminal se desejar, ou usar a secundária
        System.out.println(modo + Terminal.ANSI_RED + mensagem + reset);
        Terminal.aplicaDelay(900);
    }

    // Pergunta em qual dos três slots o jogador deseja salvar ou carregar.
    public int escolherSlot(String titulo) {
        Terminal.limpaTerminal();
        System.out.println(formatarDestaque("=== " + titulo + " ==="));
        System.out.println(formatar("1 - Slot 1"));
        System.out.println(formatar("2 - Slot 2"));
        System.out.println(formatar("3 - Slot 3"));
        System.out.println(formatar("4 - Slot 4"));
        System.out.println(formatar("5 - Continuar último jogo"));
        System.out.println(formatar("0 - Cancelar"));
        System.out.print(formatar("\nEscolha: "));

        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Evita substituir um save existente sem autorização do jogador.
    public boolean confirmarSobrescrita(int slot) {
        System.out.print(formatarDestaque("O slot " + slot + " já possui um save. Substituir? (S/N): "));
        return scanner.nextLine().trim().equalsIgnoreCase("S");
    }

    // Mostra os finais que o jogador já desbloqueou
    public void mostrarGaleriaFinais(java.util.List<String> finaisDesbloqueados) {
        Terminal.limpaTerminal();

        System.out.println(formatarDestaque("========================================"));
        System.out.println(formatarDestaque("           GALERIA DE FINAIS"));
        System.out.println(formatarDestaque("========================================"));
        System.out.println();

        // Final 1
        if (finaisDesbloqueados != null && finaisDesbloqueados.contains("FINAL_TOIN_INDICIADO_BRUNO_VIVO")) {
            System.out.println(formatar("1. A verdade que restou (Toin Indiciado, Bruno Vivo)"));
        } else {
            System.out.println(formatar("1. ??? (Bloqueado)"));
        }

        // Final 2
        if (finaisDesbloqueados != null && finaisDesbloqueados.contains("FINAL_TOIN_INDICIADO_BRUNO_MORTO")) {
            System.out.println(formatar("2. Depois da fumaça (Toin Indiciado, Bruno Morto)"));
        } else {
            System.out.println(formatar("2. ??? (Bloqueado)"));
        }

        // Final 3
        if (finaisDesbloqueados != null && finaisDesbloqueados.contains("FINAL_KATARINA_INDICIADA_BRUNO_VIVO")) {
            System.out.println(formatar("3. Sob suspeita (Katarina Indiciada, Bruno Vivo)"));
        } else {
            System.out.println(formatar("3. ??? (Bloqueado)"));
        }

        // Final 4
        if (finaisDesbloqueados != null && finaisDesbloqueados.contains("FINAL_KATARINA_INDICIADA_BRUNO_MORTO")) {
            System.out.println(formatar("4. O peso da noite (Katarina Indiciada, Bruno Morto)"));
        } else {
            System.out.println(formatar("4. ??? (Bloqueado)"));
        }

        System.out.println();
        System.out.println(formatarDestaque("[Pressione ENTER para voltar]"));
        scanner.nextLine();
    }

    // Mostra a lista de conteúdos extras e verifica se estão bloqueados
    public int openMenuExtras(java.util.List<String> extrasDesbloqueados) {
        Terminal.limpaTerminal();

        System.out.println(formatarDestaque("========================================"));
        System.out.println(formatarDestaque("           CONTEÚDO EXTRA"));
        System.out.println(formatarDestaque("========================================"));
        System.out.println();

        // Extra 1 - Mérito de Fim de Jogo
        if (extrasDesbloqueados != null && extrasDesbloqueados.contains("FIM?")) {
            System.out.println(formatar("1 - O Fim"));
        } else {
            System.out.println(formatar("1 - ??? (Bloqueado)"));
        }

        // Extra 2
        if (extrasDesbloqueados != null && extrasDesbloqueados.contains("ISQUEIRO_TOIN")) {
            System.out.println(formatar("2 - Isqueiro de Toin (Não Registrado)"));
        } else {
            System.out.println(formatar("2 - ??? (Bloqueado)"));
        }

        // Extra 3
        if (extrasDesbloqueados != null && extrasDesbloqueados.contains("BRINQUEDO_BRUNO")) {
            System.out.println(formatar("3 - Brinquedo de Bruno"));
        } else {
            System.out.println(formatar("3 - ??? (Bloqueado)"));
        }

        System.out.println(formatar("0 - Voltar"));

        System.out.print(formatar("\nDigite o número do extra que deseja ler: "));

        try {
            int opcao = scanner.nextInt();
            scanner.nextLine();
            return opcao;
        } catch (InputMismatchException e) {
            scanner.nextLine();
            return -1;
        }
    }


    // Métodos para atualizar as configurações no Controller
    public void setCorPrimaria(String corPrimaria) {
        this.corPrimaria = corPrimaria;
    }

    public void setCorSecundaria(String corSecundaria) {
        this.corSecundaria = corSecundaria;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }

    // Exibe a imagem de um diário velho baseada no arquivo fornecido

}