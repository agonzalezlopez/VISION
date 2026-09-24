import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.core.Point;

import javax.swing.*;
import java.awt.*;


public class MainVideo extends JFrame{
    private static JPanel cam;
    private static JPanel cam2; // This will be GreyScale

    private static VideoCapture vid; // live camera feed

    public MainVideo(){
        cam = new JPanel();
        cam2 = new JPanel();
        //Insert the Panels using util

        VideoCapture stream = new VideoCapture(0);

    }

    private static Runnable runHelper(final JPanel cameraF, final JPanel processedF, VideoCapture liveCam){
        return () -> {
            Mat frame = new Mat();
            while(true){
                liveCam.read(frame);

                Mat processed = new Mat();
                // Util function to processImg
                // Util function to mark outer contour
                // drawImage from frame to cameraFeed
                // Draw processed Mat onto processed Feed cam/panel
            }
        };
    }
    private static void runs(Runnable detectionFunc){

    }


    public static void main(String[] args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        MainVideo vid = new MainVideo();

        System.out.println("loaded");
    }

}