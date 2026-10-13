package persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.DadosGlobais;
import views.Terminal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

// Salva e carrega o perfil do jogador usando um único arquivo JSON.
public class PersistenciaGlobal {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Path pasta = Path.of("saves");
    private final Path caminhoArquivo = pasta.resolve("perfil.json");

    // Cria um JSON temporário antes de substituir o arquivo de destino (prevenção contra corrupção).
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

    // Tenta ler o JSON; se o arquivo não existir ou falhar, devolve o perfil padrão.
    public DadosGlobais carregar() {
        if (!Files.isRegularFile(caminhoArquivo)) {
            // Arquivo não existe: primeira vez jogando. Devolve o padrão verde.
            return DadosGlobais.criarPadrao(
                    Terminal.ANSI_GREEN,
                    Terminal.ANSI_YELLOW,
                    ""
            );
        }

        try {
            DadosGlobais dados = mapper.readValue(caminhoArquivo.toFile(), DadosGlobais.class);

            // Validação de integridade
            if (dados == null || dados.versao != 1 || dados.corPrimaria == null) {
                return DadosGlobais.criarPadrao(Terminal.ANSI_GREEN, Terminal.ANSI_YELLOW, "");
            }
            return dados;

        } catch (IOException e) {
            // Se o arquivo estiver corrompido, imprime no console (opcional) e devolve o padrão.
            System.err.println("Aviso: Falha ao ler perfil.json. Usando configurações padrão.");
            return DadosGlobais.criarPadrao(Terminal.ANSI_GREEN, Terminal.ANSI_YELLOW, "");
        }
    }
}