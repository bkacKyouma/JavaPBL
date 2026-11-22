package controllers;

import models.DadosGlobais;
import persistence.PersistenciaGlobal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Intermedia a tela de configurações e o perfil global salvo em disco (cor, negrito e itálico).
// Padrão Singleton: só existe uma cópia das preferências em memória, compartilhada por todas as telas.
// Padrão Observer (Subject): avisa os observadores sempre que uma preferência muda.
public class ConfigController {

    private static ConfigController instancia;

    private final List<ConfigObserver> observadores = new ArrayList<>();

    public static final String ANSI_VERDE = "\u001B[32m";
    public static final String ANSI_CIANO = "\u001B[36m";
    public static final String ANSI_AMARELO = "\u001B[33m";
    public static final String ANSI_BRANCO = "\u001B[37m";

    private static final String ANSI_NEGRITO = "\u001B[1m";
    private static final String ANSI_ITALICO = "\u001B[3m";

    private final PersistenciaGlobal persistencia = new PersistenciaGlobal();
    private final DadosGlobais perfil = persistencia.carregar();

    // Construtor restrito ao pacote: a aplicação usa getInstance(); os testes podem criar instâncias isoladas.
    ConfigController() {
    }

    public static synchronized ConfigController getInstance() {
        if (instancia == null) {
            instancia = new ConfigController();
        }
        return instancia;
    }

    public void adicionarObservador(ConfigObserver observador) {
        observadores.add(observador);
    }

    public void removerObservador(ConfigObserver observador) {
        observadores.remove(observador);
    }

    private void notificarObservadores() {
        for (ConfigObserver observador : new ArrayList<>(observadores)) {
            observador.aoMudarConfiguracao();
        }
    }

    public String getCorPrimariaAnsi() {
        return perfil.corPrimaria;
    }

    public String getCorSecundariaAnsi() {
        return perfil.corSecundaria;
    }

    public String getModo() {
        return perfil.modo == null ? "" : perfil.modo;
    }

    // Traduz o código ANSI salvo no perfil para uma cor hexadecimal usada pela interface gráfica.
    public String getCorHex() {
        return corParaHex(perfil.corPrimaria);
    }

    public static String corParaHex(String ansi) {
        if (ANSI_CIANO.equals(ansi)) return "#4fd1e0";
        if (ANSI_AMARELO.equals(ansi)) return "#f2c14e";
        if (ANSI_BRANCO.equals(ansi)) return "#f2ece4";
        return "#7fd68c";
    }

    public void setCor(String ansi) {
        perfil.corPrimaria = ansi;
        notificarObservadores();
    }

    public boolean isNegrito() {
        return getModo().contains(ANSI_NEGRITO);
    }

    public boolean isItalico() {
        return getModo().contains(ANSI_ITALICO);
    }

    public void setNegrito(boolean ativo) {
        alternarModo(ANSI_NEGRITO, ativo);
    }

    public void setItalico(boolean ativo) {
        alternarModo(ANSI_ITALICO, ativo);
    }

    // Liga ou desliga um código de estilo dentro da String de modo, sem repetir códigos.
    private void alternarModo(String codigo, boolean ativo) {
        String modo = getModo().replace(codigo, "");
        perfil.modo = ativo ? modo + codigo : modo;
        notificarObservadores();
    }

    // Grava as preferências para que sejam mantidas na próxima execução.
    public void salvar() throws IOException {
        persistencia.salvar(perfil);
    }
}
