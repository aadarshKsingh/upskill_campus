package com.ayu.beats.ayub;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import static com.ayu.beats.ayub.MainController.*;


public class Player  extends  MediaPlayerEventAdapter{
    public static EmbeddedMediaPlayer mediaPlayer;
    public static MediaPlayerFactory factory;
    public static float currentPosition;
    public static MainController mainController;

    @Override
    public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
        super.positionChanged(mediaPlayer, newPosition);
        mainController.startTime.setText(String.valueOf(currentPosition));
    }

    public static void pausePlay(){
        if(mediaPlayer.status().isPlaying())
            mediaPlayer.controls().pause();
        else
            mediaPlayer.controls().play();
    }
    public static String getStatus(){
        if(mediaPlayer.status().isPlaying())
                return "playing";
        else
                return "paused";
    }

    public void play(String path){
        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
        Player.factory = mediaPlayerFactory;
        mediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
        PlayerListener listener = new PlayerListener();
        mediaPlayer.media().play(path);
        mediaPlayer.events().addMediaPlayerEventListener(listener);

    }


    public static float getCurrentSeek(){
        return mediaPlayer.status().position();
    }
    public static float getTotalDuration(){
        return mediaPlayer.status().length();
    }

    public static EmbeddedMediaPlayer getPlayer(){
        return mediaPlayer;
    }
    public static MediaPlayerFactory getFactory(){ return factory;}


}

class PlayerListener extends MediaPlayerEventAdapter{
//    private final Text startTime;


    @Override
    public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
        super.timeChanged(mediaPlayer, newTime);


        Platform.runLater(() -> {
            float TotalValue =  Player.getTotalDuration();
            float currentValue = newTime*100;
            double progress = newTime / TotalValue;
            Main.getController().startTime.setText(formatTime(newTime));
            Main.getController().seekbar.setProgress(progress);
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("ayu.fxml"));
                loader.setController(Main.getController());
                Stage stage = Main.getStage();

                stage.initModality(Modality.APPLICATION_MODAL);

                Parent root = loader.load();
                Scene scene = new Scene(root);

                stage.setScene(scene);

                stage.showAndWait();

            } catch (Exception ex) {
//                ex.printStackTrace();
            }
        });
    }

    public String formatTime(long milliseconds) {
        long totalSeconds = milliseconds / 1000;
        long minutes = (int) (totalSeconds / 60);
        long seconds = (int) (totalSeconds % 60);

        return String.format("%02d:%02d", minutes, seconds);
    }
    }




