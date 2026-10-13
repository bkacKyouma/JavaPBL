package persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.DadosGlobais;
import views.Terminal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

// Salva e carrega as preferências e conquistas globais em um único arquivo JSON.
public class PersistenciaGlobal {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Path pasta = Path.of("saves");
    private final Path caminhoArquivo = pasta.resolve("perfil.json");

    // Cria um JSON temporário antes de substituir o arquivo de destino.
    public void salvar(DadosGlobais dados) throws IOException {
        Files.createDirectories(pasta);
        Path temporario = Files.createTempFile(pasta, "perfil-", ".tmp");

        try {
            dados.dataUltimaModificacao = System.currentTimeMillis();
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(temporario.toFile(), dados);
            Files.move(temporario, caminhoArquivo, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporario);
        }
    }

    // Lê o arquivo de perfil ou devolve a configuração padrão caso não exista ou seja inválido.
    public DadosGlobais carregar() {
        if (!Files.isRegularFile(caminhoArquivo)) {
            return DadosGlobais.criarPadrao(
                    Terminal.ANSI_GREEN,
                    Terminal.ANSI_YELLOW,
                    ""
            );
        }

        try {
            DadosGlobais dados = mapper.readValue(caminhoArquivo.toFile(), DadosGlobais.class);

            // Verifica se os campos essenciais do perfil foram preenchidos corretamente.
            if (dados == null || dados.versao != 1 || dados.corPrimaria == null) {
                return DadosGlobais.criarPadrao(Terminal.ANSI_GREEN, Terminal.ANSI_YELLOW, "");
            }
            return dados;

        } catch (IOException e) {
            System.err.println("Aviso: Falha ao ler perfil.json. Usando configurações padrão.");
            return DadosGlobais.criarPadrao(Terminal.ANSI_GREEN, Terminal.ANSI_YELLOW, "");
        }
    }
}