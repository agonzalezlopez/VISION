import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

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
        Imgproc.cvtColor(processedMat,processedMat,Imgproc.COLOR_RGB2GRAY);

        //Canny detection to track edges
        Imgproc.Canny(processedMat,processedMat,200,25);

        //Dilate to erase gaps in img, use 3x3 (new Mat creates this kernel) and (-1,-1) default values for now
        //only one iteration of dilation per func call
        Imgproc.dilate(processedMat,processedMat,new Mat(), new Point(-1,-1),1);

        return processedMat;
    }

    public static boolean markEdgeContours(final Mat processsedMat, final Mat originalMat){
        ArrayList<MatOfPoint> allcontours = new ArrayList<>();

        //Finds contours using kernel and uses uncompressed data(use chain simple later)
        Imgproc.findContours(processsedMat,allcontours,new Mat(processsedMat.rows(),processsedMat.cols(),processsedMat.type()),Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_NONE);

        // fucntion that returns true or false given that it contains only contours,
        // the boolean is determined through a test ()
        final List<MatOfPoint> filterContours = allcontours.stream()
                .filter(contour -> {
                    double contourArea = Imgproc.contourArea(contour);
                    Rect boundingRect = Imgproc.boundingRect(contour);

                    boolean isNoise = (contourArea>=1000);
                    if(isNoise){
                        Imgproc.putText(originalMat,"Area: " + contourArea,
                                new Point(boundingRect.x + boundingRect.width, boundingRect.y + boundingRect.height),2,2,new Scalar(0,0,255));
                        MatOfPoint2f distance = new MatOfPoint2f();
                        contour.convertTo(distance,CvType.CV_32F);
                        Imgproc.approxPolyDP(distance,distance,0.02*Imgproc.arcLength(distance,true),true);

                        //apply put text feature here
                    }

                    return isNoise;
                }).toList();

        return false;
    }

}
