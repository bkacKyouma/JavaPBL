package models;

public class Dialogo {

    private String personagem;
    private String conteudo;
    private String tipo;

    public Dialogo(String personagem, String conteudo, String tipo) {
        this.personagem = personagem;
        this.conteudo = conteudo;
        this.tipo = tipo;
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
}