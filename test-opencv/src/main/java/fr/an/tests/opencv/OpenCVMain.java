package fr.an.tests.opencv;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import nu.pattern.OpenCV;

public class OpenCVMain {

    public static void main(String[] args) {
        try {
            new OpenCVMain().run();
        } catch(Exception ex) {
            ex.printStackTrace(System.err);
            System.err.println("Failed .. exiting");
        }
    }

    private static void run() {
        OpenCV.loadShared();
        
        Mat imgData = loadImage("data/img.jpg");
        
        Scalar color = new Scalar(0, 0, 255);
        int thickness = 3;
        Point topLeftPt = new Point(10, 10);
        Point bottomRightPt = new Point(100, 100);
        Imgproc.rectangle(imgData, topLeftPt , bottomRightPt, color, thickness); 
    
        saveImage(imgData, "data/res-img.jpg");
    }
    
    public static Mat loadImage(String imagePath) {
        return Imgcodecs.imread(imagePath);
    }
    
    public static void saveImage(Mat imageMatrix, String targetPath) {
        Imgcodecs.imwrite(targetPath, imageMatrix);
    }

}
