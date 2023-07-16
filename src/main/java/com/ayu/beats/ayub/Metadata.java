package com.ayu.beats.ayub;

import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.avutil.AVDictionaryEntry;
import org.bytedeco.ffmpeg.global.avformat;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.bytedeco.ffmpeg.global.avutil.AV_DICT_IGNORE_SUFFIX;
import static org.bytedeco.ffmpeg.global.avutil.av_dict_get;

public class Metadata {
    private static String duration;
    private static Frame frame;
    public static Map<String,String> getMetadata(String filePath){
        Map<String,String> metadata= new HashMap<>();
        BufferedImage image = null;
        AVFormatContext formatContext = new AVFormatContext(null);
        System.out.println(filePath);

        if ( avformat.avformat_open_input(formatContext,filePath, null, null)!= 0) {
            throw new RuntimeException("Failed to open audio file");
        }

//         Find the audio stream
//        if (avformat.avformat_find_stream_info(formatContext, (AVInputFormat) null) < 0) {
//            throw new RuntimeException("Failed to find audio stream");
//        }

        // gives metadata
        AVDictionaryEntry entry = null;
        while ((entry = av_dict_get(formatContext.metadata(), "", entry, AV_DICT_IGNORE_SUFFIX)) != null)
            metadata.put(entry.key().getString().toString(), entry.value().getString().toString());

        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filePath);
            grabber.start();

            long durationInSeconds = grabber.getLengthInTime() / avutil.AV_TIME_BASE;
            long minutes = durationInSeconds / 60;
            long seconds = durationInSeconds % 60;
            while((frame=grabber.grabFrame())!=null){
                if(frame.image!=null && frame.imageWidth>0 && frame.imageHeight>0){
                     image = new Java2DFrameConverter().getBufferedImage(frame,1.0);
                }
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            if(image!=null){
                ImageIO.write(image,"png",baos);
                byte[] imageBytes = baos.toByteArray();
                String imageB64 = Base64.getEncoder().encodeToString(imageBytes);

                metadata.put("duration", minutes +":"+ seconds);
                metadata.put("cover", imageB64);
            }else{
                metadata.put("cover",null);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return metadata;
    }

}
