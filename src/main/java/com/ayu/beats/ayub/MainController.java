package com.ayu.beats.ayub;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Base64;
import java.util.List;
import java.util.Map;



public class MainController {
    @FXML
    public Text trackName;
    public Text artistName;
    public Text endTime ;
    public ImageView cover;
    public Button play;
    public ProgressBar seekbar ;
    public Text startTime ;
    public Player player;
    public Button eq;
    public Button next;
    public Button previous;
    public Button loop;
    String path = "";

    @FXML
    private void exitApp() {
        Platform.exit();
    }

    static Map<String, String> metaData = null;

    @FXML
    private void minimizeApp() {
        Stage stage = (Stage) Main.getStage().getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private ListView<Song> songsList;
    private static List<File> audioFiles;
    public static int currentIndex = 0;
    public static int totalIndex = 0;

    public static int flag = 0;
    private boolean loopState = false;
    public void initializeList()  {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            path =  System.getenv("USERPROFILE") + "\\Music\\";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            path =  System.getProperty("user.home") + "/Music/";
        }

        audioFiles = FetchFiles.fetchAudioFiles(new File(path));
        ObservableList<Song> listData = FXCollections.observableArrayList();
        totalIndex = audioFiles.size();
        ScrollPane mDpane = new ScrollPane();
        mDpane.setPrefWidth(400);
        mDpane.setPrefHeight(800);
        Button closeButton = new Button("Exit");

        for (int i = 0; i < audioFiles.size(); i++) {
            String name = audioFiles.get(i).getName();
            Song song = new Song(name);
            listData.add(i, song);
        }

        songsList.setStyle("-fx-padding-top: 10px;-fx-background-radius:20px;");
        songsList.setItems(listData);
        songsList.setCellFactory(listView -> new CustomSongListItem());


        songsList.setOnMouseClicked(setterEvent -> {
            if (setterEvent.getButton().toString().equals("PRIMARY")) {
                String selectedItem ;
                selectedItem = String.valueOf(songsList.getSelectionModel().getSelectedItem().title());
                String songPath=path+selectedItem;
                currentIndex = songsList.getSelectionModel().getSelectedIndex();


//                System.out.println(path);


                updateMetadata(songPath);
                    player = new Player();
                    if(flag == 1 )
                            Player.mediaPlayer.controls().setPause(true);
                    player.play(songPath);
                    flag = 1;
                    play.setText("⏸");

            }
        });



        seekbar.setOnMouseDragged(this::handleSeekBar);
        seekbar.setOnMousePressed(this::handleSeekBar);


        // right click event listener
        songsList.setOnContextMenuRequested(event -> {
            String selectedItem = "";
            selectedItem = String.valueOf(songsList.getSelectionModel().getSelectedItem().title());

            metaData = Metadata.getMetadata("/home/aadarshksingh/Downloads/64Gram Desktop/" + selectedItem);


            Stage metaDataStage = new Stage();


            // new stage for metadata
            metaDataStage.setOnShowing(event3 -> {
                VBox md = new VBox();
                md.setPadding(new Insets(10, 10, 10, 10));

                md.getChildren().add(closeButton);
                for (Map.Entry<String, String> entry : metaData.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    Label metaDataLabel = new Label(key + ": " + value);
                    metaDataLabel.setWrapText(true);
                    md.getChildren().add(metaDataLabel);
                }
                mDpane.setPrefWidth(500);
                mDpane.setPrefHeight(900);
                mDpane.setPrefViewportHeight(1000);
                mDpane.setPrefViewportWidth(700);
                mDpane.setContent(md);

                Scene showMetaData = new Scene(md, 400, 500);
                metaDataStage.setResizable(true);
                metaDataStage.setScene(showMetaData);
            });


            //Exit button
            closeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 4px 8px;");
            closeButton.setAlignment(Pos.TOP_RIGHT);
            closeButton.setOnMouseClicked(event1 -> {
                VBox meta = (VBox) metaDataStage.getScene().getRoot();
                meta.getChildren().clear();
                mDpane.setContent(null);
                metaDataStage.close();
            });
            metaDataStage.show();

        });


        //next button handler
        next.setOnMouseClicked((event)->{
            Player.mediaPlayer.controls().setPosition(0);
            if(currentIndex!=totalIndex-1){
                currentIndex++;
            }else {
                currentIndex = (currentIndex+1)%totalIndex;
            }
            updateMetadata(audioFiles.get(currentIndex).getPath());
            Player.mediaPlayer.media().play(audioFiles.get(currentIndex).getPath());

        });

        //previous button handler
        previous.setOnMouseClicked((event)->{
            Player.mediaPlayer.controls().setPosition(0);
            if(currentIndex!=0){
                currentIndex--;
            }else {
                currentIndex = totalIndex-1;
            }
            updateMetadata(audioFiles.get(currentIndex).getPath());
            Player.mediaPlayer.media().play(audioFiles.get(currentIndex).getPath());

        });

        //pause play handler
        play.setOnMouseClicked((event -> {
            Player.pausePlay();
            if(Player.getStatus().equals("playing"))
                play.setText("▶");
            else
                play.setText("⏸");
        }));

        loop.setOnMouseClicked((event -> {
            if(!loopState){
                loopState = true;
                loop.setStyle("-fx-text-fill:red;-fx-background-color:#1F2937;");
                Player.mediaPlayer.controls().setRepeat(true);
            }else{
                loopState = false;
                loop.setStyle("-fx-text-fill:white;-fx-background-color:#1F2937;");
                Player.mediaPlayer.controls().setRepeat(false);
            }
        }));

    }

    public static void nextTrack(){
      MainController main = new MainController();
      main.next.fire();
    }


    public void updateMetadata(String path){
        metaData = Metadata.getMetadata(path);

        String title = metaData.get("title");
        String artist = metaData.get("artist");
        String duration = metaData.get("duration");
        System.out.println(title);
        System.out.println(artist);
        System.out.println(duration);
        trackName.setText(title);
        artistName.setText(artist);
        endTime.setText(duration);
        String coverPath = metaData.get("cover");

        if(coverPath!=null){
            byte[] imageBytes = Base64.getDecoder().decode(coverPath);
            ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
            cover.setImage(new Image(bais));
            cover.setSmooth(true);
        }

    }


    //drag press seekbar
    private void handleSeekBar(MouseEvent mouseEvent) {
        double progress =mouseEvent.getX()/seekbar.getWidth();
        seekbar.setProgress(progress);

        Player.mediaPlayer.controls().setPosition((float) progress);
    }


    //equalizer event handler
    public void showEqualizer(){
        eq.setOnAction(event -> {
            try {
                EqualizeR equalizer = new EqualizeR();
                equalizer.show();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

}
class CustomSongListItem extends ListCell<Song> {


    @Override
    protected void updateItem(Song song, boolean empty) {
        super.updateItem(song, empty);

        if (empty || song == null) {
            setText(null);
            setGraphic(null);
        } else {
            System.out.println(song.title());
            Text title = new Text(song.title());
            title.setFill(Paint.valueOf("#e6eaf1"));
            HBox hbox = new HBox(title);
            hbox.setMaxWidth(600);
            hbox.setPadding(new Insets(5, 2, 5, 7));
            hbox.setStyle("-fx-background-color: #314158; -fx-background-radius: 5px;-fx-text-fill:#e6eaf1;-fx-margin: 5px;");
            hbox.setPrefWidth(Region.USE_COMPUTED_SIZE);
            HBox.setHgrow(hbox, Priority.ALWAYS);
            setGraphic(hbox);

        }
    }

}


record Song(String title) {
}




