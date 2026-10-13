package controllers;

import java.util.List;
import java.io.IOException;
import models.DadosPartida;
import persistence.PersistenciaJogo;

import models.Capitulo;
import models.Cena;
import models.Dialogo;
import models.Escolha;
import models.EstadoJogo;
import models.Pista;
import models.Protagonista;
import models.Psecundario;

import views.JogoView;
import views.MenuView;
import views.Terminal;


// Coordena a partida: cria o conteúdo narrativo, aplica regras especiais e move o EstadoJogo entre as cenas.
public class JogoController {

    // Apenas declare as variáveis, sem dar 'new' aqui
    private JogoView jogoView;
    private MenuView menuView;
    private EstadoJogo estadoJogo;


    // private EstadoJogo estadoJogo;

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

    // ADICIONE ESTE CONSTRUTOR
    public JogoController(String corPrimaria, String corSecundaria, String modo) {
        this.jogoView = new JogoView(corPrimaria, corSecundaria, modo);
        this.menuView = new MenuView(corPrimaria, corSecundaria, modo);
    }
    // Converte o temperamento, cria Katarina, monta a história e inicia o loop da partida.

    public void iniciarJogo(int opcaoTemperamento) {

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
        criarPistas();
        construirHistoria();

        estadoJogo.iniciarJogo();

        executarJogo();
    }


    // Lê o JSON, reconstrói a protagonista e volta à cena salva.
    public void carregarJogo(int slot) {
        try {
            DadosPartida dados = persistencia.carregar(slot);
            if (dados == null) {
                this.jogoView.apresentaErro("Não existe partida salva no slot " + slot);
                return;
            }

            Protagonista katarina = dados.restaurarProtagonista();
            estadoJogo = new EstadoJogo(katarina);
            criarPersonagens();
            criarPistas();
            construirHistoria();

            if (!estadoJogo.irParaCena(dados.cenaAtualId)
                    || estadoJogo.getCapituloAtual().getId() != dados.capituloAtualId) {
                this.jogoView.apresentaErro("A cena guardada no save não foi encontrada.");
                return;
            }

            brunoSobreviveu = dados.brunoSobreviveu;
            executarJogo();
        } catch (IOException e) {
            this.jogoView.apresentaErro("Não foi possível carregar: " + e.getMessage());
        }
    }

    // Permite salvar a cena atual em qualquer momento, sem avançar a história.
    private void salvarManual() {
        int slot = this.menuView.escolherSlot("SALVAR PARTIDA");
        if (slot == 0) return;
        if (slot < 1 || slot > PersistenciaJogo.TOTAL_SLOTS) {
            this.jogoView.apresentaErro("Slot inválido.");
            return;
        }

        if (persistencia.existe(slot) && !this.menuView.confirmarSobrescrita(slot)) {
            return;
        }

        DadosPartida dados = DadosPartida.capturar(estadoJogo, brunoSobreviveu, slot);
        try {
            persistencia.salvar(slot, dados);
            System.out.println("Partida salva no slot " + slot + ".");
            views.Terminal.aplicaDelay(900);
        } catch (IOException e) {
            this.jogoView.apresentaErro("Erro ao salvar: " + e.getMessage());
        }
    }
    private void salvaAuto() {
        int slot = 1;

        DadosPartida dados = DadosPartida.capturar(estadoJogo, brunoSobreviveu, slot);
        try {
            persistencia.salvar(slot, dados);
            System.out.println("Auto save no slot " + slot + ".");
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


    // Centraliza a criação das escolhas para manter a ordem dos deltas igual à tabela do roteiro.

    // Os parâmetros seguem a ordem da tabela: memória, confiança, vínculo, suspeita, estabilidade e culpa.
    // definirEfeitos() recebe esses mesmos valores em outra ordem, por isso a reorganização abaixo.
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


        cena101.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "No meio do incêndio, você correu. "
                                + "Só que correu pra longe do seu filho, não pra perto.",
                        "FALA"
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


        cena111.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Pra mim, agora, é a mesma coisa. "
                                + "Me convence do contrário.",
                        "FALA"
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


        cena112.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Pra mim, agora, é a mesma coisa. "
                                + "Me convence do contrário.",
                        "FALA"
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


        cena102.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Os vizinhos disseram que ouviram vocês dois brigando "
                                + "antes da fumaça começar. Sobre o quê?",
                        "FALA"
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


        cena121.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Toda briga tem a ver com a noite "
                                + "em que a casa pega fogo, Katarina.",
                        "FALA"
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


        cena122.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Toda briga tem a ver com a noite "
                                + "em que a casa pega fogo, Katarina.",
                        "FALA"
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


        cena103.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Toin diz que chegou antes dos bombeiros. "
                                + "Que foi ele quem tirou você de dentro de casa. "
                                + "Isso bate com o que você lembra?",
                        "FALA"
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


        cena131.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Isso é mais honesto do que qualquer coisa "
                                + "que você me disse até agora.",
                        "FALA"
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


        cena132.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "'Na hora certa.' Interessante escolha de palavras "
                                + "pra descrever um resgate.",
                        "FALA"
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


        cena104.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Me diz uma coisa que só você poderia saber "
                                + "sobre aquela noite. "
                                + "Uma coisa que não tá no meu relatório.",
                        "FALA"
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


        cena141.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Isso não está no meu relatório porque "
                                + "ninguém tinha mencionado ainda.",
                        "FALA"
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


        cena142.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Ou talvez você já soubesse a resposta "
                                + "e não quisesse me dar.",
                        "FALA"
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


        cena105.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Última pergunta, Katarina. "
                                + "O que você viu, de verdade, "
                                + "antes de tudo virar fumaça?",
                        "FALA"
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


        cena201.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "E isso era comum? "
                                + "Ele aparecer assim, sem avisar?",
                        "FALA"
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


        cena202.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Você sentiu o cheiro e não disse nada. Por quê?",
                        "FALA"
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


        cena203.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Por que uma ligação de um amigo da família "
                                + "precisa ser atendida com a porta trancada?",
                        "FALA"
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


        cena204.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Nenhuma dessas coisas, sozinha, é motivo pra nada. "
                                + "Mas você mesma não acredita mais "
                                + "que era tudo nada.",
                        "FALA"
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


        cena301.adicionarDialogo(
                new Dialogo(
                        passos.getNome(),
                        "Toin, essa é a Katarina. "
                                + "Katarina, esse aqui é meu irmão de escolha.",
                        "FALA"
                )
        );


        cena301.adicionarDialogo(
                new Dialogo(
                        toin.getNome(),
                        "Então você é a mulher que finalmente fez "
                                + "esse aí parar de falar de futebol "
                                + "pra falar de outra coisa.",
                        "FALA"
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


        cena302.adicionarDialogo(
                new Dialogo(
                        toin.getNome(),
                        "Que INCRÍVEL notícia, mano. "
                                + "Não sei nem expressar o quão feliz fico por vocês.",
                        "FALA"
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


        cena401.adicionarDialogo(
                new Dialogo(
                        passos.getNome(),
                        "Ele nem chora mais no colo de estranho, hein.",
                        "FALA"
                )
        );


        cena401.adicionarDialogo(
                new Dialogo(
                        toin.getNome(),
                        "Eu não sou estranho, Passos.",
                        "FALA"
                )
        );


        cena401.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Você parou. Nessa parte da história, você sempre para.",
                        "FALA"
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


        cena402.adicionarDialogo(
                new Dialogo(
                        passos.getNome(),
                        "Olha pra cara do meu filho, Katarina. "
                                + "E me diz que ele não tem o queixo do Toin.",
                        "FALA"
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


        cena403.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Você disse que ele perguntou sobre o alarme. "
                                + "Isso te pareceu estranho na hora?",
                        "FALA"
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


        cena404.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "A perícia achou que o alarme de incêndio "
                                + "do quarto do seu filho tinha sido desligado manualmente. "
                                + "Não foi defeito.",
                        "FALA"
                )
        );


        cena404.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Só três pessoas entraram naquele apartamento "
                                + "nos últimos três dias. "
                                + "Você, seu marido, e o Toin.",
                        "FALA"
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


        cena501.adicionarDialogo(
                new Dialogo(
                        toin.getNome(),
                        "Eu passei os últimos dois anos ajudando essa família "
                                + "a não desmoronar. "
                                + "E agora eu sou suspeito por isso?",
                        "FALA"
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


        cena502.adicionarDialogo(
                new Dialogo(
                        passos.getNome(),
                        "Eu traí ela. Eu sei. "
                                + "Isso não me faz capaz de matar meu próprio filho.",
                        "FALA"
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


        cena531.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Katarina. O que você fez, "
                                + "nos segundos antes do fogo tomar o quarto?",
                        "FALA"
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


        cena532.adicionarDialogo(
                new Dialogo(
                        fauan.getNome(),
                        "Katarina. O que você fez, "
                                + "nos segundos antes do fogo tomar o quarto?",
                        "FALA"
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


        // Identifica as cenas finais e devolve o nome interno correspondente ao final alcançado.

        Cena final591 =
                new Cena(
                        591,
                        "Final - A verdade que restou",
                        "Bruno sobreviveu ao incêndio. "
                                + "A investigação leva ao indiciamento de Toin.",
                        "FINAL"
                );


        Cena final592 =
                new Cena(
                        592,
                        "Final - Depois da fumaça",
                        "Bruno não sobreviveu ao incêndio. "
                                + "A investigação leva ao indiciamento de Toin.",
                        "FINAL"
                );


        Cena final593 =
                new Cena(
                        593,
                        "Final - Sob suspeita",
                        "Bruno sobreviveu ao incêndio. "
                                + "Ao final da investigação, Katarina é indiciada.",
                        "FINAL"
                );


        Cena final594 =
                new Cena(
                        594,
                        "Final - O peso da noite",
                        "Bruno não sobreviveu ao incêndio. "
                                + "Ao final da investigação, Katarina é indiciada.",
                        "FINAL"
                );


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


                    // Registra a conquista na PersistenciaGlobal
                    persistence.PersistenciaGlobal persistenciaG = new persistence.PersistenciaGlobal();
                    models.DadosGlobais perfil = persistenciaG.carregar();
                    perfil.registrarFinal(finalAlcancado);
                    if(!perfil.extrasDesbloqueados.contains("FIM"))
                        perfil.registrarExtra("FIM?");
                    if (finalAlcancado.contains("BRUNO_MORTO"))
                        perfil.registrarExtra("BRINQUEDO_BRUNO");

                    try {
                        persistenciaG.salvar(perfil);
                    } catch (java.io.IOException e) {
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


            // Capítulo 3: mostra a origem da relação entre Passos e Toin sem participação de Fauan.
            // No capítulo 3, o tom altera somente a estabilidade porque Fauan não está presente.

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


        // Na cena 403, a suspeita precisa ser verificada antes dos deltas para decidir se a pista é obtida.

        // Guarda a suspeita anterior porque a pista e o bônus só valem se ela já era >= 50 antes da escolha.
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


    // Compara a resposta da cena 301 com o temperamento sem alterar a confiança em Fauan.

    // Se o tom combina com o temperamento, soma 5 de estabilidade; caso contrário, subtrai 5.
    // Como Fauan não está no flashback, essa regra não altera a confiança nele.
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


    // Identifica as cenas finais e devolve o nome interno correspondente ao final alcançado.

    private boolean ehCenaFinal(int idCena) {

        return idCena == 591
                || idCena == 592
                || idCena == 593
                || idCena == 594;
    }


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
}