package com.warung.haryati;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        java.util.Locale.setDefault(new java.util.Locale("id", "ID"));
        scene = new Scene(loadFXML("login"), 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Warung Haryati - Analisis FP-Growth");
        try {
            stage.getIcons().add(new Image(App.class.getResourceAsStream("/images/logo.png")));
        } catch (Exception e) {
            System.err.println("Gagal memuat ikon aplikasi: " + e.getMessage());
        }
        stage.setMaximized(true);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
