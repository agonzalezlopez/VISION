import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.core.Point;

import javax.swing.*;
import java.awt.*;


public class MainVideo extends JFrame{
    private static JPanel panel;
    private static JLabel cam;
    private static JLabel cam2;

    private static VideoCapture vid; // live camera feed

    public MainVideo(){
        panel = new JPanel();
        panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        panel.setLayout(new GridLayout(2,2,10,10));


        cam = new JLabel("cam1",SwingConstants.CENTER);
        cam.setSize(150,150);
        cam.setVisible(true);

        cam2 = new JLabel("cam2",SwingConstants.CENTER);
        cam2.setSize(150,150);
        cam2.setVisible(true);


    }


    private static void Run(){

    }

    public static void main(String[] args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        MainVideo vid = new MainVideo();
        MainVideo.Run();

        System.out.println("loaded");
    }

}