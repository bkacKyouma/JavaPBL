package models;


//Guarda o estado geral da partida: personagens, capítulos, capítulo jogado, cena jogada, jogo terminou, final alcançado
import java.util.List;
import java.util.ArrayList;


public class EstadoJogo {

    private Protagonista protagonista;
    private List<Psecundario> personagens;
    private List<Capitulo> capitulos;

    private Capitulo capituloAtual;
    private Cena cenaAtual;

    private boolean jogoEncerrado;
    private String finalAlcancado;


    public EstadoJogo(Protagonista protagonista) {

        this.protagonista = protagonista;
        this.personagens = new ArrayList<>();
        this.capitulos = new ArrayList<>();
        this.capituloAtual = null;
        this.cenaAtual = null;
        this.jogoEncerrado = false;
        this.finalAlcancado = null;
    }

    public Protagonista getProtagonista() {
        return protagonista;
    }

    public List<Psecundario> getPersonagens() {
        return personagens;
    }

    public void adicionarPersonagem(Psecundario personagem) {
        personagens.add(personagem);
    }

    public Psecundario getPersonagem(int id) {
        for (Psecundario personagem : personagens) {
            if (personagem.getId() == id) {
                return personagem;
            }
        }

        return null;
    }

    public List<Capitulo> getCapitulos() {
        return capitulos;
    }


    public void adicionarCapitulo(Capitulo capitulo) {
        capitulos.add(capitulo);
    }


    public Capitulo getCapitulo(int id) {
        for (Capitulo capitulo : capitulos) {
            if (capitulo.getId() == id) {
                return capitulo;
            }
        }

        return null;
    }



    public Capitulo getCapituloAtual() {
        return capituloAtual;
    }


    public Cena getCenaAtual() {
        return cenaAtual;
    }


    // Inicia a partida no primeiro capítulo e na primeira cena desse capítulo.
    public void iniciarJogo() {

        if (capitulos.size() > 0) {

            capituloAtual = capitulos.get(0);
            cenaAtual = capituloAtual.getCenaInicial();

            jogoEncerrado = false;
            finalAlcancado = null;
        }
    }

    // Procura uma cena pelo ID em todos os capítulos, quando encontra atualiza capítulo e cena atuais
    public boolean irParaCena(int idCena) {

        for (Capitulo capitulo : capitulos) {

            Cena cenaEncontrada = capitulo.getCena(idCena);

            if (cenaEncontrada != null) {

                capituloAtual = capitulo;
                cenaAtual = cenaEncontrada;

                return true;
            }
        }

        return false;
    }



    public boolean isJogoEncerrado() {
        return jogoEncerrado;
    }


    public String getFinalAlcancado() {
        return finalAlcancado;
    }


    public void encerrarJogo(String finalAlcancado) {

        this.finalAlcancado = finalAlcancado;
        this.jogoEncerrado = true;
    }


    // Retorna o jogo para o início.
    public void reiniciarPosicao() {

        jogoEncerrado = false;
        finalAlcancado = null;

        if (capitulos.size() > 0) {

            capituloAtual = capitulos.get(0);
            cenaAtual = capituloAtual.getCenaInicial();

        } else {

            capituloAtual = null;
            cenaAtual = null;
        }
    }
}

