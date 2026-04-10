package com.nikolai.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;


public class App extends Application {

    private static Stage primaryStage;
    private static Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    private static double screenWidth = screenBounds.getWidth();
    private static double screenHeight = screenBounds.getHeight();

    @Override
    public void start(Stage stage) throws IOException {
        // Set the primary stage and show the main menu.
        primaryStage = stage;
        switchTo("main.fxml");
        stage.setTitle("Pokemon");
        stage.show();
    }

    public static void switchTo(String fxml) throws IOException {
        // Load the specified FXML file and set it as the current scene. 
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/nikolai/" + fxml));
        Scene scene = new Scene(loader.load(), screenWidth/2, screenHeight*2/3);
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static double getScreenWidth() {
        return screenWidth;
    }

    public static double getScreenHeight() {
        return screenHeight;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}