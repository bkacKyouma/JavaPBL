
package persistence;

import models.DadosPartida;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PersistenciaJogoTest {

    @TempDir
    Path pastaTemporaria;

    // Direciona os arquivos de teste para uma pasta temporária.
    private PersistenciaJogo criarPersistenciaIsolada() throws Exception {
        PersistenciaJogo p = new PersistenciaJogo();

        Field campo = PersistenciaJogo.class.getDeclaredField("pasta");
        campo.setAccessible(true);
        campo.set(p, pastaTemporaria.resolve("saves-teste"));

        return p;
    }

    // Cria um save de exemplo para os testes.
    private DadosPartida dados(int slot) {
        DadosPartida d = new DadosPartida();

        d.slot = slot;
        d.nomeSave = "Pessoa " + slot;
        d.nomeProtagonista = "Katarina";
        d.temperamento = "SANGUE_FRIO";
        d.capituloAtualId = 2;
        d.cenaAtualId = 201;
        d.resumo = "Tres dias antes - Visita";
        d.dataSalvamento = 1792005833465L;
        d.brunoSobreviveu = true;

        d.pistas.add(
                new DadosPartida.PistaSalva(
                        "Cheiro de gasolina",
                        "Pista do jogo"
                )
        );

        return d;
    }

    @Test
    void salvaCarregaEExcluiUmDosQuatroSlots() throws Exception {
        PersistenciaJogo p = criarPersistenciaIsolada();

        assertEquals(4, PersistenciaJogo.TOTAL_SLOTS);
        assertNull(p.carregar(3));

        p.salvar(3, dados(3), "Primeiro save");

        assertTrue(p.existe(3));

        DadosPartida lido = p.carregar(3);

        assertEquals("Primeiro save", lido.nomeSave);
        assertEquals(201, lido.cenaAtualId);
        assertTrue(lido.brunoSobreviveu);
        assertEquals("Cheiro de gasolina", lido.pistas.get(0).nome);

        assertTrue(p.excluirSave(3));
        assertFalse(p.existe(3));
        assertNull(p.carregar(3));
        assertFalse(p.excluirSave(3));
    }

    @Test
    void sobrescreveMesmoSlotSemAlterarOsOutros() throws Exception {
        PersistenciaJogo p = criarPersistenciaIsolada();

        p.salvar(1, dados(1), "Auto save");
        p.salvar(4, dados(4), "Save quatro");

        DadosPartida novo = dados(1);
        novo.cenaAtualId = 504;

        p.salvar(1, novo, "Novo auto save");

        assertEquals(504, p.carregar(1).cenaAtualId);
        assertEquals("Novo auto save", p.carregar(1).nomeSave);
        assertEquals(201, p.carregar(4).cenaAtualId);
        assertEquals("Save quatro", p.carregar(4).nomeSave);
    }

    @Test
    void mostraMetadadosDoSaveELidaComJsonCorrompidoNaListagem()
            throws Exception {

        PersistenciaJogo p = criarPersistenciaIsolada();

        p.salvar(2, dados(2), "Raphael");

        String[] nomes = p.listarNomesSaves();

        assertEquals("Vazio", nomes[0]);
        assertTrue(nomes[1].contains("Raphael"));
        assertTrue(nomes[1].contains("Tres dias antes"));
        assertEquals("Vazio", nomes[2]);

        Path arquivo = pastaTemporaria.resolve(
                "saves-teste/slot2.json"
        );

        Files.writeString(
                arquivo,
                "{json incompleto",
                StandardCharsets.UTF_8
        );

        assertThrows(IOException.class, () -> p.carregar(2));
        assertEquals("Vazio", p.listarNomesSaves()[1]);
    }

    @Test
    void impedeSlotForaDoIntervalo() throws Exception {
        PersistenciaJogo p = criarPersistenciaIsolada();

        assertThrows(
                IllegalArgumentException.class,
                () -> p.carregar(0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> p.existe(5)
        );
    }
}
