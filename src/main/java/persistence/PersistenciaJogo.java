package persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.DadosPartida;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

// Salva e carrega os três slots usando arquivos JSON e a biblioteca Jackson.
public class PersistenciaJogo {

    public static final int TOTAL_SLOTS = 4;

    private final ObjectMapper mapper = new ObjectMapper();
    private final Path pasta = Path.of("saves");

    // Rejeita números fora dos três slots previstos.
    private Path caminho(int slot) {
        if (slot < 1 || slot > TOTAL_SLOTS) {
            throw new IllegalArgumentException("Slot inválido: " + slot);
        }
        return pasta.resolve("slot" + slot + ".json");
    }

    // Cria um JSON temporário antes de substituir o arquivo de destino.
    public void salvar(int slot, DadosPartida dados) throws IOException {
        Path destino = caminho(slot);
        Files.createDirectories(pasta);
        Path temporario = Files.createTempFile(pasta, "save-", ".tmp");

        try {
            dados.slot = slot;
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(temporario.toFile(), dados);
            Files.move(temporario, destino, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporario);
        }
    }

    // Devolve null se o slot estiver vazio; JSON inválido gera IOException.
    public DadosPartida carregar(int slot) throws IOException {
        Path arquivo = caminho(slot);
        if (!Files.isRegularFile(arquivo)) {
            return null;
        }

        DadosPartida dados = mapper.readValue(arquivo.toFile(), DadosPartida.class);
        if (dados == null || dados.versao != 1 || dados.slot != slot
                || dados.nomeProtagonista == null || dados.temperamento == null
                || dados.capituloAtualId <= 0 || dados.cenaAtualId <= 0) {
            throw new IOException("Save inválido ou incompatível no slot " + slot);
        }
        return dados;
    }

    // Informa se um slot já contém um arquivo de salvamento.
    public boolean existe(int slot) {
        return Files.isRegularFile(caminho(slot));
    }

    // Usa a data de modificação para localizar o último save.
    public int slotMaisRecente() {
        int maisRecente = 0;
        long maiorData = Long.MIN_VALUE;

        for (int slot = 1; slot <= TOTAL_SLOTS; slot++) {
            Path arquivo = caminho(slot);
            if (Files.isRegularFile(arquivo)) {
                long data = arquivo.toFile().lastModified();
                if (data > maiorData) {
                    maiorData = data;
                    maisRecente = slot;
                }
            }
        }
        return maisRecente;
    }

    //public boolean salvaConfig(){}
}
