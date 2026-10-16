package models;

import java.util.ArrayList;
import java.util.List;

// Guarda apenas os dados que mudam durante uma partida.
public class DadosPartida {

    public int versao = 1;
    public int slot;
    public String nomeSave;
    public long dataSalvamento;
    public String resumo;

    public int idProtagonista;
    public String nomeProtagonista;
    public String temperamento;
    public int memoria;
    public int estabilidadeEmocional;
    public int culpaPercebida;
    public int confiancaFauan;
    public int vinculoPassos;
    public int suspeitaToin;
    public String posturaPaternidade;
    public List<PistaSalva> pistas = new ArrayList<>();

    public int capituloAtualId;
    public int cenaAtualId;
    public boolean brunoSobreviveu;

    // O Jackson pode criar este objeto ao ler o arquivo JSON.
    public DadosPartida() {
    }

    // Registra em um objeto todos os valores necessários para retomar a partida.
    public static DadosPartida capturar(
            EstadoJogo estado,
            boolean brunoSobreviveu,
            int slot) {
        Protagonista protagonista = estado.getProtagonista();
        DadosPartida dados = new DadosPartida();

        dados.slot = slot;
        dados.dataSalvamento = System.currentTimeMillis();
        dados.idProtagonista = protagonista.getId();
        dados.nomeProtagonista = protagonista.getNome();
        dados.temperamento = protagonista.getTemperamento();
        dados.memoria = protagonista.getMemoria();
        dados.estabilidadeEmocional = protagonista.getEstabilidadeEmocional();
        dados.culpaPercebida = protagonista.getCulpaPercebida();
        dados.confiancaFauan = protagonista.getConfiancaFauan();
        dados.vinculoPassos = protagonista.getVinculoPassos();
        dados.suspeitaToin = protagonista.getSuspeitaToin();
        dados.posturaPaternidade = protagonista.getPosturaPaternidade();

        for (Pista pista : protagonista.getPistas()) {
            dados.pistas.add(new PistaSalva(pista.getNome(), pista.getDescricao()));
        }

        dados.capituloAtualId = estado.getCapituloAtual().getId();
        dados.cenaAtualId = estado.getCenaAtual().getId();
        dados.brunoSobreviveu = brunoSobreviveu;
        dados.resumo = estado.getCapituloAtual().getTitulo()
                + " - " + estado.getCenaAtual().getTitulo();

        return dados;
    }

    // Reconstrói Katarina usando os valores do save, sem refazer as decisões antigas.
    public Protagonista restaurarProtagonista() {
        Protagonista protagonista = new Protagonista(
                idProtagonista, nomeProtagonista, temperamento
        );

        // Os métodos ajustar... somam deltas; por isso subtraímos o valor inicial.
        protagonista.ajustarMemoria(memoria - protagonista.getMemoria());
        protagonista.ajustarEstabilidade(
                estabilidadeEmocional - protagonista.getEstabilidadeEmocional());
        protagonista.ajustarCulpa(culpaPercebida - protagonista.getCulpaPercebida());
        protagonista.ajustarConfiancaFauan(
                confiancaFauan - protagonista.getConfiancaFauan());
        protagonista.ajustarVinculoPassos(
                vinculoPassos - protagonista.getVinculoPassos());
        protagonista.ajustarSuspeitaToin(
                suspeitaToin - protagonista.getSuspeitaToin());
        protagonista.setPosturaPaternidade(posturaPaternidade);

        if (pistas != null) {
            for (PistaSalva pista : pistas) {
                protagonista.adicionarPista(new Pista(pista.nome, pista.descricao));
            }
        }

        return protagonista;
    }

    // Representa cada pista no JSON, sem exigir mudanças em Pista.java.
    public static class PistaSalva {
        public String nome;
        public String descricao;

        public PistaSalva() {
        }

        public PistaSalva(String nome, String descricao) {
            this.nome = nome;
            this.descricao = descricao;
        }
    }
}