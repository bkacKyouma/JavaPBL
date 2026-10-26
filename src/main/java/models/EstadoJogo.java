
package models;

import java.util.List;
import java.util.ArrayList;

// Mantém o estado da partida, incluindo personagens, capítulos, cena atual e encerramento.
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

    // Registra um personagem secundário para que ele possa ser encontrado durante a partida.
    public void adicionarPersonagem(Psecundario personagem) {
        personagens.add(personagem);
    }

    // Busca um personagem pelo ID e retorna null caso não exista.
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

    // Adiciona um capítulo à lista utilizada para construir e percorrer a história.
    public void adicionarCapitulo(Capitulo capitulo) {
        capitulos.add(capitulo);
    }

    // Procura um capítulo pelo ID, retornando null quando não encontra correspondência.
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

    // Posiciona o jogador na primeira cena do primeiro capítulo e libera o estado de encerramento.
    public void iniciarJogo() {

        if (capitulos.size() > 0) {

            capituloAtual = capitulos.get(0);
            cenaAtual = capituloAtual.getCenaInicial();

            jogoEncerrado = false;
            finalAlcancado = null;
        }
    }

    // Percorre os capítulos procurando a cena informada e atualiza a posição da partida.
    // Se a cena não existir, retorna false sem modificar o capítulo ou a cena atual.
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

    // Registra qual final foi alcançado e sinaliza que a partida terminou.
    public void encerrarJogo(String finalAlcancado) {

        this.finalAlcancado = finalAlcancado;
        this.jogoEncerrado = true;
    }

    // Reinicia a posição da história sem apagar os atributos ou as pistas da protagonista.
    // Se nenhum capítulo tiver sido cadastrado, mantém a cena e o capítulo como null.
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

