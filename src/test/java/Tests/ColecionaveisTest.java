
package views;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@ResourceLock(Resources.SYSTEM_IN)
class ColecionaveisTest {

    @Test
    void mostraConquistasEPermiteRetornarComEnter()
            throws Exception {

        InputStream inAnterior = System.in;
        PrintStream outAnterior = System.out;

        try {
            System.setIn(
                    new ByteArrayInputStream(
                            "\n\n".getBytes(StandardCharsets.UTF_8)
                    )
            );

            ByteArrayOutputStream saida =
                    new ByteArrayOutputStream();

            System.setOut(
                    new PrintStream(
                            saida,
                            true,
                            StandardCharsets.UTF_8
                    )
            );

            Colecionaveis extras = new Colecionaveis();

            extras.exibirConquistaFim();
            extras.exibirConquistaBruno();

            String texto = saida.toString(StandardCharsets.UTF_8);

            assertTrue(texto.contains("FIM DA LINHA"));
            assertTrue(texto.contains("FINAL INDIGESTO"));

        } finally {
            System.setIn(inAnterior);
            System.setOut(outAnterior);
        }
    }
}
