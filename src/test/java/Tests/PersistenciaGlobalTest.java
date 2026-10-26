
package persistence;

import models.DadosGlobais;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PersistenciaGlobalTest {

    @TempDir
    Path temp;

    // Redireciona perfil.json para uma pasta descartável.
    private PersistenciaGlobal isolada() throws Exception {
        PersistenciaGlobal p = new PersistenciaGlobal();

        Path pasta = temp.resolve("saves-teste");

        Field fPasta = PersistenciaGlobal.class.getDeclaredField("pasta");
        Field fCaminho = PersistenciaGlobal.class.getDeclaredField("caminhoArquivo");

        fPasta.setAccessible(true);
        fCaminho.setAccessible(true);

        fPasta.set(p, pasta);
        fCaminho.set(p, pasta.resolve("perfil.json"));

        return p;
    }

    @Test
    void semArquivoUsaPreferenciasPadrao() throws Exception {
        DadosGlobais p = isolada().carregar();

        assertNotNull(p.corPrimaria);
        assertNotNull(p.corSecundaria);
        assertEquals(0, p.finaisDesbloqueados.size());
    }

    @Test
    void salvaEReabreConfiguracoesEConquistas() throws Exception {
        PersistenciaGlobal p = isolada();

        DadosGlobais dados = DadosGlobais.criarPadrao(
                "VERDE", "AMARELO", "NEGRITO"
        );

        dados.registrarFinal("FINAL_TESTE");
        dados.registrarExtra("FIM?");

        p.salvar(dados);

        DadosGlobais lido = p.carregar();

        assertEquals("VERDE", lido.corPrimaria);
        assertEquals("AMARELO", lido.corSecundaria);
        assertTrue(lido.finaisDesbloqueados.contains("FINAL_TESTE"));
        assertTrue(lido.extrasDesbloqueados.contains("FIM?"));
    }

    @Test
    void jsonInvalidoVoltaAoPerfilPadrao() throws Exception {
        PersistenciaGlobal p = isolada();

        Path pasta = temp.resolve("saves-teste");

        Files.createDirectories(pasta);

        Files.writeString(
                pasta.resolve("perfil.json"),
                "{invalido",
                StandardCharsets.UTF_8
        );

        DadosGlobais recuperado = p.carregar();

        assertNotNull(recuperado.corPrimaria);
        assertTrue(recuperado.finaisDesbloqueados.isEmpty());
    }
}
