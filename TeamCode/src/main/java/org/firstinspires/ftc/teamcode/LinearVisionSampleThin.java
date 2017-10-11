package org.firstinspires.ftc.teamcode;
/*
 +The MIT License (MIT)
 +
 +Copyright (c) 2015 LASA Robotics
 +Copyright (c) 2017 Don Bosco Tech FTC Team 5197
 +
 +This software was developed by Don Bosco Tech's FTC Team 5197, "the
 +GearHeads". It is derived from work by LASA Robotics on
 +
 +  https://github.com/lasarobotics/FTCVision
 +
 +Permission is hereby granted, free of charge, to any person obtaining a copy
 +of this software and associated documentation files (the "Software"), to deal
 +in the Software without restriction, including without limitation the rights
 +to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 +copies of the Software, and to permit persons to whom the Software is
 +furnished to do so, subject to the following conditions:
 +
 +The above copyright notice and this permission notice shall be included in all
 +copies or substantial portions of the Software.
 +
 +THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 +IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 +FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 +AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 +LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 +OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 +SOFTWARE.
 +
 */

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.lasarobotics.vision.android.Cameras;
import org.lasarobotics.vision.ftc.resq.Beacon;
import org.lasarobotics.vision.opmode.LinearVisionOpMode;
import org.opencv.core.Mat;
import org.opencv.core.Size;

/**
 * Linear Vision Sample
 * <p/>
 * Use this in a typical linear op mode. A LinearVisionOpMode allows using
 * Vision Extensions, which do a lot of processing for you. Here, we enable
 * only the BEACON extension and set its options to GearHead preference. That
 * extension was optimized for last year's Velocity Vortex game, and used to
 * detect the colors of a powered beacon. This year's game is Reclic
 * Recovery, and the unmodified BEACON extension does a decent job in ambient
 * light of determining the order of Jewels sitting on a Jewel Platform.
 * Surely, there is room for optimization.
 * <p/>
 * Please note that the LinearVisionOpMode is specially designed to target a
 * particular version of the FTC Robot Controller app. Changes to the app may
 * break the LinearVisionOpMode. Should this happen, open up an issue with
 * http://github.com/ftctechn. :)
 *
 * Version history
 * 10/8/17 v 0.1 jmr. Works unmodified as a TeleOpMode.
 * 10/9/17 v 0.11 jmr. Renamed to "Linear Vision Sample (Thin)", and works
 * with ROTATION and CAMERA_CONTROL disabled. Succeeding minor versions 0.1x
 * will try to thin down the code more.
 *
 */
@TeleOp(name = "Linear Vision Sample (Thin)", group = "OpenCV")
//@Disabled
public class LinearVisionSampleThin extends LinearVisionOpMode {

  @Override
  public void runOpMode() throws InterruptedException {
    //Wait for vision to initialize - this should be the first thing you do.
    waitForVisionStart();

    /**
     * Set the camera used for detection
     * PRIMARY = Front-facing, larger camera
     * SECONDARY = Screen-facing, "selfie" camera :D
     **/
    this.setCamera(Cameras.PRIMARY);

    /**
     * Set the frame size
     * Larger = sometimes more accurate, but also much slower
     * After this method runs, it will set the "width" and "height" of the frame
     **/
    this.setFrameSize(new Size(900, 900));

    /**
     * Enable extensions. Use what you need.
     */
    enableExtension(Extensions.BEACON);         //Beacon detection
    //Extensions ROTATION and CAMERA_CONTROL not enabled. Eventually, we want
    // a JEWEL extension.

    /**
     * Set the beacon analysis method
     * Try them all and see what works!
     */
    beacon.setAnalysisMethod(Beacon.AnalysisMethod.FAST);

    /**
     * Set color tolerances
     * 0 is default, -1 is minimum and 1 is maximum tolerance
     */
    beacon.setColorToleranceRed(0);
    beacon.setColorToleranceBlue(0);

    /**
     * Set analysis boundary
     * You should comment this to use the entire screen and uncomment only if
     * you want faster analysis at the cost of not using the entire frame.
     * This is also particularly useful if you know approximately where the
     * beacon is
     * as this will eliminate parts of the frame which may cause problems
     * This will not work on some methods, such as COMPLEX
     **/
    //beacon.setAnalysisBounds(new Rectangle(new Point(width / 2, height / 2)
    // , width - 200, 200));

    /**
     * Set the rotation parameters of the screen
     * If colors are being flipped or output appears consistently incorrect,
     * try changing these.
     *
     * First, tell the extension whether you are using a secondary camera
     * (or in some devices, a front-facing camera that reverses some colors).
     *
     * It's a good idea to disable global auto rotate in Android settings.
     * You can do this
     * by calling disableAutoRotate() or enableAutoRotate().
     *
     * It's also a good idea to force the phone into a specific orientation
     * (or auto rotate) by
     * calling either setActivityOrientationAutoRotate() or
     * setActivityOrientationFixed(). If
     * you don't, the camera reader may have problems reading the current
     * orientation.
     */
    //rotation.setIsUsingSecondaryCamera(false);
    //rotation.disableAutoRotate();
    //rotation.setActivityOrientationFixed(ScreenOrientation.PORTRAIT);

    /**
     * Set camera control extension preferences
     *
     * Enabling manual settings will improve analysis rate and may lead to
     * better results under
     * tested conditions. If the environment changes, expect to change these
     * values.
     */
    //cameraControl.setColorTemperature(CameraControlExtension.ColorTemperature
    //  .AUTO);
    //cameraControl.setAutoExposureCompensation();

    //Wait for the match to begin
    waitForStart();

    //Main loop
    //Camera frames and OpenCV analysis will be delivered to this method
    // as quickly as possible. This loop will exit once the opmode is closed.
    while (opModeIsActive()) {
      //Log a few things
      telemetry.addData("Beacon Color", beacon.getAnalysis().getColorString());
      telemetry.addData("Beacon Center", beacon.getAnalysis()
        .getLocationString());
      telemetry.addData("Beacon Confidence", beacon.getAnalysis()
        .getConfidenceString());


      //You can access the most recent frame data and modify it here using
      // getFrameRgba() or getFrameGray().
      //Vision will run asynchronously (parallel) to any user code so your
      // programs won't hang.
      //You can use hasNewFrame() to test whether vision processed a new frame.
      //Once you copy the frame, discard it immediately with discardFrame().
      if (hasNewFrame()) {
        //Get the frame
        Mat rgba = getFrameRgba();
        Mat gray = getFrameGray();

        //Discard the current frame to allow for the next one to render
        discardFrame();
      }
      //Wait for a hardware cycle to allow other processes to run.
      waitOneFullHardwareCycle();
    }
  }
}
