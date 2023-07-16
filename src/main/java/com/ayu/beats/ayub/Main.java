package com.ayu.beats.ayub;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {
    private static MainController mainController;
    private static Stage stage;
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader;
        fxmlLoader = new FXMLLoader(Main.class.getResource("ayu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("AYUBeats");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
        Main.stage = stage;
        mainController = fxmlLoader.getController();
        mainController.initializeList();
    }

    public static void main(String[] args)  {
        launch();
    }
    
    static MainController getController(){
        return mainController;
    }

    static Stage getStage(){
        return Main.stage;
    }
}