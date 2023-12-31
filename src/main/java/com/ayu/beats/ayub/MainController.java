package com.ayu.beats.ayub;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
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
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URISyntaxException;
import java.util.*;

import static com.ayu.beats.ayub.MainController.playlist;


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

    public static String path = "";

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
    @FXML
    private ListView<TitledPane> playLists;
    private static List<File> audioFiles;
    private static ObservableList<String> playlistFiles;
    public static int currentIndex = 0;
    public static int totalIndex = 0;

    public static int playlistIndex = 0;
    public static int totalPlaylistIndex = 0;
    public static Map<String,List<File>> playlist = new HashMap<>();


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
        playlistFiles = initializePlaylist();
        ObservableList<TitledPane> playlistData = FXCollections.observableArrayList();
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

        for (int i = 0; i < playlistFiles.size(); i++) {
            String name = String.valueOf(playlistFiles.get(i));
            HBox header = new HBox();
            Label title = new Label(name);
            title.setStyle("-fx-text-fill:white;-fx-alignment:CENTER;");
            Button play = new Button("▶");
            play.setOnMouseClicked((event -> {
                Player.pausePlay();
                runPlaylist(name);
            }));
            play.setStyle("-fx-background-color:transparent;-fx-text-fill:red;-fx-alignment:CENTER;");
            header.setSpacing(50);
            header.getChildren().addAll(title,play);
            TitledPane titledPane = new TitledPane(null, getPlaylist(name));
            titledPane.setGraphic(header);
            VBox.setVgrow(titledPane, Priority.ALWAYS);
            titledPane.setCollapsible(true);
            titledPane.setExpanded(false);
           playlistData.add(titledPane);

        }

        playLists.setItems(playlistData);
        playLists.refresh();
        songsList.setStyle("-fx-padding-top: 10px;-fx-background-radius:20px;");
        songsList.setItems(listData);

        songsList.setCellFactory(listView -> new CustomSongListItem());
        songsList.setOnMouseClicked(setterEvent -> {
            if (setterEvent.getButton().toString().equals("PRIMARY")) {
                String selectedItem ;
                selectedItem = String.valueOf(songsList.getSelectionModel().getSelectedItem().title());
                String songPath=path+selectedItem;
                currentIndex = songsList.getSelectionModel().getSelectedIndex();
                updateMetadata(songPath);
                    player = new Player();
                    if(flag == 1 )
                            Player.mediaPlayer.controls().setPause(true);
                    player.play(songPath);
                    flag = 1;
                    play.setText("⏸");

            }

            playLists.setOnMouseClicked((event -> playLists.refresh()));
        });

        seekbar.setOnMouseDragged(this::handleSeekBar);
        seekbar.setOnMousePressed(this::handleSeekBar);


        // right click event listener
        songsList.setOnContextMenuRequested(event -> {
            String selectedItem = "";
            selectedItem = String.valueOf(songsList.getSelectionModel().getSelectedItem().title());

            metaData = Metadata.getMetadata(path + selectedItem);


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

    public void runPlaylist(String name) {
        player = new Player();

        List<File> playlistContent = new ArrayList<>(); // Initialize the playlistContent list
        for (int i = 0; i < playlist.get(name).size(); i++) {
            playlistContent.add(playlist.get(name).get(i));
        }
        updateMetadata(playlistContent.get(playlistIndex).getPath());

        player.play(playlistContent.get(playlistIndex).getPath());
        Player.mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void finished(MediaPlayer mediaPlayer) {
                if (playlistIndex != totalPlaylistIndex - 1) {
                    playlistIndex++;
                } else {
                    playlistIndex = (playlistIndex + 1) % totalPlaylistIndex;
                }
                runPlaylist(name);
            }
        });

    }

    public ObservableList<String> initializePlaylist() {

            ObservableList<String> playlistName = FXCollections.observableArrayList();
            playlistName.addAll(playlist.keySet());


        return playlistName;
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

    public VBox getPlaylist(String playlistName) {
        VBox plBox = new VBox();
        if (playlist.get(playlistName) != null) {
            for (int i = 0; i < playlist.get(playlistName).size(); i++) {
                Label name = new Label(playlist.get(playlistName).get(i).getName());
                name.setStyle("-fx-text-fill:white;-fx-padding:5px;");
                plBox.getChildren().add(name);
            }
        }
        return plBox;
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

    public void openAbout(MouseEvent mouseEvent) throws URISyntaxException {
        About.showAbout(new Stage());
    }
}
class CustomSongListItem extends ListCell<Song> {

    private final Button addToPlaylist;
    private String title;
    ContextMenu plContext = new ContextMenu();
    MenuItem newPlaylist = new MenuItem("+ New Playlist");
    CustomSongListItem(){
        plContext.getItems().add(newPlaylist);
            for (int i = 0; i < playlist.size(); i++) {
                MenuItem option = new MenuItem(playlist.get(i).toString());
                plContext.getItems().add(i, option);
        }


        addToPlaylist = new Button("❤");
        addToPlaylist.setStyle("-fx-background-color: transparent;-fx-text-fill:red;");
        addToPlaylist.setOnMouseClicked((event)->{
            if(event.getButton().toString().equals( "PRIMARY"))
                plContext.show(addToPlaylist,event.getScreenX(),event.getScreenY());

            newPlaylist.setOnAction((event1 -> {
                Platform.runLater(()->{
                    Stage newPLStage = new Stage();
                    newPLStage.setTitle("Enter playlist Name");

                    //TODO: check for playlist availability

                    VBox PLBox = new VBox();
                    TextField tf = new TextField();
                    tf.setMinWidth(120);
                    tf.setMinHeight(70);
                    Button confirm = new Button("Confirm");
                    confirm.setStyle("-fx-padding:5px;-fx-background-color:red;");
                    Button cancel = new Button("Cancel");
                    cancel.setStyle("-fx-padding:5px;-fx-background-color:red;");
                    cancel.setAlignment(Pos.CENTER);
                    confirm.setStyle("-fx-padding:5px;-fx-background-color:red;");
                    confirm.setAlignment(Pos.CENTER);
                    PLBox.setAlignment(Pos.CENTER);
                    VBox.setMargin(tf,new Insets(0,10,0,10));
                    VBox.setMargin(confirm,new Insets(10,30,10,30));
                    VBox.setMargin(cancel,new Insets(10,30,10,30));
                    PLBox.getChildren().addAll(tf,confirm,cancel);
                    Scene PLName = new Scene(PLBox,300,200);
                    newPLStage.setScene(PLName);
                    newPLStage.show();
                    cancel.setOnAction(event2 -> {newPLStage.close();});
                    confirm.setOnAction(event2 -> generatePlayList(event2, tf.getText(), newPLStage));

                });
            }));
        });
    }

    private void generatePlayList(Event event,String playlistName,Stage PLStage){
        if (title.length() != 0 && Main.getController().playlist != null) {
            List<File> files = new ArrayList<>();
            files.add(new File(MainController.path + title));

            if(playlist.containsKey(playlistName)){
                playlist.get(playlistName).addAll(files);
            }
            else{
                Main.getController().playlist.put(playlistName, files);
            }
        }


        Main.getController().initializeList();
        PLStage.close();
    }


    @Override
    protected void updateItem(Song song, boolean empty) {
        super.updateItem(song, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            this.title = song.title();
            Text title = new Text(song.title());
            title.setFill(Paint.valueOf("#e6eaf1"));
            HBox hbox = new HBox();
            hbox.getChildren().add(addToPlaylist);
            hbox.getChildren().add(title);
            hbox.setMaxWidth(600);
            hbox.setPadding(new Insets(5, 2, 5, 7));
            hbox.setStyle("-fx-background-color: #314158; -fx-background-radius: 5px;-fx-text-fill:#e6eaf1;-fx-margin: 5px;");
            hbox.setPrefWidth(Region.USE_COMPUTED_SIZE);
            HBox.setHgrow(hbox, Priority.ALWAYS);
            setGraphic(hbox);
            getStyleClass().add("no-clip");

        }
    }

}



record Song(String title) {
}




