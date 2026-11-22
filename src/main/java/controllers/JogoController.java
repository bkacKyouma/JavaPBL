package controllers;

import java.io.IOException;
import java.util.List;
import models.Capitulo;
import models.Cena;
import models.DadosPartida;
import models.Dialogo;
import models.Escolha;
import models.EstadoJogo;
import models.Personagem;
import models.Pista;
import models.Protagonista;
import models.Psecundario;
import persistence.PersistenciaJogo;
import views.JogoView;
import views.MenuView;
import views.Terminal;

// Coordena a partida: cria o conteúdo narrativo, aplica regras especiais e move o EstadoJogo entre as cenas.
public class JogoController {

    private JogoView jogoView;
    private MenuView menuView;
    private EstadoJogo estadoJogo;

    // Referências dos personagens secundários usadas na criação dos diálogos e cenas.
    private Psecundario fauan;
    private Psecundario passos;
    private Psecundario toin;
    private Psecundario bruno;

    // Pistas criadas uma única vez e entregues à protagonista quando suas condições são cumpridas.
    private Pista pistaCheiroGasolina;
    private Pista pistaSombraDesconhecida;
    private Pista pistaAmeacaBruno;
    private Pista pistaPerguntaAlarme;
    private Pista pistaAlarmeDesligado;

    // Guarda se Bruno sobreviveu para combinar esse resultado com o veredito no final.
    private boolean brunoSobreviveu;

    private final PersistenciaJogo persistencia = new PersistenciaJogo();

    // Inicializa as views da partida com as configurações de cor e modo ativas no menu.
    public JogoController(String corPrimaria, String corSecundaria, String modo) {
        this.jogoView = new JogoView(corPrimaria, corSecundaria, modo);
        this.menuView = new MenuView(corPrimaria, corSecundaria, modo);
    }

    // Prepara uma nova partida e inicia o loop de terminal da partida.
    public void iniciarJogo(int opcaoTemperamento) {
        prepararNovaPartida(opcaoTemperamento);
        executarJogo();
    }

    // Converte o temperamento, cria Katarina e monta a história, sem iniciar nenhum loop (usado também pela GUI).
    public void prepararNovaPartida(int opcaoTemperamento) {

        brunoSobreviveu = false;

        String temperamento =
                converterTemperamento(opcaoTemperamento);

        Protagonista katarina =
                new Protagonista(
                        1,
                        "Katarina",
                        temperamento
                );

        estadoJogo = new EstadoJogo(katarina);

        criarPersonagens();
        registrarSpritesPersonagens();
        criarPistas();
        construirHistoria();

        estadoJogo.iniciarJogo();
    }

    // Lê o JSON do slot informado, reconstrói a protagonista e retoma a partida da cena salva.
    public void carregarJogo(int slot) {
        try {
            carregarPartida(slot);
            executarJogo();
        } catch (IOException e) {
            this.jogoView.apresentaErro("Não foi possível carregar: " + e.getMessage());
        }
    }

    // Restaura o estado salvo no slot sem iniciar loop algum; falhas viram IOException com mensagem legível.
    public void carregarPartida(int slot) throws IOException {
        DadosPartida dados = persistencia.carregar(slot);
        if (dados == null) {
            throw new IOException("Não existe partida salva no slot " + slot);
        }

        Protagonista katarina = dados.restaurarProtagonista();
        estadoJogo = new EstadoJogo(katarina);
        criarPersonagens();
        registrarSpritesPersonagens();
        criarPistas();
        construirHistoria();

        if (!estadoJogo.irParaCena(dados.cenaAtualId)
                || estadoJogo.getCapituloAtual().getId() != dados.capituloAtualId) {
            estadoJogo = null;
            throw new IOException("A cena guardada no save não foi encontrada.");
        }

        brunoSobreviveu = dados.brunoSobreviveu;
    }

    // Exibe a lista de slots com seus nomes atuais, solicita o apelido e grava a partida.
    private void salvarManual() {
        String[] savesDisponiveis = persistencia.listarNomesSaves();
        int slot = this.menuView.escolherSlot("SALVAR PARTIDA", savesDisponiveis, false);
        if (slot == 0) return;
        if (slot < 1 || slot > PersistenciaJogo.TOTAL_SLOTS) {
            this.jogoView.apresentaErro("Slot inválido.");
            return;
        }

        if (persistencia.existe(slot) && !this.menuView.confirmarSobrescrita(slot)) {
            return;
        }

        String nomeSave = this.jogoView.pegaNomeSave();
        DadosPartida dados = DadosPartida.capturar(estadoJogo, brunoSobreviveu, slot);
        try {
            persistencia.salvar(slot, dados, nomeSave);
            Terminal.enviaMensagem("Partida salva no slot " + slot + ".");
            views.Terminal.aplicaDelay(900);
        } catch (IOException e) {
            this.jogoView.apresentaErro("Erro ao salvar: " + e.getMessage());
        }
    }

    // Salva automaticamente o progresso no slot 1 após cada avanço de escolha.
    private void salvaAuto() {
        int slot = 1;

        DadosPartida dados = DadosPartida.capturar(estadoJogo, brunoSobreviveu, slot);
        try {
            persistencia.salvar(slot, dados, "auto save");
            Terminal.enviaMensagem("Auto save no slot " + slot + ".");
            views.Terminal.aplicaDelay(600);
        } catch (IOException e) {
            this.jogoView.apresentaErro("Erro ao salvar: " + e.getMessage());
        }
    }

    // Converte a opção numérica do menu para a String de temperamento usada nas condições.
    private String converterTemperamento(int opcao) {

        if (opcao == 1) {
            return "SANGUE_FRIO";
        }

        if (opcao == 2) {
            return "EQUILIBRADO";
        }

        return "SANGUE_QUENTE";
    }

    // Cria Fauan, Passos, Toin e Bruno e registra todos no EstadoJogo.
    private void criarPersonagens() {

        fauan = new Psecundario(2, "Fauan");

        passos = new Psecundario(3, "Passos");

        toin = new Psecundario(4, "Toin");

        bruno = new Psecundario(5, "Bruno");

        estadoJogo.adicionarPersonagem(fauan);
        estadoJogo.adicionarPersonagem(passos);
        estadoJogo.adicionarPersonagem(toin);
        estadoJogo.adicionarPersonagem(bruno);
    }

    // Registra apenas os sprites isolados por humor para protagonista e personagens secundários.
    private void registrarSpritesPersonagens() {
        if (estadoJogo == null) {
            return;
        }

        Protagonista katarina = estadoJogo.getProtagonista();
        if (katarina != null) {
            katarina.adicionarSprite("assustada", "katarina_assustada.png");
            katarina.adicionarSprite("brava", "katarina_brava.png");
            katarina.adicionarSprite("chorando", "katarina_chorando.png");
            katarina.adicionarSprite("desinteressada", "katarina_desinsteressada.png");
            katarina.adicionarSprite("destruida", "katarina_destruida.png");
            katarina.adicionarSprite("feliz", "katarina_feliz.png");
            katarina.adicionarSprite("pensativa", "katarina_pensativa.png");
            katarina.adicionarSprite("seria", "katarina_seria.png");
        }

        if (fauan != null) {
            fauan.adicionarSprite("desconfiado", "fauan_desconfiado.png");
            fauan.adicionarSprite("na_prancheta", "fauan_na_prancheta.png");
            fauan.adicionarSprite("surpreso", "fauan_surpreso.png");
        }

        if (passos != null) {
            passos.adicionarSprite("bravo", "passos_bravo.png");
            passos.adicionarSprite("desconfiado", "passos_desconfiado.png");
            passos.adicionarSprite("feliz", "passos_feliz.png");
            passos.adicionarSprite("serio", "passos_serio.png");
        }

        if (toin != null) {
            toin.adicionarSprite("bravo", "toin_bravo.png");
            toin.adicionarSprite("desconfiado", "toin_desconfiado.png");
            toin.adicionarSprite("feliz", "toin_feliz.png");
            toin.adicionarSprite("pensativo", "toin_pensativo.png");
        }

        if (bruno != null) {
            bruno.adicionarSprite("assustado", "bruno_assustado.png");
            bruno.adicionarSprite("feliz", "bruno_feliz.png");
            bruno.adicionarSprite("medo", "bruno_medo.png");
            bruno.adicionarSprite("triste", "bruno_triste.png");
        }
    }

    // Instancia as pistas que podem ser obtidas ao longo dos capítulos.
    private void criarPistas() {

        pistaCheiroGasolina =
                new Pista(
                        "Cheiro de gasolina",
                        "Katarina lembra de um cheiro de gasolina antes da fumaça."
                );

        pistaSombraDesconhecida =
                new Pista(
                        "Sombra desconhecida",
                        "Katarina lembra de uma sombra próxima ao quarto de Bruno."
                );

        pistaAmeacaBruno =
                new Pista(
                        "Ameaça velada ao Bruno",
                        "Durante uma ligação, Toin disse algo sobre Bruno que deixou Katarina inquieta."
                );

        pistaPerguntaAlarme =
                new Pista(
                        "Pergunta sobre o alarme",
                        "Toin perguntou se o alarme de incêndio ainda funcionava."
                );

        pistaAlarmeDesligado =
                new Pista(
                        "Alarme desligado manualmente",
                        "A perícia concluiu que o alarme do quarto de Bruno foi desligado manualmente."
                );
    }

    // Constrói os cinco capítulos antes de iniciar a navegação entre as cenas.
    private void construirHistoria() {

        construirCapitulo1();
        construirCapitulo2();
        construirCapitulo3();
        construirCapitulo4();
        construirCapitulo5();
    }

    // Cria uma escolha recebendo os deltas na ordem da tabela do roteiro e repassando para definirEfeitos().
    private Escolha criarEscolha(
            int id,
            String texto,
            int proximaCena,
            int memoria,
            int confFauan,
            int vincPassos,
            int suspToin,
            int estabEmoc,
            int culpaPerc) {

        Escolha escolha =
                new Escolha(
                        id,
                        texto,
                        proximaCena
                );

        escolha.definirEfeitos(
                memoria,
                estabEmoc,
                culpaPerc,
                confFauan,
                vincPassos,
                suspToin
        );

        return escolha;
    }

    // Cria uma escolha padrão e associa o tom emocional correspondente à resposta.
    private Escolha criarEscolhaComTom(
            int id,
            String texto,
            int proximaCena,
            int memoria,
            int confFauan,
            int vincPassos,
            int suspToin,
            int estabEmoc,
            int culpaPerc,
            String tom) {

        Escolha escolha =
                criarEscolha(
                        id,
                        texto,
                        proximaCena,
                        memoria,
                        confFauan,
                        vincPassos,
                        suspToin,
                        estabEmoc,
                        culpaPerc
                );

        escolha.definirTom(tom);

        return escolha;
    }

    // Capítulo 1: monta o primeiro interrogatório e as primeiras pistas sobre o incêndio.
    private void construirCapitulo1() {

        Capitulo capitulo =
                new Capitulo(
                        1,
                        "A NOITE QUE NÃO SE APAGA"
                );

        // Cena 1: Fauan questiona a reação de Katarina durante a fuga do incêndio.
        Cena cena101 =
                new Cena(
                        101,
                        "Bloco 1 - A fuga",
                        "Sala de interrogatório da delegacia. "
                                + "Katarina ainda está com cheiro de fumaça na roupa. "
                                + "Fauan permanece de pé diante dela.",
                        "INTERROGATORIO"
                );
        cena101.setImagem("interrogatorio.jpg");

        cena101.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "No meio do incêndio, você correu. "
                                + "Só que correu pra longe do seu filho, não pra perto.",
                        "FALA",
                        "desconfiado"
                )
        );

        Escolha c101a =
                criarEscolha(
                        1,
                        "Eu congelei. Não é a mesma coisa que fugir.",
                        111,

                        15,
                        10,
                        0,
                        0,
                        10,
                        10
                );

        Escolha c101b =
                criarEscolha(
                        2,
                        "Eu tentei. Eu juro que tentei chegar até ele.",
                        112,

                        5,
                        -10,
                        0,
                        0,
                        -10,
                        -15
                );

        cena101.adicionarEscolha(c101a);
        cena101.adicionarEscolha(c101b);

        Cena cena111 =
                new Cena(
                        111,
                        "A resposta de Fauan",
                        "Fauan ajeita a postura, incomodado. "
                                + "Ele não esperava uma resposta tão sincera.",
                        "INTERROGATORIO"
                );
        cena111.setImagem("interrogatorio.jpg");

        cena111.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Pra mim, agora, é a mesma coisa. "
                                + "Me convence do contrário.",
                        "FALA",
                        "desconfiado"
                )
        );

        cena111.definirProximaCena(102);

        Cena cena112 =
                new Cena(
                        112,
                        "A resposta de Fauan",
                        "Fauan anota algo sem tirar os olhos dela. "
                                + "A resposta veio rápida demais.",
                        "INTERROGATORIO"
                );
        cena112.setImagem("interrogatorio.jpg");

        cena112.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Pra mim, agora, é a mesma coisa. "
                                + "Me convence do contrário.",
                        "FALA",
                        "na_prancheta"
                )
        );

        cena112.definirProximaCena(102);

        // Cena 2: o interrogatório passa para a briga entre Katarina e Passos.
        Cena cena102 =
                new Cena(
                        102,
                        "Bloco 2 - A briga",
                        "Fauan vira uma página do relatório.",
                        "INTERROGATORIO"
                );
        cena102.setImagem("interrogatorio.jpg");

        cena102.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Os vizinhos disseram que ouviram vocês dois brigando "
                                + "antes da fumaça começar. Sobre o quê?",
                        "FALA",
                        "na_prancheta"
                )
        );

        Escolha c102a =
                criarEscolha(
                        1,
                        "Sobre Passos. Sobre o que ele andava fazendo "
                                + "enquanto eu tava grávida do Bruno.",
                        121,

                        15,
                        5,
                        -15,
                        0,
                        -5,
                        5
                );

        Escolha c102b =
                criarEscolha(
                        2,
                        "Briga de casal. Não tinha nada a ver com aquela noite.",
                        122,

                        5,
                        -15,
                        10,
                        0,
                        0,
                        0
                );

        cena102.adicionarEscolha(c102a);
        cena102.adicionarEscolha(c102b);

        Cena cena121 =
                new Cena(
                        121,
                        "A traição dita em voz alta",
                        "O silêncio pesa. "
                                + "É a primeira vez que Katarina nomeia a traição em voz alta.",
                        "INTERROGATORIO"
                );
        cena121.setImagem("interrogatorio.jpg");

        cena121.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Toda briga tem a ver com a noite "
                                + "em que a casa pega fogo, Katarina.",
                        "FALA",
                        "desconfiado"
                )
        );

        cena121.definirProximaCena(103);

        Cena cena122 =
                new Cena(
                        122,
                        "Uma mentira registrada",
                        "Fauan já sabia da traição por outra fonte. "
                                + "A resposta de Katarina vira uma mentira registrada.",
                        "INTERROGATORIO"
                );
        cena122.setImagem("interrogatorio.jpg");

        cena122.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Toda briga tem a ver com a noite "
                                + "em que a casa pega fogo, Katarina.",
                        "FALA",
                        "na_prancheta"
                )
        );

        cena122.definirProximaCena(103);

        // Cena 3: Fauan confronta Katarina sobre o resgate realizado por Toin.
        Cena cena103 =
                new Cena(
                        103,
                        "Bloco 3 - O resgate",
                        "Fauan observa Katarina por alguns segundos.",
                        "INTERROGATORIO"
                );
        cena103.setImagem("interrogatorio.jpg");

        cena103.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Toin diz que chegou antes dos bombeiros. "
                                + "Que foi ele quem tirou você de dentro de casa. "
                                + "Isso bate com o que você lembra?",
                        "FALA",
                        "desconfiado"
                )
        );

        Escolha c103a =
                criarEscolha(
                        1,
                        "Eu... não sei mais o que eu lembro "
                                + "e o que ele me contou depois.",
                        131,

                        20,
                        15,
                        0,
                        15,
                        -10,
                        0
                );

        Escolha c103b =
                criarEscolha(
                        2,
                        "Bate. Toin sempre aparece na hora certa.",
                        132,

                        5,
                        -10,
                        0,
                        0,
                        0,
                        0
                );

        cena103.adicionarEscolha(c103a);
        cena103.adicionarEscolha(c103b);

        Cena cena131 =
                new Cena(
                        131,
                        "Memórias emprestadas",
                        "Katarina percebe que algumas lembranças "
                                + "podem ter sido reconstruídas depois do incêndio.",
                        "INTERROGATORIO"
                );
        cena131.setImagem("interrogatorio.jpg");

        cena131.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Isso é mais honesto do que qualquer coisa "
                                + "que você me disse até agora.",
                        "FALA",
                        "surpreso"
                )
        );

        cena131.definirProximaCena(104);

        Cena cena132 =
                new Cena(
                        132,
                        "Na hora certa",
                        "Fauan repete mentalmente as palavras de Katarina.",
                        "INTERROGATORIO"
                );
        cena132.setImagem("interrogatorio.jpg");

        cena132.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "'Na hora certa.' Interessante escolha de palavras "
                                + "pra descrever um resgate.",
                        "FALA",
                        "desconfiado"
                )
        );

        cena132.definirProximaCena(104);

        // Cena 4: a lembrança do cheiro de gasolina pode virar uma pista da investigação.
        Cena cena104 =
                new Cena(
                        104,
                        "Bloco 4 - O que não estava no relatório",
                        "A sala fica ainda mais silenciosa.",
                        "INTERROGATORIO"
                );
        cena104.setImagem("interrogatorio.jpg");

        cena104.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Me diz uma coisa que só você poderia saber "
                                + "sobre aquela noite. "
                                + "Uma coisa que não tá no meu relatório.",
                        "FALA",
                        "na_prancheta"
                )
        );

        Escolha c104a =
                criarEscolha(
                        1,
                        "O cheiro. Antes da fumaça, tinha um cheiro diferente. "
                                + "Gasolina, eu acho. Isso não devia estar lá.",
                        141,

                        25,
                        20,
                        0,
                        10,
                        5,
                        -5
                );

        c104a.definirPistaGanha(
                pistaCheiroGasolina
        );

        Escolha c104b =
                criarEscolha(
                        2,
                        "Eu não sei. Talvez eu esteja esquecendo de propósito.",
                        142,

                        5,
                        -15,
                        0,
                        0,
                        -15,
                        10
                );

        cena104.adicionarEscolha(c104a);
        cena104.adicionarEscolha(c104b);

        Cena cena141 =
                new Cena(
                        141,
                        "O cheiro",
                        "Fauan anota e, pela primeira vez, "
                                + "parece genuinamente perturbado.",
                        "INTERROGATORIO"
                );
        cena141.setImagem("interrogatorio.jpg");

        cena141.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Isso não está no meu relatório porque "
                                + "ninguém tinha mencionado ainda.",
                        "FALA",
                        "surpreso"
                )
        );

        cena141.definirProximaCena(105);

        Cena cena142 =
                new Cena(
                        142,
                        "Esquecer de propósito",
                        "Fauan não parece convencido.",
                        "INTERROGATORIO"
                );
        cena142.setImagem("interrogatorio.jpg");

        cena142.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Ou talvez você já soubesse a resposta "
                                + "e não quisesse me dar.",
                        "FALA",
                        "desconfiado"
                )
        );

        cena142.definirProximaCena(105);

        // Cena 5: Katarina relata o que viu antes da fumaça e pode confrontar Toin se cumprir as condições.
        Cena cena105 =
                new Cena(
                        105,
                        "Bloco 5 - Antes da fumaça",
                        "Fauan fecha parcialmente a pasta.",
                        "INTERROGATORIO"
                );
        cena105.setImagem("interrogatorio.jpg");

        cena105.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Última pergunta, Katarina. "
                                + "O que você viu, de verdade, "
                                + "antes de tudo virar fumaça?",
                        "FALA",
                        "desconfiado"
                )
        );

        Escolha c105a =
                criarEscolha(
                        1,
                        "Uma sombra na porta do quarto do Bruno. "
                                + "Eu acho que não era Passos.",
                        106,

                        20,
                        5,
                        15,
                        15,
                        0,
                        0
                );

        c105a.definirPistaGanha(
                pistaSombraDesconhecida
        );

        Escolha c105b =
                criarEscolha(
                        2,
                        "Eu vi meu filho. Só isso. "
                                + "Só ele importava naquele momento.",
                        106,

                        10,
                        10,
                        0,
                        0,
                        10,
                        0
                );

        // A terceira opção só aparece quando a suspeita de Toin e a pista exigida já foram alcançadas.
        Escolha c105c =
                criarEscolha(
                        3,
                        "Tem alguma coisa errada nessa história, Fauan. "
                                + "Eu quero saber o que Toin estava fazendo ali.",
                        106,

                        30,
                        25,
                        10,
                        25,
                        15,
                        -20
                );

        c105c.definirCondicao(
                "Toin",
                65,
                null
        );

        c105c.adicionarPistaNecessaria(
                "Cheiro de gasolina"
        );

        cena105.adicionarEscolha(c105a);
        cena105.adicionarEscolha(c105b);
        cena105.adicionarEscolha(c105c);

        Cena cena106 =
                new Cena(
                        106,
                        "O fim do primeiro interrogatório",
                        "A memória se fecha em torno de um único ponto. "
                                + "Do lado de fora da sala, alguém bate à porta.",
                        "NARRATIVA"
                );
        cena106.setImagem("corredor_fumaça.jpg");

        cena106.definirProximaCena(201);

        // Registra todas as cenas do capítulo na ordem em que podem ser buscadas pelo EstadoJogo.
        capitulo.adicionarCena(cena101);
        capitulo.adicionarCena(cena111);
        capitulo.adicionarCena(cena112);

        capitulo.adicionarCena(cena102);
        capitulo.adicionarCena(cena121);
        capitulo.adicionarCena(cena122);

        capitulo.adicionarCena(cena103);
        capitulo.adicionarCena(cena131);
        capitulo.adicionarCena(cena132);

        capitulo.adicionarCena(cena104);
        capitulo.adicionarCena(cena141);
        capitulo.adicionarCena(cena142);

        capitulo.adicionarCena(cena105);
        capitulo.adicionarCena(cena106);

        estadoJogo.adicionarCapitulo(capitulo);
    }

    // Capítulo 2: monta os flashbacks dos três dias anteriores e aplica escolhas com tom.
    private void construirCapitulo2() {

        Capitulo capitulo =
                new Capitulo(
                        2,
                        "TRÊS DIAS ANTES"
                );

        // Cena 1: as visitas inesperadas de Toin introduzem a mecânica de tom deste capítulo.
        Cena cena201 =
                new Cena(
                        201,
                        "Visitas de surpresa",
                        "Toin aparece em casa de surpresa por duas noites seguidas, "
                                + "sempre quando Passos viaja a trabalho.",
                        "FLASHBACK"
                );
        cena201.setImagem("sala_de_noite_flashback.jpg");

        cena201.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "E isso era comum? "
                                + "Ele aparecer assim, sem avisar?",
                        "FALA",
                        "desconfiado"
                )
        );

        Escolha c201a =
                criarEscolhaComTom(
                        1,
                        "Toin sempre gostou de aparecer sem avisar. "
                                + "Isso nunca significou nada.",
                        202,

                        10,
                        0,
                        0,
                        -5,
                        0,
                        0,

                        "SANGUE_FRIO"
                );

        Escolha c201b =
                criarEscolhaComTom(
                        2,
                        "Era estranho, sim. "
                                + "Mas eu nunca parei pra pensar no porquê.",
                        202,

                        15,
                        5,
                        0,
                        10,
                        0,
                        0,

                        "EQUILIBRADO"
                );

        Escolha c201c =
                criarEscolhaComTom(
                        3,
                        "Ele fazia isso porque sabia que eu tava sozinha! "
                                + "E eu deixava, de fato.",
                        202,

                        15,
                        -5,
                        0,
                        15,
                        -10,
                        5,

                        "SANGUE_QUENTE"
                );

        cena201.adicionarEscolha(c201a);
        cena201.adicionarEscolha(c201b);
        cena201.adicionarEscolha(c201c);

        // Cena 2: a lembrança do perfume evidencia a traição de Passos.
        Cena cena202 =
                new Cena(
                        202,
                        "O perfume que não era o dela",
                        "Passos chega tarde, cheirando a um perfume "
                                + "que não é o de Katarina.",
                        "FLASHBACK"
                );
        cena202.setImagem("sala_de_noite_flashback.jpg");

        cena202.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Você sentiu o cheiro e não disse nada. Por quê?",
                        "FALA",
                        "desconfiado"
                )
        );

        Escolha c202a =
                criarEscolhaComTom(
                        1,
                        "Porque perguntar não ia mudar a resposta. "
                                + "Eu já sabia.",
                        203,

                        10,
                        5,
                        -10,
                        0,
                        0,
                        -5,

                        "SANGUE_FRIO"
                );

        Escolha c202b =
                criarEscolhaComTom(
                        2,
                        "Eu tava grávida, cansada. "
                                + "Não tinha força pra mais uma briga.",
                        203,

                        10,
                        0,
                        0,
                        0,
                        5,
                        0,

                        "EQUILIBRADO"
                );

        Escolha c202c =
                criarEscolhaComTom(
                        3,
                        "Porque se eu perguntasse, "
                                + "eu ia ter que fazer alguma coisa a respeito. "
                                + "E eu não tava pronta.",
                        203,

                        15,
                        -10,
                        -15,
                        0,
                        -10,
                        15,

                        "SANGUE_QUENTE"
                );

        cena202.adicionarEscolha(c202a);
        cena202.adicionarEscolha(c202b);
        cena202.adicionarEscolha(c202c);

        // Cena 3: a ligação de Toin pode liberar uma escolha exclusiva de sangue frio.
        Cena cena203 =
                new Cena(
                        203,
                        "A ligação no banheiro",
                        "O telefone toca. É Toin. "
                                + "Katarina atende escondida no banheiro.",
                        "FLASHBACK"
                );
        cena203.setImagem("banheiro.jpg");

        cena203.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Por que uma ligação de um amigo da família "
                                + "precisa ser atendida com a porta trancada?",
                        "FALA",
                        "desconfiado"
                )
        );

        Escolha c203a =
                criarEscolhaComTom(
                        1,
                        "Não precisava. "
                                + "Eu só queria um minuto sozinha. Isso é crime?",
                        204,

                        10,
                        -5,
                        0,
                        5,
                        0,
                        0,

                        "SANGUE_FRIO"
                );

        Escolha c203b =
                criarEscolhaComTom(
                        2,
                        "Eu não sei. Foi automático. "
                                + "Eu nem percebi que tinha trancado a porta.",
                        204,

                        10,
                        5,
                        0,
                        5,
                        0,
                        0,

                        "EQUILIBRADO"
                );

        Escolha c203c =
                criarEscolhaComTom(
                        3,
                        "Porque eu sabia como ia parecer! "
                                + "E olha só, eu tava certa!",
                        204,

                        10,
                        -10,
                        0,
                        10,
                        -15,
                        10,

                        "SANGUE_QUENTE"
                );

        Escolha c203d =
                criarEscolha(
                        4,
                        "Eu tranquei a porta porque, nessa ligação, "
                                + "ele disse uma coisa sobre o Bruno "
                                + "que me deixou gelada.",
                        204,

                        25,
                        15,
                        0,
                        25,
                        -5,
                        -10
                );

        // Usa exatamente "SANGUE_FRIO" porque a condição compara essa String com o temperamento da protagonista.
        c203d.definirCondicao(
                "Toin",
                40,
                "SANGUE_FRIO"
        );

        c203d.definirPistaGanha(
                pistaAmeacaBruno
        );

        cena203.adicionarEscolha(c203a);
        cena203.adicionarEscolha(c203b);
        cena203.adicionarEscolha(c203c);
        cena203.adicionarEscolha(c203d);

        // Encerra o capítulo 2 e encaminha automaticamente para o capítulo 3.
        Cena cena204 =
                new Cena(
                        204,
                        "O padrão",
                        "Fauan larga a caneta na mesa.",
                        "INTERROGATORIO"
                );
        cena204.setImagem("interrogatorio.jpg");

        cena204.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Nenhuma dessas coisas, sozinha, é motivo pra nada. "
                                + "Mas você mesma não acredita mais "
                                + "que era tudo nada.",
                        "FALA",
                        "na_prancheta"
                )
        );

        cena204.definirProximaCena(301);

        capitulo.adicionarCena(cena201);
        capitulo.adicionarCena(cena202);
        capitulo.adicionarCena(cena203);
        capitulo.adicionarCena(cena204);

        estadoJogo.adicionarCapitulo(capitulo);
    }

    // Capítulo 3: mostra a origem da relação entre Passos e Toin sem participação de Fauan.
    private void construirCapitulo3() {

        Capitulo capitulo =
                new Capitulo(
                        3,
                        "ORIGEM: DOIS MENINOS"
                );

        Cena cena301 =
                new Cena(
                        301,
                        "A primeira conversa",
                        "Passos e Toin se conhecem desde crianças. "
                                + "Anos depois, Passos apresenta Katarina ao melhor amigo.",
                        "FLASHBACK"
                );
        cena301.setImagem("pub.jpg");

        cena301.adicionarDialogo(
                new Dialogo(
                        passos.getNome(),
                        "Toin, essa é a Katarina. "
                                + "Katarina, esse aqui é meu irmão de escolha.",
                        "FALA",
                        "feliz"
                )
        );

        cena301.adicionarDialogo(
                new Dialogo(
                        toin.getNome(),
                        "Então você é a mulher que finalmente fez "
                                + "esse aí parar de falar de futebol "
                                + "pra falar de outra coisa.",
                        "FALA",
                        "feliz"
                )
        );

        // Não usa definirTom() porque Fauan não participa deste flashback; o ajuste de estabilidade é feito no Controller.
        Escolha c301a =
                criarEscolha(
                        1,
                        "Ele contou o suficiente. "
                                + "O resto eu prefiro descobrir sozinha.",
                        302,

                        5,
                        0,
                        5,
                        0,
                        0,
                        0
                );

        Escolha c301b =
                criarEscolha(
                        2,
                        "Contou um pouco. "
                                + "Parece que vocês dois têm história pra série de TV.",
                        302,

                        5,
                        0,
                        10,
                        -5,
                        5,
                        0
                );

        Escolha c301c =
                criarEscolha(
                        3,
                        "Se tiver partes ruins já conta logo! "
                                + "Eu preciso saber com quem tô lidando aqui.",
                        302,

                        5,
                        0,
                        5,
                        0,
                        5,
                        0
                );

        cena301.adicionarEscolha(c301a);
        cena301.adicionarEscolha(c301b);
        cena301.adicionarEscolha(c301c);

        Cena cena302 =
                new Cena(
                        302,
                        "Vinte anos sem um arranhão",
                        "Toin é padrinho do casamento de Katarina e Passos. "
                                + "Depois, quando o teste de gravidez dá positivo, "
                                + "Passos liga para ele antes mesmo dos próprios pais.",
                        "FLASHBACK"
                );
        cena302.setImagem("pub.jpg");

        cena302.adicionarDialogo(
                new Dialogo(
                        toin.getNome(),
                        "Que INCRÍVEL notícia, mano. "
                                + "Não sei nem expressar o quão feliz fico por vocês.",
                        "FALA",
                        "feliz"
                )
        );

        cena302.definirProximaCena(401);

        capitulo.adicionarCena(cena301);
        capitulo.adicionarCena(cena302);

        estadoJogo.adicionarCapitulo(capitulo);
    }

    // Capítulo 4: concentra as suspeitas sobre Toin, a paternidade e as pistas do alarme.
    private void construirCapitulo4() {

        Capitulo capitulo =
                new Capitulo(
                        4,
                        "O QUEIXO DO TOIN"
                );

        // Cena 1: a fala de Passos sobre Bruno aumenta o conflito em torno de Toin.
        Cena cena401 =
                new Cena(
                        401,
                        "A frase que não devia doer",
                        "A relação entre Passos e Toin começa a se desgastar. "
                                + "Uma noite, Passos encontra Bruno dormindo "
                                + "no colo do amigo.",
                        "FLASHBACK"
                );
        cena401.setImagem("quarto_bruno_flashback.jpg");

        cena401.adicionarDialogo(
                new Dialogo(
                        passos.getNome(),
                        "Ele nem chora mais no colo de estranho, hein.",
                        "FALA",
                        "desconfiado"
                )
        );

        cena401.adicionarDialogo(
                new Dialogo(
                        toin.getNome(),
                        "Eu não sou estranho, Passos.",
                        "FALA",
                        "desconfiado"
                )
        );

        cena401.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Você parou. Nessa parte da história, você sempre para.",
                        "FALA",
                        "na_prancheta"
                )
        );

        Escolha c401a =
                criarEscolhaComTom(
                        1,
                        "Porque foi uma frase boba. "
                                + "Eu só tô sendo cuidadosa com os detalhes.",
                        402,

                        10,
                        0,
                        -5,
                        0,
                        0,
                        0,

                        "SANGUE_FRIO"
                );

        Escolha c401b =
                criarEscolhaComTom(
                        2,
                        "Porque na hora eu não dei importância. "
                                + "Só depois é que aquilo ficou estranho.",
                        402,

                        15,
                        5,
                        0,
                        0,
                        0,
                        0,

                        "EQUILIBRADO"
                );

        Escolha c401c =
                criarEscolhaComTom(
                        3,
                        "Porque aquela frase me incomodou na hora! "
                                + "E eu nunca disse isso a ninguém.",
                        402,

                        15,
                        -5,
                        0,
                        5,
                        -10,
                        5,

                        "SANGUE_QUENTE"
                );

        Escolha c401d =
                criarEscolha(
                        4,
                        "Porque foi o primeiro momento em que eu vi "
                                + "meu marido com medo de perder alguma coisa "
                                + "que ele nem sabia nomear.",
                        402,

                        20,
                        10,
                        20,
                        0,
                        10,
                        -5
                );

        c401d.definirCondicao(
                "Passos",
                65,
                null
        );

        cena401.adicionarEscolha(c401a);
        cena401.adicionarEscolha(c401b);
        cena401.adicionarEscolha(c401c);
        cena401.adicionarEscolha(c401d);

        // Cena 2: a resposta de Katarina define sua postura sobre a paternidade.
        Cena cena402 =
                new Cena(
                        402,
                        "Fúria ou dúvida",
                        "A paranoia de Passos cresce até que "
                                + "ele faz a acusação que muda o restante da história.",
                        "FLASHBACK"
                );
        cena402.setImagem("quarto_bruno_flashback.jpg");

        cena402.adicionarDialogo(
                new Dialogo(
                        passos.getNome(),
                        "Olha pra cara do meu filho, Katarina. "
                                + "E me diz que ele não tem o queixo do Toin.",
                        "FALA",
                        "bravo"
                )
        );

        Escolha c402a =
                criarEscolha(
                        1,
                        "Como você ousa olhar pro seu filho "
                                + "e duvidar dele por ciúme de um amigo?",
                        403,

                        10,
                        -5,
                        20,
                        -15,
                        -10,
                        -20
                );

        c402a.definirPosturaResultante(
                "NEGACAO_TOTAL"
        );

        Escolha c402b =
                criarEscolha(
                        2,
                        "Eu... eu nunca olhei procurando isso, Passos. "
                                + "E agora eu não consigo parar de olhar.",
                        403,

                        20,
                        15,
                        -10,
                        10,
                        -15,
                        15
                );

        c402b.definirPosturaResultante(
                "DUVIDA_ABERTA"
        );

        cena402.adicionarEscolha(c402a);
        cena402.adicionarEscolha(c402b);

        // Cena 3: a pergunta de Toin sobre o alarme pode gerar uma nova pista.
        Cena cena403 =
                new Cena(
                        403,
                        "O alarme",
                        "Dias antes do incêndio, Toin pergunta casualmente "
                                + "se o alarme de incêndio ainda funcionava.",
                        "FLASHBACK"
                );
        cena403.setImagem("sala_flashback.jpg");

        cena403.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Você disse que ele perguntou sobre o alarme. "
                                + "Isso te pareceu estranho na hora?",
                        "FALA",
                        "desconfiado"
                )
        );

        // As opções A e C deixam o delta de suspeita em 0 aqui; o bônus só é aplicado se a suspeita anterior já for >= 50.
        Escolha c403a =
                criarEscolhaComTom(
                        1,
                        "Na hora, não. "
                                + "Só faz sentido agora, do jeito que você tá perguntando.",
                        404,

                        15,
                        10,
                        0,
                        0,
                        0,
                        0,

                        "SANGUE_FRIO"
                );

        Escolha c403b =
                criarEscolhaComTom(
                        2,
                        "Um pouco. Mas Toin sempre foi "
                                + "de reparar em detalhes da casa.",
                        404,

                        10,
                        0,
                        5,
                        0,
                        0,
                        0,

                        "EQUILIBRADO"
                );

        Escolha c403c =
                criarEscolhaComTom(
                        3,
                        "Estranho?! Agora que você fala, "
                                + "sim, foi muito estranho, e eu não vi!",
                        404,

                        15,
                        -5,
                        0,
                        0,
                        -10,
                        10,

                        "SANGUE_QUENTE"
                );

        cena403.adicionarEscolha(c403a);
        cena403.adicionarEscolha(c403b);
        cena403.adicionarEscolha(c403c);

        // Cena 4: o laudo revela que o alarme foi desligado manualmente e permite uma acusação condicionada.
        Cena cena404 =
                new Cena(
                        404,
                        "A carta que Fauan guardava",
                        "Fauan revela o resultado da perícia.",
                        "INTERROGATORIO"
                );
        cena404.setImagem("interrogatorio.jpg");

        cena404.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "A perícia achou que o alarme de incêndio "
                                + "do quarto do seu filho tinha sido desligado manualmente. "
                                + "Não foi defeito.",
                        "FALA",
                        "na_prancheta"
                )
        );

        cena404.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Só três pessoas entraram naquele apartamento "
                                + "nos últimos três dias. "
                                + "Você, seu marido, e o Toin.",
                        "FALA",
                        "desconfiado"
                )
        );

        Escolha c404a =
                criarEscolha(
                        1,
                        "Então investiga os três. "
                                + "Comigo incluída, se for preciso.",
                        405,

                        10,
                        20,
                        0,
                        0,
                        10,
                        0
                );

        Escolha c404b =
                criarEscolha(
                        2,
                        "Não foi o Passos. "
                                + "Eu sei que não foi o Passos.",
                        405,

                        10,
                        -5,
                        25,
                        -10,
                        0,
                        -10
                );

        Escolha c404c =
                criarEscolha(
                        3,
                        "Foi o Toin.",
                        405,

                        25,
                        15,
                        -5,
                        30,
                        -5,
                        5
                );

        c404c.definirCondicao(
                "Toin",
                70,
                null
        );

        c404c.adicionarPistaNecessaria(
                "Pergunta sobre o alarme"
        );

        c404c.adicionarPistaNecessaria(
                "Alarme desligado manualmente"
        );

        cena404.adicionarEscolha(c404a);
        cena404.adicionarEscolha(c404b);
        cena404.adicionarEscolha(c404c);

        Cena cena405 =
                new Cena(
                        405,
                        "Toin chegou",
                        "Um oficial bate à porta. "
                                + "Toin chegou à delegacia e quer depor.",
                        "NARRATIVA"
                );
        cena405.setImagem("corredor_fumaça.jpg");

        cena405.definirProximaCena(501);

        capitulo.adicionarCena(cena401);
        capitulo.adicionarCena(cena402);
        capitulo.adicionarCena(cena403);
        capitulo.adicionarCena(cena404);
        capitulo.adicionarCena(cena405);

        estadoJogo.adicionarCapitulo(capitulo);
    }

    // Capítulo 5: reúne os personagens, define o destino de Bruno e prepara o veredito.
    private void construirCapitulo5() {

        Capitulo capitulo =
                new Capitulo(
                        5,
                        "O VEREDITO"
                );

        // Cena 1: Toin apresenta sua defesa e pode ser confrontado com as pistas do alarme.
        Cena cena501 =
                new Cena(
                        501,
                        "A defesa de Toin",
                        "Katarina, Toin, Passos e Fauan "
                                + "estão juntos pela primeira vez.",
                        "INTERROGATORIO"
                );
        cena501.setImagem("interrogatorio.jpg");

        cena501.adicionarDialogo(
                new Dialogo(
                        toin.getNome(),
                        "Eu passei os últimos dois anos ajudando essa família "
                                + "a não desmoronar. "
                                + "E agora eu sou suspeito por isso?",
                        "FALA",
                        "bravo"
                )
        );

        Escolha c501a =
                criarEscolhaComTom(
                        1,
                        "Ninguém disse que ajudar é crime, Toin. "
                                + "Mas ajudar demais também é uma escolha.",
                        502,

                        5,
                        5,
                        0,
                        5,
                        0,
                        0,

                        "SANGUE_FRIO"
                );

        Escolha c501b =
                criarEscolhaComTom(
                        2,
                        "Eu não sei mais separar o que foi ajuda "
                                + "do que foi outra coisa.",
                        502,

                        10,
                        10,
                        0,
                        10,
                        0,
                        0,

                        "EQUILIBRADO"
                );

        Escolha c501c =
                criarEscolhaComTom(
                        3,
                        "Para de fingir que isso é sobre ajudar! "
                                + "Nunca foi só sobre ajudar!",
                        502,

                        5,
                        -5,
                        0,
                        10,
                        -10,
                        0,

                        "SANGUE_QUENTE"
                );

        Escolha c501d =
                criarEscolha(
                        4,
                        "Você perguntou se o alarme ainda funcionava. "
                                + "Três dias antes. "
                                + "E ele foi desligado na mão. "
                                + "Me explica essa parte, Toin.",
                        502,

                        15,
                        25,
                        0,
                        25,
                        -5,
                        0
                );

        c501d.definirCondicao(
                "Toin",
                70,
                null
        );

        c501d.adicionarPistaNecessaria(
                "Pergunta sobre o alarme"
        );

        c501d.adicionarPistaNecessaria(
                "Alarme desligado manualmente"
        );

        cena501.adicionarEscolha(c501a);
        cena501.adicionarEscolha(c501b);
        cena501.adicionarEscolha(c501c);
        cena501.adicionarEscolha(c501d);

        // Cena 2: as respostas a Passos dependem da postura de paternidade definida no capítulo anterior.
        Cena cena502 =
                new Cena(
                        502,
                        "A defesa de Passos",
                        "Passos tenta se explicar diante de todos.",
                        "INTERROGATORIO"
                );
        cena502.setImagem("interrogatorio.jpg");

        cena502.adicionarDialogo(
                new Dialogo(
                        passos.getNome(),
                        "Eu traí ela. Eu sei. "
                                + "Isso não me faz capaz de matar meu próprio filho.",
                        "FALA",
                        "serio"
                )
        );

        // As quatro respostas desta cena usam os mesmos deltas; o que muda é a postura necessária para liberá-las.
        Escolha c502a =
                criarEscolha(
                        1,
                        "Não, não faz. "
                                + "E eu devia ter dito isso mais vezes nesses dois anos.",
                        503,

                        10,
                        5,
                        15,
                        0,
                        5,
                        -10
                );

        c502a.definirPosturaNecessaria(
                "NEGACAO_TOTAL"
        );

        Escolha c502b =
                criarEscolha(
                        2,
                        "Eu sei que não foi você, Passos. "
                                + "Eu sempre soube.",
                        503,

                        10,
                        5,
                        15,
                        0,
                        5,
                        -10
                );

        c502b.definirPosturaNecessaria(
                "NEGACAO_TOTAL"
        );

        Escolha c502c =
                criarEscolha(
                        3,
                        "Eu não sei mais o que você é capaz de fazer. "
                                + "E isso é mais sobre mim do que sobre você.",
                        503,

                        10,
                        5,
                        15,
                        0,
                        5,
                        -10
                );

        c502c.definirPosturaNecessaria(
                "DUVIDA_ABERTA"
        );

        Escolha c502d =
                criarEscolha(
                        4,
                        "A dúvida que você plantou nunca foi só sobre o Bruno. "
                                + "Foi sobre nós dois.",
                        503,

                        10,
                        5,
                        15,
                        0,
                        5,
                        -10
                );

        c502d.definirPosturaNecessaria(
                "DUVIDA_ABERTA"
        );

        cena502.adicionarEscolha(c502a);
        cena502.adicionarEscolha(c502b);
        cena502.adicionarEscolha(c502c);
        cena502.adicionarEscolha(c502d);

        // A cena 503 não é exibida; serve apenas para decidir qual versão da memória será usada.
        Cena cena503 =
                new Cena(
                        503,
                        "Os segundos que faltavam",
                        "A memória de Katarina tenta se reorganizar.",
                        "CONTROLE"
                );

        // Memória >= 85 leva à versão nítida da lembrança.
        Cena cena531 =
                new Cena(
                        531,
                        "Os segundos que faltavam - memória nítida",
                        "A cena se abre nítida, sem lacunas.",
                        "MEMORIA"
                );
        cena531.setImagem("fogo_quarto_bruno_muito_forte.jpg");

        cena531.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Katarina. O que você fez, "
                                + "nos segundos antes do fogo tomar o quarto?",
                        "FALA",
                        "desconfiado"
                )
        );

        Escolha c531a =
                criarEscolha(
                        1,
                        "Eu fui até o quarto.",
                        504,

                        20,
                        0,
                        0,
                        0,
                        0,
                        0
                );

        Escolha c531b =
                criarEscolha(
                        2,
                        "Eu gritei pelo Toin.",
                        504,

                        20,
                        0,
                        0,
                        0,
                        0,
                        0
                );

        cena531.adicionarEscolha(c531a);
        cena531.adicionarEscolha(c531b);

        // Memória < 85 leva à versão fragmentada da lembrança.
        Cena cena532 =
                new Cena(
                        532,
                        "Os segundos que faltavam - memória fragmentada",
                        "A lembrança ainda possui lacunas.",
                        "MEMORIA"
                );
        cena532.setImagem("fogo_quarto_bruno_fraco.jpg");

        cena532.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Katarina. O que você fez, "
                                + "nos segundos antes do fogo tomar o quarto?",
                        "FALA",
                        "desconfiado"
                )
        );

        Escolha c532a =
                criarEscolha(
                        1,
                        "Eu fui até o quarto.",
                        504,

                        20,
                        0,
                        0,
                        0,
                        0,
                        0
                );

        Escolha c532b =
                criarEscolha(
                        2,
                        "Eu gritei pelo Toin.",
                        504,

                        20,
                        0,
                        0,
                        0,
                        0,
                        0
                );

        cena532.adicionarEscolha(c532a);
        cena532.adicionarEscolha(c532b);

        // A cena 504 não tem escolha e dispara o cálculo do veredito no loop principal.
        Cena cena504 =
                new Cena(
                        504,
                        "O veredito",
                        "Fauan fecha a pasta. "
                                + "A decisão nasce de tudo que foi construído "
                                + "durante a investigação.",
                        "FINAL"
                );
        cena504.setImagem("interrogatorio.jpg");

        // Instancia as quatro cenas de desfecho possíveis da investigação.
        Cena final591 =
                new Cena(
                        591,
                        "Final - A verdade que restou",
                        "Bruno sobreviveu ao incêndio. "
                                + "A investigação leva ao indiciamento de Toin.",
                        "FINAL"
                );
        final591.setImagem("so_policia_cena_crime.jpg");

        Cena final592 =
                new Cena(
                        592,
                        "Final - Depois da fumaça",
                        "Bruno não sobreviveu ao incêndio. "
                                + "A investigação leva ao indiciamento de Toin.",
                        "FINAL"
                );
        final592.setImagem("so_policia_cena_crime.jpg");

        Cena final593 =
                new Cena(
                        593,
                        "Final - Sob suspeita",
                        "Bruno sobreviveu ao incêndio. "
                                + "Ao final da investigação, Katarina é indiciada.",
                        "FINAL"
                );
        final593.setImagem("so_policia_cena_crime.jpg");

        Cena final594 =
                new Cena(
                        594,
                        "Final - O peso da noite",
                        "Bruno não sobreviveu ao incêndio. "
                                + "Ao final da investigação, Katarina é indiciada.",
                        "FINAL"
                );
        final594.setImagem("so_policia_cena_crime.jpg");

        capitulo.adicionarCena(cena501);
        capitulo.adicionarCena(cena502);

        capitulo.adicionarCena(cena503);

        capitulo.adicionarCena(cena531);
        capitulo.adicionarCena(cena532);

        capitulo.adicionarCena(cena504);

        capitulo.adicionarCena(final591);
        capitulo.adicionarCena(final592);
        capitulo.adicionarCena(final593);
        capitulo.adicionarCena(final594);

        estadoJogo.adicionarCapitulo(capitulo);
    }

    // Exibe a cena atual, filtra escolhas válidas, aplica consequências e avança a partida.
    private void executarJogo() {

        while (!estadoJogo.isJogoEncerrado()) {

            Cena cenaAtual =
                    estadoJogo.getCenaAtual();

            Protagonista protagonista =
                    estadoJogo.getProtagonista();

            if (cenaAtual == null) {

                this.jogoView.apresentaErro(
                        "Erro: cena atual não encontrada."
                );

                break;
            }

            // A cena 503 apenas escolhe qual versão da lembrança será exibida conforme a memória acumulada.
            if (cenaAtual.getId() == 503) {

                if (protagonista.getMemoria() >= 85) {

                    estadoJogo.irParaCena(531);

                } else {

                    estadoJogo.irParaCena(532);
                }

                continue;
            }

            processarEventoAoEntrarNaCena(
                    cenaAtual,
                    protagonista
            );

            List<Escolha> escolhasDisponiveis =
                    cenaAtual.getEscolhasDisponiveis(
                            protagonista
                    );

            int opcaoEscolhida =
                    this.jogoView.mostrarCena(
                            cenaAtual,
                            escolhasDisponiveis
                    );

            // Trata salvar/voltar ao menu antes das regras de avanço da cena.
            if (opcaoEscolhida == -2) {
                salvarManual();
                continue;
            }

            if (opcaoEscolhida == -3) {
                if (this.jogoView.confirmarVoltarMenu()) return;
                continue;
            }

            if (opcaoEscolhida == 0) {
                this.jogoView.apresentaErro("A entrada fornecida é inválida.");
                continue;
            }

            // Cenas sem escolhas podem avançar automaticamente, calcular o veredito ou encerrar o jogo.
            if (escolhasDisponiveis.isEmpty()) {

                if (cenaAtual.getId() == 504) {

                    redirecionarParaFinal(
                            protagonista
                    );

                    continue;
                }

                if (ehCenaFinal(
                        cenaAtual.getId()
                )) {

                    String finalAlcancado = nomeDoFinal(cenaAtual.getId());

                    // Salva o desfecho alcançado e os extras correspondentes no perfil global.
                    if (!registrarFinalNoPerfil(finalAlcancado)) {
                        this.jogoView.apresentaErro("Não foi possível salvar a conquista.");
                    }

                    estadoJogo.encerrarJogo(finalAlcancado);

                    continue;
                }

                int proximaCena =
                        cenaAtual.getProximaCenaId();

                if (proximaCena != -1) {

                    boolean encontrou =
                            estadoJogo.irParaCena(
                                    proximaCena
                            );

                    if (!encontrou) {

                        this.jogoView.apresentaErro(
                                "Erro: cena "
                                        + proximaCena
                                        + " não encontrada."
                        );

                        break;
                    }

                    continue;
                }

                estadoJogo.encerrarJogo("FIM");

                continue;
            }

            // Garante que o número digitado corresponda a uma escolha realmente exibida ao jogador.
            if (opcaoEscolhida < 1
                    || opcaoEscolhida > escolhasDisponiveis.size()) {

                this.jogoView.apresentaErro(
                        "A entrada fornecida é inválida."
                );

                continue;
            }

            Escolha escolha =
                    escolhasDisponiveis.get(
                            opcaoEscolhida - 1
                    );

            // Executa regras que dependem dos atributos antes de aplicar os deltas da escolha.
            processarConsequenciaEspecialAntesDaEscolha(
                    cenaAtual,
                    escolha,
                    protagonista
            );

            // Aplica os deltas, pistas, postura e regra de tom definidos na própria Escolha.
            escolha.aplicarConsequencias(
                    protagonista
            );

            // No capítulo 3, o tom altera somente a estabilidade porque Fauan não participa do flashback.
            if (cenaAtual.getId() == 301) {

                aplicarTomCapitulo3(
                        escolha,
                        protagonista
                );
            }

            boolean encontrou =
                    estadoJogo.irParaCena(
                            escolha.getProximaCenaId()
                    );

            if (!encontrou) {

                this.jogoView.apresentaErro(
                        "Erro: próxima cena não encontrada."
                );

                break;
            }
            this.salvaAuto();
        }
    }

    // Aplica consequências que acontecem ao entrar em uma cena, sem depender de uma escolha.
    private void processarEventoAoEntrarNaCena(
            Cena cenaAtual,
            Protagonista protagonista) {

        // Ao entrar na cena 404, adiciona automaticamente a pista do laudo se Katarina ainda não a possuir.
        if (cenaAtual.getId() == 404
                && !protagonista.possuiPista(
                "Alarme desligado manualmente"
        )) {

            protagonista.adicionarPista(
                    pistaAlarmeDesligado
            );
        }
    }

    // Trata condições cujo resultado depende do estado da protagonista antes da escolha.
    private void processarConsequenciaEspecialAntesDaEscolha(
            Cena cenaAtual,
            Escolha escolha,
            Protagonista protagonista) {

        // Na cena 403, verifica se a suspeita já era >= 50 antes da escolha para conceder o bônus e a pista.
        if (cenaAtual.getId() == 403) {

            int suspeitaAntes =
                    protagonista.getSuspeitaToin();

            if (suspeitaAntes >= 50) {

                // A opção A acrescenta 10 de suspeita e registra a pista do alarme.
                if (escolha.getId() == 1) {

                    protagonista.ajustarSuspeitaToin(
                            10
                    );

                    if (!protagonista.possuiPista(
                            "Pergunta sobre o alarme"
                    )) {

                        protagonista.adicionarPista(
                                pistaPerguntaAlarme
                        );
                    }
                }

                // A opção C acrescenta 15 de suspeita e registra a mesma pista do alarme.
                if (escolha.getId() == 3) {

                    protagonista.ajustarSuspeitaToin(
                            15
                    );

                    if (!protagonista.possuiPista(
                            "Pergunta sobre o alarme"
                    )) {

                        protagonista.adicionarPista(
                                pistaPerguntaAlarme
                        );
                    }
                }
            }
        }

        // Nas cenas 531 e 532, a opção escolhida usa estabilidade ou vínculo para definir se Bruno sobrevive.
        if (cenaAtual.getId() == 531
                || cenaAtual.getId() == 532) {

            // Na opção A, Bruno sobrevive somente se a estabilidade já for >= 55.
            if (escolha.getId() == 1) {

                brunoSobreviveu =
                        protagonista
                                .getEstabilidadeEmocional()
                                >= 55;
            }

            // Na opção B, Bruno sobrevive somente se o vínculo com Passos já for >= 60.
            if (escolha.getId() == 2) {

                brunoSobreviveu =
                        protagonista
                                .getVinculoPassos()
                                >= 60;
            }
        }
    }

    // Ajusta a estabilidade em +5 ou -5 conforme o temperamento da protagonista, sem alterar a confiança em Fauan.
    private void aplicarTomCapitulo3(
            Escolha escolha,
            Protagonista protagonista) {

        String tomEscolhido = "";

        if (escolha.getId() == 1) {

            tomEscolhido = "SANGUE_FRIO";
        }

        if (escolha.getId() == 2) {

            tomEscolhido = "EQUILIBRADO";
        }

        if (escolha.getId() == 3) {

            tomEscolhido = "SANGUE_QUENTE";
        }

        if (tomEscolhido.equals(
                protagonista.getTemperamento()
        )) {

            protagonista.ajustarEstabilidade(
                    5
            );

        } else {

            protagonista.ajustarEstabilidade(
                    -5
            );
        }
    }

    // Calcula a credibilidade de Toin e usa esse valor para escolher uma das quatro cenas finais.
    private void redirecionarParaFinal(
            Protagonista protagonista) {

        // Conta as pistas ligadas diretamente ao alarme para calcular a credibilidade de Toin.
        int quantidadePistasContraToin = 0;

        if (protagonista.possuiPista(
                "Pergunta sobre o alarme"
        )) {

            quantidadePistasContraToin++;
        }

        if (protagonista.possuiPista(
                "Alarme desligado manualmente"
        )) {

            quantidadePistasContraToin++;
        }

        int credibilidadeToin =
                100
                        - protagonista.getSuspeitaToin()
                        - (5 * quantidadePistasContraToin);

        if (credibilidadeToin < 0) {
            credibilidadeToin = 0;
        }

        if (credibilidadeToin > 100) {
            credibilidadeToin = 100;
        }

        boolean toinIndiciado =
                protagonista.getConfiancaFauan()
                        >= credibilidadeToin;

        int cenaFinal;

        if (toinIndiciado && brunoSobreviveu) {

            cenaFinal = 591;

        } else if (toinIndiciado) {

            cenaFinal = 592;

        } else if (brunoSobreviveu) {

            cenaFinal = 593;

        } else {

            cenaFinal = 594;
        }

        estadoJogo.irParaCena(
                cenaFinal
        );
    }

    // Verifica se o ID informado pertence a uma das quatro cenas de encerramento.
    private boolean ehCenaFinal(int idCena) {

        return idCena == 591
                || idCena == 592
                || idCena == 593
                || idCena == 594;
    }

    // Devolve a chave interna correspondente ao final alcançado.
    private String nomeDoFinal(int idCena) {

        if (idCena == 591) {

            return "FINAL_TOIN_INDICIADO_BRUNO_VIVO";
        }

        if (idCena == 592) {

            return "FINAL_TOIN_INDICIADO_BRUNO_MORTO";
        }

        if (idCena == 593) {

            return "FINAL_KATARINA_INDICIADA_BRUNO_VIVO";
        }

        return "FINAL_KATARINA_INDICIADA_BRUNO_MORTO";
    }

    // =========================================================
    // API PARA A INTERFACE GRÁFICA (JavaFX)
    // A View só chama estes métodos; nenhuma regra do jogo fica nas telas.
    // =========================================================

    private static final String[] NOMES_VALORES = {
            "Memória",
            "Estabilidade emocional",
            "Culpa percebida",
            "Confiança em Fauan",
            "Vínculo com Passos",
            "Suspeita sobre Toin"
    };

    // Informa se existe uma partida carregada na memória.
    public boolean temPartida() {
        return estadoJogo != null;
    }

    // Só é possível salvar enquanto há uma partida em andamento.
    public boolean podeSalvar() {
        return estadoJogo != null
                && !estadoJogo.isJogoEncerrado()
                && estadoJogo.getCenaAtual() != null;
    }

    // Recomeça a história do zero mantendo o temperamento escolhido para Katarina.
    public void reiniciarPartida() {
        String temperamento = estadoJogo.getProtagonista().getTemperamento();
        int opcao = 2;
        if ("SANGUE_FRIO".equals(temperamento)) opcao = 1;
        if ("SANGUE_QUENTE".equals(temperamento)) opcao = 3;
        prepararNovaPartida(opcao);
    }

    // Descarta a partida atual (usado ao voltar para o menu).
    public void encerrarSessao() {
        estadoJogo = null;
    }

    public Protagonista getProtagonista() {
        return estadoJogo.getProtagonista();
    }

    public Personagem buscarPersonagemPorNome(String nome) {
        if (estadoJogo == null || nome == null) {
            return null;
        }

        String alvo = nome.trim();
        Protagonista p = estadoJogo.getProtagonista();
        if (p != null && p.getNome() != null && alvo.equalsIgnoreCase(p.getNome().trim())) {
            return p;
        }

        for (Psecundario sec : estadoJogo.getPersonagens()) {
            if (sec != null && sec.getNome() != null && alvo.equalsIgnoreCase(sec.getNome().trim())) {
                return sec;
            }
        }

        return null;
    }

    public Cena getCenaAtual() {
        return estadoJogo.getCenaAtual();
    }

    public Capitulo getCapituloAtual() {
        return estadoJogo.getCapituloAtual();
    }

    public int getTotalCapitulos() {
        return estadoJogo.getCapitulos().size();
    }

    public boolean isJogoEncerrado() {
        return estadoJogo.isJogoEncerrado();
    }

    // Escolhas que o jogador pode ver agora, já filtradas pelas condições de acesso.
    public List<Escolha> getEscolhasDisponiveis() {
        return getCenaAtual().getEscolhasDisponiveis(getProtagonista());
    }

    // Resolve cenas internas (503) e eventos de entrada; deve ser chamado antes de exibir a cena atual.
    public void prepararCenaAtual() {
        Cena cena = estadoJogo.getCenaAtual();

        if (cena != null && cena.getId() == 503) {
            estadoJogo.irParaCena(
                    getProtagonista().getMemoria() >= 85 ? 531 : 532
            );
            cena = estadoJogo.getCenaAtual();
        }

        if (cena != null) {
            processarEventoAoEntrarNaCena(cena, getProtagonista());
        }
    }

    // Aplica a escolha (índice na lista de disponíveis) e devolve as mudanças para o feedback visual.
    public List<String> escolherOpcao(int indice) {
        List<Escolha> disponiveis = getEscolhasDisponiveis();

        if (indice < 0 || indice >= disponiveis.size()) {
            throw new IllegalArgumentException("Escolha inválida.");
        }

        Cena cenaAtual = estadoJogo.getCenaAtual();
        Protagonista protagonista = getProtagonista();
        Escolha escolha = disponiveis.get(indice);

        int[] valoresAntes = capturarValores(protagonista);
        int pistasAntes = protagonista.getPistas().size();
        String posturaAntes = protagonista.getPosturaPaternidade();

        processarConsequenciaEspecialAntesDaEscolha(cenaAtual, escolha, protagonista);
        escolha.aplicarConsequencias(protagonista);

        if (cenaAtual.getId() == 301) {
            aplicarTomCapitulo3(escolha, protagonista);
        }

        if (!estadoJogo.irParaCena(escolha.getProximaCenaId())) {
            throw new IllegalStateException("Cena " + escolha.getProximaCenaId() + " não encontrada.");
        }

        List<String> mensagens = descreverMudancas(
                valoresAntes, pistasAntes, posturaAntes, protagonista);

        if (salvarAutomatico()) {
            mensagens.add("Progresso salvo automaticamente (slot 1).");
        }

        return mensagens;
    }

    // Avança uma cena sem escolhas: calcula o veredito, registra o final ou segue para a próxima cena.
    public void avancarCena() {
        Cena cenaAtual = estadoJogo.getCenaAtual();
        Protagonista protagonista = getProtagonista();

        if (cenaAtual.getId() == 504) {
            redirecionarParaFinal(protagonista);
            return;
        }

        if (ehCenaFinal(cenaAtual.getId())) {
            registrarFinalNoPerfil(nomeDoFinal(cenaAtual.getId()));
            estadoJogo.encerrarJogo(nomeDoFinal(cenaAtual.getId()));
            return;
        }

        int proxima = cenaAtual.getProximaCenaId();

        if (proxima == -1) {
            estadoJogo.encerrarJogo("FIM");
            return;
        }

        if (!estadoJogo.irParaCena(proxima)) {
            throw new IllegalStateException("Cena " + proxima + " não encontrada.");
        }
    }

    // Lista "apelido | data | resumo" de cada slot, ou "Vazio".
    public String[] listarSaves() {
        return persistencia.listarNomesSaves();
    }

    public int getTotalSlots() {
        return PersistenciaJogo.TOTAL_SLOTS;
    }

    public boolean slotOcupado(int slot) {
        return persistencia.existe(slot);
    }

    // Salvamento manual: grava a partida atual no slot informado com o apelido dado.
    public void salvarPartida(int slot, String apelido) throws IOException {
        if (!podeSalvar()) {
            throw new IOException("Não há partida em andamento para salvar.");
        }
        if (slot < 1 || slot > PersistenciaJogo.TOTAL_SLOTS) {
            throw new IOException("Slot inválido.");
        }

        String nome = (apelido == null || apelido.trim().isEmpty()) ? "Sem_Nome" : apelido.trim();
        DadosPartida dados = DadosPartida.capturar(estadoJogo, brunoSobreviveu, slot);
        persistencia.salvar(slot, dados, nome);
    }

    public void excluirSave(int slot) throws IOException {
        persistencia.excluirSave(slot);
    }

    // Devolve o slot salvo mais recentemente (0 se não houver nenhum), usado pelo botão "Continuar".
    public int slotMaisRecente() {
        int melhor = 0;
        long dataMelhor = -1;

        for (int slot = 1; slot <= PersistenciaJogo.TOTAL_SLOTS; slot++) {
            try {
                DadosPartida dados = persistencia.carregar(slot);
                if (dados != null && dados.dataSalvamento > dataMelhor) {
                    melhor = slot;
                    dataMelhor = dados.dataSalvamento;
                }
            } catch (IOException ignorado) {
                // Save corrompido é ignorado na busca pelo mais recente.
            }
        }

        return melhor;
    }

    // Salva no slot 1 sem imprimir nada no terminal; devolve false se falhar.
    private boolean salvarAutomatico() {
        try {
            DadosPartida dados = DadosPartida.capturar(estadoJogo, brunoSobreviveu, 1);
            persistencia.salvar(1, dados, "auto save");
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // Registra o final e os extras desbloqueados no perfil global; devolve false se não conseguir gravar.
    private boolean registrarFinalNoPerfil(String finalAlcancado) {
        persistence.PersistenciaGlobal persistenciaG = new persistence.PersistenciaGlobal();
        models.DadosGlobais perfil = persistenciaG.carregar();
        perfil.registrarFinal(finalAlcancado);
        perfil.registrarExtra("FIM?");
        if (finalAlcancado.contains("BRUNO_MORTO"))
            perfil.registrarExtra("BRINQUEDO_BRUNO");

        try {
            persistenciaG.salvar(perfil);
            return true;
        } catch (java.io.IOException e) {
            return false;
        }
    }

    private int[] capturarValores(Protagonista p) {
        return new int[]{
                p.getMemoria(),
                p.getEstabilidadeEmocional(),
                p.getCulpaPercebida(),
                p.getConfiancaFauan(),
                p.getVinculoPassos(),
                p.getSuspeitaToin()
        };
    }

    // Compara o antes e o depois da escolha e monta as mensagens de feedback (ex.: "Memória +15").
    private List<String> descreverMudancas(
            int[] antes,
            int pistasAntes,
            String posturaAntes,
            Protagonista p) {

        List<String> mensagens = new java.util.ArrayList<>();
        int[] depois = capturarValores(p);

        for (int i = 0; i < antes.length; i++) {
            int diferenca = depois[i] - antes[i];
            if (diferenca != 0) {
                mensagens.add(NOMES_VALORES[i] + (diferenca > 0 ? " +" : " ") + diferenca);
            }
        }

        for (int i = pistasAntes; i < p.getPistas().size(); i++) {
            mensagens.add("Nova pista: " + p.getPistas().get(i).getNome());
        }

        if (!p.getPosturaPaternidade().equals(posturaAntes)) {
            mensagens.add("Sua postura sobre a paternidade mudou.");
        }

        return mensagens;
    }
}