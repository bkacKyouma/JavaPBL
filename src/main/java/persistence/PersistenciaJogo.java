package persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.DadosPartida;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

// Salva e carrega os slots de partida usando arquivos JSON e a biblioteca Jackson.
public class PersistenciaJogo {

    public static final int TOTAL_SLOTS = 4;

    private final ObjectMapper mapper = new ObjectMapper();
    private final Path pasta = Path.of("saves");

    // Lê os JSONs dos slots, formata apelido, data e capítulo em Strings e libera os objetos da memória.
    public String[] listarNomesSaves() {
        String[] nomes = new String[TOTAL_SLOTS];
        SimpleDateFormat formatadorData = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (int slot = 1; slot <= TOTAL_SLOTS; slot++) {
            try {
                DadosPartida dados = carregar(slot);
                if (dados != null) {
                    String apelido = (dados.nomeSave != null && !dados.nomeSave.isEmpty())
                            ? dados.nomeSave
                            : "Sem Nome";
                    String data = formatadorData.format(new Date(dados.dataSalvamento));
                    String progresso = (dados.resumo != null) ? dados.resumo : "Capítulo " + dados.capituloAtualId;

                    nomes[slot - 1] = apelido + " | " + data + " | " + progresso;
                } else {
                    nomes[slot - 1] = "Vazio";
                }
            } catch (Exception e) {
                nomes[slot - 1] = "Vazio";
            }
        }
        return nomes;
    }

    // Procura na pasta o arquivo correspondente ao slot sem depender do sufixo do nome.
    private Path encontrarCaminhoSlot(int slot) {
        if (slot < 1 || slot > TOTAL_SLOTS) throw new IllegalArgumentException("Slot inválido");
        if (!Files.exists(pasta)) return null;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(pasta, "slot" + slot + "*.json")) {
            for (Path entry : stream) {
                return entry;
            }
        } catch (IOException ignored) {}
        return null;
    }

    // Remove o arquivo anterior do slot e grava o novo JSON com os metadados atualizados.
    public void salvar(int slot, DadosPartida dados, String nomeSave) throws IOException {
        Files.createDirectories(pasta);

        Path antigo = encontrarCaminhoSlot(slot);
        if (antigo != null) {
            Files.deleteIfExists(antigo);
        }

        Path destino = pasta.resolve("slot" + slot + ".json");
        Path temporario = Files.createTempFile(pasta, "save-", ".tmp");

        try {
            dados.slot = slot;
            dados.nomeSave = nomeSave;
            mapper.writerWithDefaultPrettyPrinter().writeValue(temporario.toFile(), dados);
            Files.move(temporario, destino, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporario);
        }
    }

    // Devolve null se o slot estiver vazio; JSON inválido gera IOException.
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

    // Informa se um slot já contém um arquivo de salvamento.
    public boolean existe(int slot) {
        return encontrarCaminhoSlot(slot) != null;
    }

    // Localiza e remove o arquivo JSON correspondente ao slot informado.
    public boolean excluirSave(int slot) throws IOException {
        Path arquivo = encontrarCaminhoSlot(slot);
        if (arquivo != null && Files.isRegularFile(arquivo)) {
            return Files.deleteIfExists(arquivo);
        }
        return false;
    }
}