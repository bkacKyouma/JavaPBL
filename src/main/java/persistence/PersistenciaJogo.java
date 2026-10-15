package persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.DadosPartida;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

// Salva e carrega os slots de partida usando arquivos JSON e a biblioteca Jackson.
public class PersistenciaJogo {

    public static final int TOTAL_SLOTS = 4;

    private final ObjectMapper mapper = new ObjectMapper();
    private final Path pasta = Path.of("saves");

    // Lê a pasta "saves", analisa os arquivos .json e extrai os nomes personalizados dos slots.
    public String[] listarNomesSaves() {
        String[] nomes = new String[TOTAL_SLOTS];
        // Preenche com "Vazio" por padrão
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            nomes[i] = "Vazio";
        }

        if (!Files.exists(pasta)) return nomes;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(pasta, "slot*.json")) {
            for (Path entry : stream) {
                String fileName = entry.getFileName().toString();
                // Exemplo de formato esperado: slot1_Bruno.json
                if (fileName.startsWith("slot") && fileName.endsWith(".json")) {
                    try {
                        int slotIndex = Character.getNumericValue(fileName.charAt(4));
                        if (slotIndex >= 1 && slotIndex <= TOTAL_SLOTS) {
                            int underscoreIdx = fileName.indexOf('_');
                            if (underscoreIdx != -1) {
                                // Recorta a string para pegar apenas o nome do jogador
                                nomes[slotIndex - 1] = fileName.substring(underscoreIdx + 1, fileName.length() - 5);
                            } else {
                                // Fallback para saves antigos sem nome (ex: slot1.json)
                                nomes[slotIndex - 1] = "Sem Nome";
                            }
                        }
                    } catch (Exception ignored) { }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao listar o diretório de saves.");
        }
        return nomes;
    }

    // Procura na pasta o arquivo correspondente ao slot (slot1_Katarina.json) sem depender do nome.
    private Path encontrarCaminhoSlot(int slot) {
        if (slot < 1 || slot > TOTAL_SLOTS) throw new IllegalArgumentException("Slot inválido");
        if (!Files.exists(pasta)) return null;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(pasta, "slot" + slot + "*.json")) {
            for (Path entry : stream) {
                return entry; // Retorna o primeiro que der match no número do slot
            }
        } catch (IOException ignored) {}
        return null;
    }

    // Apaga saves antigos do mesmo slot (para não acumular) e cria o novo com o apelido.
    public void salvar(int slot, DadosPartida dados, String nomeSave) throws IOException {
        Files.createDirectories(pasta);

        Path antigo = encontrarCaminhoSlot(slot);
        if (antigo != null) {
            Files.deleteIfExists(antigo);
        }

        Path destino = pasta.resolve("slot" + slot + "_" + nomeSave + ".json");
        Path temporario = Files.createTempFile(pasta, "save-", ".tmp");

        try {
            dados.slot = slot;
            mapper.writerWithDefaultPrettyPrinter().writeValue(temporario.toFile(), dados);
            Files.move(temporario, destino, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporario);
        }
    }

    // Carrega o jogo apenas informando o número do slot. A persistência se vira para achar o nome!
    public DadosPartida carregar(int slot) throws IOException {
        Path arquivo = encontrarCaminhoSlot(slot);
        if (arquivo == null || !Files.isRegularFile(arquivo)) {
            return null;
        }

        DadosPartida dados = mapper.readValue(arquivo.toFile(), DadosPartida.class);
        if (dados == null || dados.versao != 1 || dados.slot != slot) {
            throw new IOException("Save inválido ou incompatível no slot " + slot);
        }
        return dados;
    }

    public boolean existe(int slot) {
        return encontrarCaminhoSlot(slot) != null;
    }

    //public void excluirSave()
/*
    // Usa a data de modificação para localizar o último save jogado.
    public int slotMaisRecente() {
        int maisRecente = 0;
        long maiorData = Long.MIN_VALUE;

        for (int slot = 1; slot <= TOTAL_SLOTS; slot++) {
            Path arquivo = encontrarCaminhoSlot(slot);
            if (arquivo != null && Files.isRegularFile(arquivo)) {
                long data = arquivo.toFile().lastModified();
                if (data > maiorData) {
                    maiorData = data;
                    maisRecente = slot;
                }
            }
        }
        return maisRecente;
    }

 */
}