package views;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuViewFX extends Application {

    @Override
    public void start(Stage stage) {
        // 1. Cria os elementos visuais
        Label titulo = new Label("A NOITE QUE NÃO SE APAGA");

        Button btnNovaPartida = new Button("Nova Partida");
        Button btnCarregar = new Button("Carregar");
        Button btnColecionaveis = new Button("Colecionáveis");
        Button btnConfig = new Button("Preferências");
        Button btnSair = new Button("Sair");

        // Ação simples para testar o clique
        btnNovaPartida.setOnAction(e -> System.out.println("Clicou em Nova Partida"));
        btnSair.setOnAction(e -> stage.close());

        // 2. Empilha tudo na vertical com 10px de espaço entre cada item
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(
                titulo,
                btnNovaPartida,
                btnCarregar,
                btnColecionaveis,
                btnConfig,
                btnSair
        );

        // 3. Coloca o layout numa Cena (400x350 pixels) e exibe na Janela (Stage)
        Scene cena = new Scene(layout, 400, 350);

        stage.setTitle("Menu Inicial");
        stage.setScene(cena);
        stage.show();
    }
}