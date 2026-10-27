import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.core.Point;

import javax.swing.*;
import java.awt.*;


public class MainVideo extends JFrame{
    private static JLabel cam;
    private static JLabel cam2; // This will be GreyScale

    private static VideoCapture stream; // live camera feed

    public MainVideo(){
        cam = new JLabel();
        cam2 = new JLabel();
        //Insert the Panels
        cam.setPreferredSize(new Dimension(480,640));
        cam2.setPreferredSize(new Dimension(480,640));
        makeJframe(cam,cam2);
        stream = new VideoCapture(0);

    }

    private static Runnable runHelper(final JLabel cameraF, final JLabel processedF, VideoCapture liveCam){
        return () -> {
            Mat frame = new Mat();
            System.out.println("Frame made");
            while(true){
                if(liveCam.read(frame)){
                    System.out.println("Frame read");
                    Mat processed = new Mat();
                    // Util function to processImg
                    processed = MainVideo_Utils.processedImg(frame);
                    // Util function to mark outer contour
                    MainVideo_Utils.markEdgeContours(processed,frame);
                    // drawImage from frame to cameraFeed
                    MainVideo_Utils.drawImg(frame,cameraF);
                    // Draw processed Mat onto processed Feed cam/panel
                    MainVideo_Utils.drawImg(processed,processedF);
                }else{
                    System.out.println("System Failed");
                }

            }
        };
    }

    // ... is the same as making a list, but with an unknown amount
    private void makeJframe(JLabel... panels){
        JFrame frame = new JFrame();
        frame.setSize(new Dimension(700*panels.length,600));
        frame.setLayout(new GridLayout(1, panels.length));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        for(JLabel panel : panels){
            frame.add(panel);
        }
        frame.setVisible(true);
    }

    public static void main(String[] args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        MainVideo mnVid = new MainVideo();
        new Thread(mnVid.runHelper(cam,cam2,stream)
        ).start();

        System.out.println("loaded");
    }

}