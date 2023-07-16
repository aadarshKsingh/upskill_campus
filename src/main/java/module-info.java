module com.ayu.beats.ayub {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.bytedeco.ffmpeg;
    requires org.bytedeco.javacpp;
    requires org.bytedeco.javacv;
    requires javafx.media;
    requires uk.co.caprica.vlcj;


    opens com.ayu.beats.ayub to javafx.fxml;
    exports com.ayu.beats.ayub;
}