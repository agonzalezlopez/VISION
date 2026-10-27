import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.opencv.imgproc.CLAHE;

/**
 * This Class will contain all the data processing functions and will handle Mats accordingly
 */
public class MainVideo_Utils {
    private MainVideo_Utils(){}

    public static Mat processedImg(final Mat unprocessedMat){
        Mat gray = new Mat();
        //Greyscale Img
        Imgproc.cvtColor(unprocessedMat,gray,Imgproc.COLOR_BGR2GRAY);

        // Class to help with lighting issues
        CLAHE clah = Imgproc.createCLAHE(3,new Size(8,8));
        clah.apply(gray,gray);

        //Removes any color "noise" that may cause issues
        Imgproc.GaussianBlur(gray,gray,new Size(7,7),0);

        // used to determine lower bound and upper bound thresholds
        double mean = Core.mean(gray).val[0];
        double lower = Math.max(0,0.67 * mean);
        double upper = Math.min(255,1.33 * mean); // max val of 255

        //Canny detection to track edges
        Imgproc.Canny(gray,gray,lower,upper);

        //Dilate to erase gaps in img, use 3x3 (new Mat creates this kernel) and (-1,-1) default values for now
        //only one iteration of dilation per func call
        Imgproc.dilate(gray,gray,new Mat(), new Point(-1,-1),2);

        return gray;
    }

    public static void markEdgeContours(final Mat processsedMat, final Mat originalMat){
        ArrayList<MatOfPoint> allcontours = new ArrayList<>();

        //Finds contours using kernel and uses uncompressed data(use chain simple later)
        Mat hierarchy = new Mat();
        Imgproc.findContours(processsedMat,allcontours,hierarchy,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_NONE);

        // fucntion that returns true or false given that it contains only contours,
        // the boolean is determined through a test ()
        final List<MatOfPoint> filterContours = allcontours.stream()
                .filter(contour -> {
                    double contourArea = Imgproc.contourArea(contour);
                    Rect boundingRect = Imgproc.boundingRect(contour);

                    boolean isNoise = (contourArea>=1000);
                    boolean isApprox = false;
                    boolean isRightAngle = false;

                    if(isNoise){
                        Imgproc.putText(originalMat,"Area: " + contourArea,
                                new Point(boundingRect.x + boundingRect.width, boundingRect.y + boundingRect.height),2,2,new Scalar(0,0,255));
                        MatOfPoint2f distance = new MatOfPoint2f(contour.toArray());
                        MatOfPoint2f approx = new MatOfPoint2f();

                        Imgproc.approxPolyDP(distance,approx,0.03*Imgproc.arcLength(distance,true),true);

                        isApprox = (4<=approx.total() && approx.total() <= 6);

                        //apply put text feature here
                        Imgproc.putText(originalMat,"Pts: " + approx.toArray().length,
                                new Point(boundingRect.x + boundingRect.width, boundingRect.y + boundingRect.height+50),2,1,new Scalar(0,0,255));

                        if(isApprox){
                            Point[] corners = approx.toArray();
                            int rightAngleCounter= 0;

                            for(int i = 0; i < corners.length;i++){
                                Point a = corners[(i-1+corners.length) % corners.length];
                                Point b = corners[i]; // center of vertex
                                Point c = corners[(i+1) % corners.length];

                                //vectors from to other points
                                Point ab = new Point(a.x - b.x, a.y - b.y);
                                Point bc = new Point(c.x - b.x,c.y - b.y);

                                //getting hypotnuse/length
                                double lenAB = Math.sqrt(ab.x*ab.x + ab.y*ab.y);
                                double lenBC = Math.sqrt(bc.x*bc.x + bc.y*bc.y);

                                // get angle using lengths from B using dot product
                                double angle = (ab.x*bc.x + ab.y*bc.y) / (lenAB * lenBC);

                                if(Math.abs(angle) <= 0.5){
                                    rightAngleCounter++;
                                }


                            }
                            // majority have to be right angles
                            isRightAngle = rightAngleCounter >= corners.length - 1;

                            Imgproc.putText(originalMat,"RightAngles: " + rightAngleCounter,
                                    new Point(boundingRect.x + boundingRect.width, boundingRect.y + boundingRect.height+100)
                                    ,2,1,new Scalar(0,0,255));
                        }
                    }

                    return isNoise && isApprox && isRightAngle;
                }).toList();

        Imgproc.drawContours(originalMat,filterContours,-1,new Scalar(255,0,0),5);
    }

    public static void drawImg(final Mat processed, JLabel cam){
        BufferedImage buff = matToBuff(processed);
        ImageIcon icon = new ImageIcon(buff);
        SwingUtilities.invokeLater(() -> cam.setIcon(icon));
    }

    public static BufferedImage matToBuff(final Mat processed){
        BufferedImage buffValues = new BufferedImage(processed.width(),processed.height(),processed.channels() == 1? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR);

        //writes pixels in img
        final WritableRaster raster = buffValues.getRaster();

        DataBufferByte bufferByte = (DataBufferByte) raster.getDataBuffer();

        processed.get(0,0, bufferByte.getData());

        return buffValues;
    }


}
