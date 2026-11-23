package models;

import java.util.ArrayList;
import java.util.List;

public class Cena {

    private int id;
    private String titulo;
    private String textoNarrativa;
    private String tipo;
    private String imagem;

    private List<Escolha> escolhas;
    private List<Dialogo> dialogos;

    private int proximaCenaId;
    private String humorProtagonista;


    public Cena(int id, String titulo, String textoNarrativa, String tipo) {

        this.id = id;
        this.titulo = titulo;
        this.textoNarrativa = textoNarrativa;
        this.tipo = tipo;
        this.imagem = null;

        this.escolhas = new ArrayList<>();
        this.dialogos = new ArrayList<>();

        this.proximaCenaId = -1;  // -1 significa que não existe próxima cena automática.
    }

    public int getId() {
        return id;
    }
    public String getHumorProtagonista() {
        return humorProtagonista;
    }

    public void setHumorProtagonista(String humorProtagonista) {
        this.humorProtagonista = humorProtagonista;
    }
    public String getTitulo() {
        return titulo;
    }

    public String getTextoNarrativa() {
        return textoNarrativa;
    }

    public String getTipo() {
        return tipo;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public List<Escolha> getEscolhas() {
        return escolhas;
    }

    public void adicionarEscolha(Escolha escolha) {
        escolhas.add(escolha);
    }

    public List<Dialogo> getDialogos() {
        return dialogos;
    }

    public void adicionarDialogo(Dialogo dialogo) {
        dialogos.add(dialogo);
    }

    // Define para onde o jogo irá quando essa cena não possuir nenhuma escolha (ex.: cap 3).
    public void definirProximaCena(int proximaCenaId) {
        this.proximaCenaId = proximaCenaId;
    }

    public int getProximaCenaId() {
        return proximaCenaId;
    }

    // Retorna apenas as escolhas que o jogador realmente pode utilizar naquele momento.
    public List<Escolha> getEscolhasDisponiveis(Protagonista protagonista) {

        List<Escolha> disponiveis = new ArrayList<>();

        for (Escolha escolha : escolhas) {

            if (escolha.estaDisponivel(protagonista)) {
                disponiveis.add(escolha);
            }
        }

        return disponiveis;
    }
}