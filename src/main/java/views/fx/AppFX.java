package views.fx;

import javafx.application.Application;
import javafx.stage.Stage;

// Ponto de entrada da interface gráfica: cria o Navegador e abre a tela inicial.
public class AppFX extends Application {

    @Override
    public void start(Stage stage) {
        Navegador navegador = new Navegador(stage);
        navegador.mostrarMenu();
        stage.show();
    }
}
