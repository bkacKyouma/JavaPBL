package models;

import java.util.ArrayList;
import java.util.List;

// Guarda as configurações visuais e conquistas globais (independente de save slots).
public class DadosGlobais {

    public int versao = 1;
    public long dataUltimaModificacao;

    // Configurações de interface do terminal
    public String corPrimaria;
    public String corSecundaria;
    public String modo;

    // Conquistas (Finais alcançados pelo jogador)
    public List<String> finaisDesbloqueados = new ArrayList<>();

    public List<String> extrasDesbloqueados = new ArrayList<>();


    // O Jackson pode criar este objeto ao ler o arquivo JSON.
    public DadosGlobais() {
    }

    // Retorna um objeto com as configurações padrão de interface (verde/amarelo/sem modo).
    public static DadosGlobais criarPadrao(String corPadrao, String destaquePadrao, String modoPadrao) {
        DadosGlobais dados = new DadosGlobais();

        dados.dataUltimaModificacao = System.currentTimeMillis();
        dados.corPrimaria = corPadrao;
        dados.corSecundaria = destaquePadrao;
        dados.modo = modoPadrao;

        return dados;
    }

    // Adiciona um novo final à lista, garantindo que não haja duplicatas.
    public void registrarFinal(String nomeFinal) {
        if (!finaisDesbloqueados.contains(nomeFinal)) {
            finaisDesbloqueados.add(nomeFinal);
        }
    }
    public void registrarExtra(String nomeExtra) {
        if (!extrasDesbloqueados.contains(nomeExtra)) {
            extrasDesbloqueados.add(nomeExtra);
        }
    }
}