package models;

// Representa um conteúdo da galeria (final ou conquista) e se o jogador já o desbloqueou.
public class ItemGaleria {

    private final String categoria;
    private final String titulo;
    private final String descricao;
    private final boolean desbloqueado;

    public ItemGaleria(String categoria, String titulo, String descricao, boolean desbloqueado) {
        this.categoria = categoria;
        this.titulo = titulo;
        this.descricao = descricao;
        this.desbloqueado = desbloqueado;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isDesbloqueado() {
        return desbloqueado;
    }
}
