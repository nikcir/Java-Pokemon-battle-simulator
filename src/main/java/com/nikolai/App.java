package com.nikolai;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        switchTo("main.fxml");
        stage.setTitle("Pokedex");
        stage.show();
    }

    public static void switchTo(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/nikolai/" + fxml));
        Scene scene = new Scene(loader.load(), 600, 800);
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}