package models;

public class Dialogo {

    private String personagem;
    private String conteudo;
    private String tipo;
    private String humor;

    public Dialogo(String personagem, String conteudo, String tipo) {
        this.personagem = personagem;
        this.conteudo = conteudo;
        this.tipo = tipo;
        this.humor = null;
    }

    public Dialogo(String personagem, String conteudo, String tipo, String humor) {
        this(personagem, conteudo, tipo);
        this.humor = humor;
    }

    public String getPersonagem() {
        return personagem;
    }

    public String getConteudo() {
        return conteudo;
    }

    public String getTipo() {
        return tipo;
    }

    public String getHumor() {
        return humor;
    }
}