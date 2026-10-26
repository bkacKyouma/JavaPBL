
package models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DadosPartidaTest {

    @Test
    void capturaERestauraTudoQueMudaNaPartida() {
        Protagonista p = new Protagonista(1, "Katarina", "SANGUE_FRIO");

        p.ajustarMemoria(75);
        p.ajustarEstabilidade(10);
        p.ajustarCulpa(-15);
        p.ajustarConfiancaFauan(30);
        p.ajustarVinculoPassos(-20);
        p.ajustarSuspeitaToin(40);
        p.setPosturaPaternidade("DUVIDA_ABERTA");
        p.adicionarPista(
                new Pista("Cheiro de gasolina", "Uma lembranca")
        );

        EstadoJogo estado = new EstadoJogo(p);
        Capitulo cap = new Capitulo(4, "O queixo do Toin");

        cap.adicionarCena(
                new Cena(404, "A carta", "Texto", "INTERROGATORIO")
        );

        estado.adicionarCapitulo(cap);
        estado.iniciarJogo();

        DadosPartida save = DadosPartida.capturar(estado, true, 3);
        Protagonista restaurada = save.restaurarProtagonista();

        assertEquals(3, save.slot);
        assertEquals(4, save.capituloAtualId);
        assertEquals(404, save.cenaAtualId);
        assertTrue(save.brunoSobreviveu);
        assertTrue(save.dataSalvamento > 0);
        assertTrue(save.resumo.contains("A carta"));

        assertEquals(75, restaurada.getMemoria());
        assertEquals(60, restaurada.getEstabilidadeEmocional());
        assertEquals(35, restaurada.getCulpaPercebida());
        assertEquals(80, restaurada.getConfiancaFauan());
        assertEquals(30, restaurada.getVinculoPassos());
        assertEquals(90, restaurada.getSuspeitaToin());
        assertEquals("DUVIDA_ABERTA", restaurada.getPosturaPaternidade());
        assertEquals("Uma lembranca",
                restaurada.getPistas().get(0).getDescricao());

        assertNotSame(p, restaurada);
    }

    @Test
    void funcionaComJacksonSemPerderPistas() throws Exception {
        DadosPartida save = new DadosPartida();

        save.slot = 2;
        save.nomeSave = "Meu save";
        save.nomeProtagonista = "Katarina";
        save.temperamento = "SANGUE_FRIO";

        save.pistas.add(
                new DadosPartida.PistaSalva("Alarme", "Foi desligado")
        );

        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(save);
        DadosPartida rec = mapper.readValue(json, DadosPartida.class);

        assertEquals(2, rec.slot);
        assertEquals("Meu save", rec.nomeSave);
        assertEquals("Alarme", rec.pistas.get(0).nome);
        assertEquals("Foi desligado", rec.pistas.get(0).descricao);
    }
}
