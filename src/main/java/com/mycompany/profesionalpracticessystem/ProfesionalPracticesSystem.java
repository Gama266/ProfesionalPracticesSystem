package com.mycompany.profesionalpracticessystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * Autor: gamal
 */
public class ProfesionalPracticesSystem extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
 Parent root = FXMLLoader.load(getClass().getResource("/gui/fxml/CoordinatorMenuGUI.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setTitle("Sistema de Prácticas Profesionales");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
