import javafx.application.Application;
import views.fx.AppFX;

// Classe de lançamento separada de Application para funcionar com Maven e com jar executável.
public class LauncherFX {
    public static void main(String[] args) {
        Application.launch(AppFX.class, args);
    }
}
