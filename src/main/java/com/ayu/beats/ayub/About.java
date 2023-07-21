package com.ayu.beats.ayub;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class About {

    static Stage primaryStage;

    public static void showAbout(Stage stage) throws URISyntaxException {
       primaryStage = stage;
        VBox textsBox = new VBox();
        textsBox.setAlignment(Pos.CENTER);
        textsBox.setSpacing(10);

        // Create multiple Text nodes
        Text text1 = new Text("Access Your Unforgettable Beats");
        text1.setOnMouseClicked((event -> {
        }));
        Text text2 = new Text("Made by");
        Text text3 = new Text("aadarshKsingh");
        text3.setOnMouseClicked((event)->{

                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/aadarshKsingh"));
                } catch (IOException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }

        });

        textsBox.getChildren().addAll(text1, text2, text3);
        String imagePath = Main.class.getResource("images/image.jpg").toURI().toString();
        Image image = new Image(imagePath);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        Circle circle = new Circle(100);
        circle.setFill(new ImagePattern(image));

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> primaryStage.close());
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(circle, textsBox, closeButton);

        Scene scene = new Scene(root, 400, 400);
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setTitle("About");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
