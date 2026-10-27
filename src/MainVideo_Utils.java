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

/**
 * This Class will contain all the data processing functions and will handle Mats accordingly
 */
public class MainVideo_Utils {
    private MainVideo_Utils(){}

    public static Mat processedImg(final Mat unprocessedMat){
        Mat processedMat = new Mat(unprocessedMat.rows(),unprocessedMat.cols(),unprocessedMat.type());

        //Removes any color "noise" that may cause issues
        Imgproc.blur(unprocessedMat,processedMat,new Size(7,7));

        //Greyscale Img
        Imgproc.cvtColor(processedMat,processedMat,Imgproc.COLOR_BGR2GRAY);

        //Canny detection to track edges
        Imgproc.Canny(processedMat,processedMat,60,90);

        //Dilate to erase gaps in img, use 3x3 (new Mat creates this kernel) and (-1,-1) default values for now
        //only one iteration of dilation per func call
        Imgproc.dilate(processedMat,processedMat,new Mat(), new Point(-1,-1),1);

        return processedMat;
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
                    List<Point> corners = new ArrayList<>();
                    double contourArea = Imgproc.contourArea(contour);
                    Rect boundingRect = Imgproc.boundingRect(contour);

                    boolean isNoise = (contourArea>=1000);
                    boolean isApprox = false;
                    if(isNoise){
                        Imgproc.putText(originalMat,"Area: " + contourArea,
                                new Point(boundingRect.x + boundingRect.width, boundingRect.y + boundingRect.height),2,2,new Scalar(0,0,255));
                        MatOfPoint2f distance = new MatOfPoint2f();
                        MatOfPoint2f approx = new MatOfPoint2f();
                        contour.convertTo(distance,CvType.CV_32F);
                        Imgproc.approxPolyDP(distance,approx,0.02*Imgproc.arcLength(distance,true),true);
                        isApprox = (4<=approx.total() && approx.total() <= 6);
                        //apply put text feature here
                        Imgproc.putText(originalMat,"Pts: " + approx.toArray().length,
                                new Point(boundingRect.x + boundingRect.width, boundingRect.y + boundingRect.height+50),2,1,new Scalar(0,0,255));

                        if(isApprox){
                            for(Point p : approx.toArray()){
                                corners.add(p);
                            }
                        }
                    }

                    return isNoise && isApprox;
                }).toList();

        Imgproc.drawContours(originalMat,filterContours,-1,new Scalar(255,0,0),1);
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
