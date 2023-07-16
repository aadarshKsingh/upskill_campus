package com.ayu.beats.ayub;

import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.Equalizer;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class EqualizeR {
    private static final int BAND_COUNT = 10;

    private final MediaPlayerFactory mediaPlayerFactory = Player.getFactory();
    private final EmbeddedMediaPlayer mediaPlayer = Player.getPlayer();
    private Equalizer equalizer;
    Stage primaryStage = new Stage();


    public void show() {

        // Create the equalizer
        equalizer = mediaPlayerFactory.equalizer().newEqualizer();
        VBox root = new VBox();
        for (int i = 0; i < BAND_COUNT; i++) {
            Text text = new Text("Band "+i);
            text.setStyle("-fx-padding-left:10px");
            Slider slider = new Slider(-20, 20, 0);
            slider.setStyle("-fx-padding:20px;");
            int bandIndex = i;
            slider.valueProperty().addListener((observable, oldValue, newValue) -> {
                double gain = newValue.doubleValue();
//                double frequency = calculateFrequency(bandIndex);
                equalizer.setAmp(bandIndex, (float) gain);
               mediaPlayer.audio().setEqualizer(equalizer);

            });
            root.getChildren().add(text);
            root.getChildren().add(slider);

        }



        // Create the scene
        Scene scene = new Scene(root, 400, 300);

        // Set up the stage
        primaryStage.setTitle("Equalizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public void stop() {
        // Clean up resources
        mediaPlayer.release();
        mediaPlayerFactory.release();
    }
    private double calculateFrequency(int bandIndex) {
        double minFrequency = 60.0; // Minimum frequency for the equalizer
        double maxFrequency = 16000.0; // Maximum frequency for the equalizer
        double logMin = Math.log10(minFrequency);
        double logMax = Math.log10(maxFrequency);
        double step = (logMax - logMin) / (BAND_COUNT - 1);
        double frequency = Math.pow(10, logMin + (step * bandIndex));
        return frequency;
    }
//
    }

