package com.ayu.beats.ayub;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class FetchFiles {

    private static final String[] extensions = {".mp3",".flac",".wav",".ogg",".aac",".wma"};
    public static List<File> fetchAudioFiles(File directory){
        List<File> audioFiles = new ArrayList<>();

        if(directory.exists()){
            File[] files = directory.listFiles();
            for(File file: Objects.requireNonNull(files)){
                if (file.isDirectory()) audioFiles.addAll(fetchAudioFiles(file));
                else for (String extension : extensions) {
                    if (file.getName().toLowerCase().endsWith(extension)) audioFiles.add(file);
                }
            }

        }
        return audioFiles;
    }


}
