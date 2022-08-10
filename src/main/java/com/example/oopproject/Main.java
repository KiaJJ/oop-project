package com.example.oopproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("LoginScene - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("LoginScene.css").toExternalForm());
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}