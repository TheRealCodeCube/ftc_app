package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.lasarobotics.vision.android.Cameras;
import org.lasarobotics.vision.opmode.ManualVisionOpMode;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.io.FileNotFoundException;
import java.util.List;

import io.github.codecube.caffewrapper.CNNDevUtils;
import io.github.codecube.caffewrapper.CaffeCNN;

/**
 * Created by josh on 10/27/17.
 */
@TeleOp(name="CNN Development (For Runnerbot)", group="Test")
public class CnnDevelopmentOpMode extends ManualVisionOpMode {
    private CaffeCNN mCnn = new CaffeCNN();
    private RunnerBot bot = new RunnerBot();
    private TankDriveControl control = new TankDriveControl();

    private enum Mode { CAPTURE, TEST }
    private Mode mMode = Mode.CAPTURE;
    private boolean mPressed = false; //True if a dpad button was pressed last frame.

    private List<String> mAvailableNetworks; //Cache names of available networks.
    private int mSelectedNetwork = -1; //The index of the network that should be tested.
    private boolean mClassify = false; //True if the network should be executed.

    private int mPicturesToCapture = 0; //How many more training images to capture.
    private int mTrainingLabel = 0; //The label to apply to captured images.
    private long mLastPictureCaptured = 0; //Timestamp of when the last training image was captured.
    private long PICTURE_CAPTURE_INTERVAL = 500; //How many milliseconds in between capturing each
                                                 //training image.

    @Override
    public void init() {
        super.init();

        this.setCamera(Cameras.PRIMARY);
        this.setFrameSize(new Size(640, 480));

        mAvailableNetworks = CNNDevUtils.getAvailableNetworks();
        if(mAvailableNetworks.size() > 0) { //If size == 0, mSelectedNetwork left at -1.
            mSelectedNetwork = 0;
        }

        bot.loadHardware(hardwareMap);
        control.setGamepad(gamepad1);
    }

    @Override
    public Mat frame(Mat rgba, Mat gray) {
        double[] input = control.getSpeedAndTurn(false);
        bot.drive(input[0], input[1]);
        int pressed = -1; //TODO: Replace this with an enum or a more logical system.
        if(gamepad1.dpad_left || gamepad1.dpad_right || gamepad1.dpad_up || gamepad1.dpad_down ||
                gamepad1.a || gamepad1.b) {
            if(!mPressed) {
                mPressed = true;
                if(gamepad1.dpad_left || gamepad1.dpad_right) {
                    //Scroll through possible modes.
                    if(mMode == Mode.CAPTURE) {
                        mMode = Mode.TEST;
                    } else {
                        mMode = Mode.CAPTURE;
                    }
                } else if(gamepad1.dpad_up) {
                    pressed = 0;
                } else if(gamepad1.dpad_down) {
                    pressed = 1;
                } else if(gamepad1.a) {
                    pressed = 2;
                } else if(gamepad1.b) {
                    pressed = 3;
                }
            }
        } else {
            mPressed = false;
        }

        if(mMode == Mode.CAPTURE) {
            if(pressed == 0) {
                mTrainingLabel = (mTrainingLabel + 1) % 10;
            } else if(pressed == 1) {
                //Equivalent to -1, but modulo does not like negative numbers.
                mTrainingLabel = (mTrainingLabel + 9) % 10;
            } else if(pressed == 2) {
                mPicturesToCapture += 100;
            } else if(pressed == 3) {
                mPicturesToCapture = 0;
            }
            telemetry.addData("Mode", "Capture Training Data");
            telemetry.addData("Status", (mPicturesToCapture > 0) ? "Capturing training data" :
                    "Waiting");
            telemetry.addData("Selected label", mTrainingLabel);
            telemetry.addData("Pictures Left To Capture", mPicturesToCapture);
        } else {
            if(pressed == 0) {
                mSelectedNetwork = (mSelectedNetwork + 1) % mAvailableNetworks.size();
            } else if (pressed == 1) {
                mSelectedNetwork = (mSelectedNetwork + mAvailableNetworks.size() - 1) %
                        mAvailableNetworks.size();
            } else if(pressed == 2) {
                mClassify = true;
            } else if(pressed == 3) {
                mClassify = false;
            }
            if(pressed != -1) { //If the user pressed anything to change the selected network.
                try {
                    mCnn.loadModel(mAvailableNetworks.get(mSelectedNetwork));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            telemetry.addData("Mode", "Test Trained Network");
            telemetry.addData("Status", (mClassify) ? "Testing network" : "Waiting");
            if (mCnn.getLoadedModelName() != null) {
                float[] results = mCnn.classify(rgba);
                for (int i = 0; i < results.length; i++) {
                    telemetry.addData("Confidence for category " + (i + 1), results[i]);
                }
            }
        }
        telemetry.update();
        return rgba;
    }
}
