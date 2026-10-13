package models;

import java.util.ArrayList;
import java.util.List;

// Guarda as configurações de interface e os conteúdos desbloqueados pelo jogador.
public class DadosGlobais {

    public int versao = 1;
    public long dataUltimaModificacao;

    public String corPrimaria;
    public String corSecundaria;
    public String modo;

    public List<String> finaisDesbloqueados = new ArrayList<>();
    public List<String> extrasDesbloqueados = new ArrayList<>();

    // O Jackson pode criar este objeto ao ler o arquivo JSON.
    public DadosGlobais() {
    }

    // Instancia um objeto de perfil com as configurações iniciais de formatação.
    public static DadosGlobais criarPadrao(String corPadrao, String destaquePadrao, String modoPadrao) {
        DadosGlobais dados = new DadosGlobais();

        dados.dataUltimaModificacao = System.currentTimeMillis();
        dados.corPrimaria = corPadrao;
        dados.corSecundaria = destaquePadrao;
        dados.modo = modoPadrao;

        return dados;
    }

    // Adiciona um final alcançado à lista sem repetir registros existentes.
    public void registrarFinal(String nomeFinal) {
        if (!finaisDesbloqueados.contains(nomeFinal)) {
            finaisDesbloqueados.add(nomeFinal);
        }
    }

    // Adiciona um conteúdo extra desbloqueado à lista sem repetir registros existentes.
    public void registrarExtra(String nomeExtra) {
        if (!extrasDesbloqueados.contains(nomeExtra)) {
            extrasDesbloqueados.add(nomeExtra);
        }
    }
}