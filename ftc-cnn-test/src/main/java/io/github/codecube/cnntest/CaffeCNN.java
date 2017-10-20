package io.github.codecube.cnntest;

import android.os.Environment;

import com.sh1r0.caffe_android_lib.CaffeMobile;

import org.opencv.core.Mat;

import java.io.File;

/**
 * Created by josh on 10/13/17.
 */

public class CaffeCNN {
    private static final File sdcard = Environment.getExternalStorageDirectory();
    private static final String MODELS_FOLDER = sdcard.getAbsolutePath() + "/caffe_files";
    private String mName = "";
    private CaffeMobile mInstance;

    static {
        System.loadLibrary("caffe");
        System.loadLibrary("caffe_jni");
    }

    public CaffeCNN(String name) {
        mName = name;
        mInstance = new CaffeMobile();
        mInstance.setNumThreads(1);
        String path = MODELS_FOLDER + "/" + name;
        mInstance.loadModel(path + ".prototext", path + ".caffemodel");
    }

    private static int NN_START_SIZE = 256;
    public float[] classify(Mat image) {
        byte[] data = new byte[NN_START_SIZE*NN_START_SIZE*3], transposed = new byte[3*NN_START_SIZE*NN_START_SIZE];
        int index = 0;
        image.get(0, 0, data);
        float sum = 0.0f;
        for(int x = 0; x < NN_START_SIZE; x++) {
            int xmd = x * NN_START_SIZE * 3;
            for(int y = 0; y < NN_START_SIZE; y++) {
                int ad = xmd + y * 3;
                for(int c = 0; c < 3; c++) {
                    transposed[((c*NN_START_SIZE)+y)*NN_START_SIZE+x] = data[ad+c];
                }
            }
        }
        return mInstance.getConfidenceScore(transposed, NN_START_SIZE, NN_START_SIZE);
    }
}
