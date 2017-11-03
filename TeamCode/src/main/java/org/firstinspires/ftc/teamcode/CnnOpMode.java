package org.firstinspires.ftc.teamcode;

import org.lasarobotics.vision.android.Cameras;
import org.lasarobotics.vision.opmode.VisionOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import io.github.codecube.caffewrapper.CaffeCNN;

/**
 * Created by josh on 10/27/17.
 */
@Disabled
public class CnnOpMode extends VisionOpMode {
    private CaffeCNN mCnn;

    @Override
    public void init() {
        super.init();

        this.setCamera(Cameras.PRIMARY);
        this.setFrameSize(new Size(640, 480));

        //Loads a network called jewel_side_detector, if it is loaded on the phone.
        mCnn = new CaffeCNN("jewel_side_detector");
    }

    @Override
    public Mat frame(Mat rgba, Mat gray) {
        float[] results = mCnn.classify(rgba);
        for(int i = 0; i < results.length; i++) {
            telemetry.addData("Confidence for category " + (i + 1) + ":", results[i]);
        }
        return rgba;
    }
}
