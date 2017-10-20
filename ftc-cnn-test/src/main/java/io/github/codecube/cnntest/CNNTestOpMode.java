package io.github.codecube.cnntest;

import android.graphics.Bitmap;
import android.os.Environment;

import org.lasarobotics.vision.android.Cameras;
import org.lasarobotics.vision.image.Drawing;
import org.lasarobotics.vision.opmode.TestableVisionOpMode;
import org.lasarobotics.vision.util.color.ColorGRAY;
import org.lasarobotics.vision.util.color.ColorRGBA;
import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

/**
 * Created by josh on 10/9/17.
 */

public class CNNTestOpMode extends TestableVisionOpMode {
    private CaffeCNN mCaffeCnn;
    private String mCurrentModelName = "";
    private boolean mClassify = false;
    private int mImagesToCapture = 0;
    private int mImageLabel = 0;
    private long mLastImageTime = 0;
    private static final int IMAGE_INTERVAL = 200;

    @Override
    public void init() {
        super.init();

        this.setCamera(Cameras.PRIMARY);
        this.setFrameSize(new Size(640, 480));
    }

    public void loadModel(String newModelName) {
        if(mCurrentModelName != newModelName) {
            mCaffeCnn = new CaffeCNN(newModelName);
            mCurrentModelName = newModelName;
        }
    }

    private static final int EXPORT_SIZE = 256;
    private Bitmap mInter = null;
    private Mat mScaled = null;
    @Override
    public Mat frame(Mat rgba, Mat gray) {
        if(mImagesToCapture > 0) {
            if(mLastImageTime + IMAGE_INTERVAL < System.currentTimeMillis()) {
                mLastImageTime = System.currentTimeMillis();
                if(mScaled == null) {
                    mScaled = new Mat();
                }
                Imgproc.resize(rgba, mScaled, new Size(EXPORT_SIZE, EXPORT_SIZE));
                try {
                    if(mInter == null) {
                        mInter = Bitmap.createBitmap(EXPORT_SIZE, EXPORT_SIZE,
                                Bitmap.Config.ARGB_8888);
                    }
                    Utils.matToBitmap(mScaled, mInter);
                } catch (CvException e) {
                    return rgba;
                }
                File folder = new File(Environment.getExternalStorageDirectory() +
                        "/caffe_files/training_images");
                if(!folder.exists()) {
                    folder.mkdir();
                }
                File output = new File(folder, mLastImageTime + "_LABEL_" + mImageLabel + ".png");
                try {
                    mInter.compress(Bitmap.CompressFormat.PNG, 100,
                            new FileOutputStream(output));
                } catch(FileNotFoundException e) { }
                mImagesToCapture--;
            }
            Drawing.drawText(rgba, mImagesToCapture + " left to capture.", new Point(50, 24), 1.0f,
                    new ColorGRAY(255));
        }
        if(mClassify) {
            if(mScaled == null) {
                mScaled = new Mat();
            }
            Imgproc.resize(rgba, mScaled, new Size(EXPORT_SIZE, EXPORT_SIZE));

            float[] classification = mCaffeCnn.classify(mScaled);
            DecimalFormat percent = new DecimalFormat();
            percent.setMaximumFractionDigits(2);
            for(int i = 0; i < 10; i++) {
                Drawing.drawText(rgba, percent.format(classification[i]*100.0f) + "%",
                        new Point(0, 24 * (i + 1)), 1.0f, new ColorRGBA(0, 0, 255, 255));
            }

            return rgba;
        }
        return rgba;
    }

    public boolean toggleClassification() {
        mClassify = !mClassify;
        return mClassify;
    }

    private long lastLabelCheck = 0;
    private static final long PROMPT_USER_INTERVAL = 5000;
    public void captureImages(int label) {
        mImagesToCapture += 100;
        mImageLabel = label;
    }

    public boolean shouldPromtUserForLabel() {
        boolean tr = System.currentTimeMillis() > lastLabelCheck + PROMPT_USER_INTERVAL;
        lastLabelCheck = System.currentTimeMillis();
        return tr;
    }
}
