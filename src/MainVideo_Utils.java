import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

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


}
