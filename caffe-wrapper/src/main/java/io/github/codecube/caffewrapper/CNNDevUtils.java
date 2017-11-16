package io.github.codecube.caffewrapper;

import android.graphics.Bitmap;
import android.os.Environment;

import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by josh on 11/15/17.
 */

public class CNNDevUtils {
    private static int sExportSize = 256; //The dimensions the saved images should be.
    private static Mat sScaled = null;
    private static Bitmap sToExport = null;

    public static void setExportSize(int size) {
        sExportSize = size;
    }

    public static int getExportSize() {
        return sExportSize;
    }

    public static File getStorageDirectory() {
        File tr = new File(Environment.getExternalStorageDirectory() + "/caffe_files");
        if(!tr.exists()) {
            tr.mkdir();
        }
        return tr;
    }

    public static File getTrainingImageStorageDirectory() {
        File tr = new File(getStorageDirectory(), "training_images");
        if(!tr.exists()) {
            tr.mkdir();
        }
        return tr;
    }

    public static boolean saveTrainingImage(Mat rgba, int label) {
        if(sScaled == null) { //Saves memory if the method is never used.
            sScaled = new Mat();
        }

        //Resize the input image onto the temporary mat.
        Imgproc.resize(rgba, sScaled, new Size(sExportSize, sExportSize));

        //Try to convert that to a bitmap for exporting.
        try {
            if(sToExport == null) {
                sToExport = Bitmap.createBitmap(sExportSize, sExportSize,
                        Bitmap.Config.ARGB_8888);
            }
            Utils.matToBitmap(sScaled, sToExport);
        } catch (CvException e) {
            e.printStackTrace();
            return false;
        }

        //Make sure to append _LABEL_[label_number] to the end of the file name!
        File output = new File(getTrainingImageStorageDirectory(), System.currentTimeMillis() +
                "_LABEL_" + label + ".png");
        try {
            //Try to save the image to a file.
            sToExport.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(output));
        } catch(FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static List<String> getAvailableNetworks() {
        List<String> tr = new ArrayList<>();
        for(String name : getStorageDirectory().list()) {
            if(name.contains(".caffemodel")) {
                //Remove the .caffemodel extension
                tr.add(name.substring(0, name.length() - 11));
            }
        }
        return tr;
    }
}
