package models;

import java.util.HashMap;
import java.util.Map;

public abstract class Personagem {

    private final int id;
    private String nome;
    private final Map<String, String> spritesPorHumor = new HashMap<>();

    public Personagem(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void adicionarSprite(String humor, String arquivo) {
        if (humor != null && arquivo != null) {
            spritesPorHumor.put(humor.trim().toUpperCase(), arquivo);
        }
    }

    public String getSprite(String humor) {
        if (spritesPorHumor.isEmpty()) {
            return null;
        }

        if (humor != null) {
            String encontrado = spritesPorHumor.get(humor.trim().toUpperCase());
            if (encontrado != null) {
                return encontrado;
            }
        }

        String neutro = spritesPorHumor.get("NEUTRO");
        if (neutro != null) {
            return neutro;
        }

        return spritesPorHumor.values().iterator().next();
    }
}