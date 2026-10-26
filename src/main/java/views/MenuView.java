
package views;

import java.util.InputMismatchException;
import java.util.Scanner;

// Exibe as telas do menu e recebe as opções digitadas pelo jogador.
public class MenuView {

    private final Scanner scanner = new Scanner(System.in);

    // Guarda as preferências de aparência utilizadas nas telas do menu.
    private String corPrimaria;
    private String corSecundaria;
    private String modo;
    private final String reset = Terminal.ANSI_RESET;

    public MenuView(String corPrimaria, String corSecundaria, String modo) {
        this.corPrimaria = corPrimaria;
        this.corSecundaria = corSecundaria;
        this.modo = modo;
    }

    // Aplica a cor principal e o estilo de texto escolhidos pelo jogador.
    private String formatar(String texto) {
        return modo + corPrimaria + texto + reset;
    }

    // Usa a cor secundária para destacar títulos e mensagens importantes.
    private String formatarDestaque(String texto) {
        return modo + corSecundaria + texto + reset;
    }

    // Retorna a opção do menu principal ou -1 caso a entrada não seja numérica.
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

    // Apresenta as duas categorias de conteúdos desbloqueáveis.
    public int openColecionaveisMenu() {
        Terminal.limpaTerminal();

        System.out.println(formatarDestaque("========================================"));
        System.out.println(formatarDestaque("             COLECIONÁVEIS"));
        System.out.println(formatarDestaque("========================================"));
        System.out.println();

        System.out.println(formatar("1 - Galeria de Finais"));
        System.out.println(formatar("2 - Conquistas"));
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

    // Mostra as opções de personalização com uma prévia de cada efeito ANSI.
    public int openConfigMenu() {
        Terminal.limpaTerminal();

        System.out.println(formatarDestaque("========================================"));
        System.out.println(formatarDestaque("             PREFERÊNCIAS"));
        System.out.println(formatarDestaque("========================================"));
        System.out.println();

        System.out.println(Terminal.ANSI_GREEN + "1 - Cor: Verde" + Terminal.ANSI_RESET);
        System.out.println(Terminal.ANSI_CYAN + "2 - Cor: Ciano" + Terminal.ANSI_RESET);
        System.out.println(Terminal.ANSI_YELLOW + "3 - Cor: Amarelo" + Terminal.ANSI_RESET);
        System.out.println(Terminal.ANSI_WHITE + "4 - Cor: Branco" + Terminal.ANSI_RESET);
        System.out.println(Terminal.ANSI_REVERSE + "5 - Alternar Modo Claro/Escuro" + Terminal.ANSI_RESET);
        System.out.println(Terminal.ANSI_BOLD + "6 - Alternar Texto em Negrito" + Terminal.ANSI_RESET);
        System.out.println(Terminal.ANSI_ITALIC + "7 - Alternar Texto em Itálico" + Terminal.ANSI_RESET);
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

    // Solicita o temperamento que será utilizado nas condições e consequências da história.
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

    // Explica as mecânicas principais e aguarda ENTER antes de voltar ao menu.
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
        Terminal.aplicaDelay(3800);

        System.out.println(formatar("Durante um interrogatório conduzido pelo"));
        System.out.println(formatar("detetive Fauan, lembranças do passado"));
        System.out.println(formatar("começam a se misturar ao presente."));
        System.out.println();
        Terminal.aplicaDelay(3800);

        System.out.println(formatar("Suas escolhas determinam como Katarina"));
        System.out.println(formatar("responde, interpreta suas memórias e"));
        System.out.println(formatar("se relaciona com as pessoas envolvidas."));
        System.out.println();
        Terminal.aplicaDelay(4200);

        System.out.println(formatar("Ao longo da investigação, suas decisões"));
        System.out.println(formatar("podem alterar:"));
        System.out.println();
        Terminal.aplicaDelay(2500);

        System.out.println(formatar("- a memória de Katarina;"));
        System.out.println(formatar("- sua estabilidade emocional;"));
        System.out.println(formatar("- sua percepção de culpa;"));
        System.out.println(formatar("- sua relação com outros personagens;"));
        System.out.println(formatar("- as pistas encontradas durante a história."));
        System.out.println();
        Terminal.aplicaDelay(4550);

        System.out.println(formatar("Algumas respostas e caminhos só estarão"));
        System.out.println(formatar("disponíveis caso determinadas condições"));
        System.out.println(formatar("tenham sido alcançadas anteriormente."));
        System.out.println();
        Terminal.aplicaDelay(4200);

        System.out.println(formatar("Por isso, cada decisão pode provocar"));
        System.out.println(formatar("consequências imediatas ou aparecer"));
        System.out.println(formatar("novamente muito tempo depois."));
        System.out.println();
        Terminal.aplicaDelay(3500);

        System.out.println(formatar("Quando uma escolha aparecer, digite o"));
        System.out.println(formatar("número correspondente à opção desejada."));
        System.out.println();
        Terminal.aplicaDelay(4200);

        System.out.println(formatar("Não existe um único caminho."));
        System.out.println(formatar("As decisões tomadas durante a investigação"));
        System.out.println(formatar("podem levar a diferentes conclusões."));
        System.out.println();
        Terminal.aplicaDelay(800);

        System.out.println(formatarDestaque("[Pressione ENTER para voltar ao menu]"));
        scanner.nextLine().trim();
    }

    // Mostra os responsáveis pelo desenvolvimento do jogo.
    public void Creditos() {
        Terminal.limpaTerminal();

        System.out.println(formatarDestaque("========================================"));
        System.out.println(formatarDestaque("               CRÉDITOS"));
        System.out.println(formatarDestaque("========================================"));
        System.out.println();

        System.out.println(formatar("Desenvolvido por:"));
        Terminal.aplicaDelay(850);
        System.out.println();

        System.out.println(formatar("Bruno Carneiro"));
        Terminal.aplicaDelay(1000);

        System.out.println(formatar("Raphael Henrique"));
        System.out.println();
        Terminal.aplicaDelay(1000);

        System.out.println(formatarDestaque("Obrigado por jogar!"));
        System.out.println();

        System.out.println(formatarDestaque("[Pressione ENTER para voltar ao menu]"));
        scanner.nextLine();
    }

    // Destaca mensagens de erro em vermelho e dá tempo para o jogador visualizá-las.
    public void apresentaErro(String mensagem) {
        System.out.println();
        System.out.println(modo + Terminal.ANSI_RED + mensagem + reset);
        Terminal.aplicaDelay(900);
    }

    // Lista os quatro slots usando os metadados recebidos do Controller.
    // Retorna o número selecionado, 0 para cancelar ou -1 para uma entrada inválida.
    public int escolherSlot(String titulo, String[] nomesSaves, boolean mostrarContinuar) {
        Terminal.limpaTerminal();
        System.out.println(formatarDestaque("=== " + titulo + " ==="));

        // Percorre os slots e identifica quais possuem um save registrado.
        for (int i = 0; i < nomesSaves.length; i++) {
            String nome = nomesSaves[i];
            String textoSlot = nome.equals("Vazio") ? "Vazio" : "Save: " + nome;

            System.out.println(
                    formatar((i + 1) + " - Slot " + (i + 1) + " [" + textoSlot + "]")
            );
        }

        // A opção de exclusão aparece apenas no menu de carregamento.
        if (mostrarContinuar) {
            System.out.println(formatar("5 - Excluir save"));
        }

        System.out.println(formatar("0 - Cancelar"));
        System.out.print(formatar("\nEscolha: "));

        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Recebe o nome personalizado que será associado ao salvamento.
    // nextLine permite que o apelido contenha espaços.
    public String pegaNomeSave() {
        System.out.print(formatar("\nInforme o seu apelido (nome do save): "));
        return scanner.nextLine().trim();
    }

    // Solicita o número do slot que o jogador deseja excluir.
    public int excluirSaveView() {
        System.out.print(formatarDestaque(
                "\nQual save deseja excluir (1 a 4 | 0 para cancelar)? "
        ));

        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Confirma a exclusão mostrando o slot e seus metadados para evitar remoções acidentais.
    public boolean confirmarExclusao(int slot, String nomeSave) {
        System.out.print(formatarDestaque(
                "Tem certeza de que deseja excluir o Slot "
                + slot + " [" + nomeSave + "]? (S/N): "
        ));

        return scanner.nextLine().trim().equalsIgnoreCase("S");
    }

    // Pede confirmação antes de substituir uma partida salva no mesmo slot.
    public boolean confirmarSobrescrita(int slot) {
        System.out.print(formatarDestaque(
                "O slot " + slot + " já possui um save. Substituir? (S/N): "
        ));

        return scanner.nextLine().trim().equalsIgnoreCase("S");
    }

    // Exibe os finais presentes no perfil global e oculta os que ainda não foram alcançados.
    public void mostrarGaleriaFinais(java.util.List<String> finaisDesbloqueados) {
        Terminal.limpaTerminal();

        System.out.println(formatarDestaque("========================================"));
        System.out.println(formatarDestaque("           GALERIA DE FINAIS"));
        System.out.println(formatarDestaque("========================================"));
        System.out.println();

        if (finaisDesbloqueados != null
                && finaisDesbloqueados.contains("FINAL_TOIN_INDICIADO_BRUNO_VIVO")) {

            System.out.println(formatar(
                    "1. A verdade que restou (Toin Indiciado, Bruno Vivo)"
            ));
        } else {
            System.out.println(formatar("1. ??? (Bloqueado)"));
        }

        if (finaisDesbloqueados != null
                && finaisDesbloqueados.contains("FINAL_TOIN_INDICIADO_BRUNO_MORTO")) {

            System.out.println(formatar(
                    "2. Depois da fumaça (Toin Indiciado, Bruno Morto)"
            ));
        } else {
            System.out.println(formatar("2. ??? (Bloqueado)"));
        }

        if (finaisDesbloqueados != null
                && finaisDesbloqueados.contains("FINAL_KATARINA_INDICIADA_BRUNO_VIVO")) {

            System.out.println(formatar(
                    "3. Sob suspeita (Katarina Indiciada, Bruno Vivo)"
            ));
        } else {
            System.out.println(formatar("3. ??? (Bloqueado)"));
        }

        if (finaisDesbloqueados != null
                && finaisDesbloqueados.contains("FINAL_KATARINA_INDICIADA_BRUNO_MORTO")) {

            System.out.println(formatar(
                    "4. O peso da noite (Katarina Indiciada, Bruno Morto)"
            ));
        } else {
            System.out.println(formatar("4. ??? (Bloqueado)"));
        }

        System.out.println();
        System.out.println(formatarDestaque("[Pressione ENTER para voltar]"));
        scanner.nextLine();
    }

    // Mostra os conteúdos extras e consulta suas chaves para indicar quais estão liberados.
    public int openMenuExtras(java.util.List<String> extrasDesbloqueados) {
        Terminal.limpaTerminal();

        System.out.println(formatarDestaque("========================================"));
        System.out.println(formatarDestaque("           CONTEÚDO EXTRA"));
        System.out.println(formatarDestaque("========================================"));
        System.out.println();

        if (extrasDesbloqueados != null
                && extrasDesbloqueados.contains("FIM?")) {

            System.out.println(formatar("1 - O Fim"));
        } else {
            System.out.println(formatar("1 - ??? (Bloqueado)"));
        }

        if (extrasDesbloqueados != null
                && extrasDesbloqueados.contains("ISQUEIRO_TOIN")) {

            System.out.println(formatar("2 - Isqueiro de Toin (Não Registrado)"));
        } else {
            System.out.println(formatar("2 - ??? (Bloqueado)"));
        }

        if (extrasDesbloqueados != null
                && extrasDesbloqueados.contains("BRINQUEDO_BRUNO")) {

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

    // Atualiza as preferências visuais utilizadas nos próximos menus.
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
