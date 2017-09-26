/*
Copyright (c) 2017 Don Bosco Technical Institute Robotics, FTC Team 5197

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted (subject to the limitations in the disclaimer
 below) provided that the following conditions are met:

 Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 Neither the name of Don Bosco Technical Institute nor the names of its
 contributors may be used to endorse or promote products derived from this
 software without specific prior written permission.

 NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
 ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation
  .VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.lasarobotics.vision.opmode.LinearVisionOpMode;

import java.util.ArrayList;
import java.util.List;

//import com.qualcomm.robotcore.util.TouchSensor; // ** Fixit: Not resolved

/**
 * This is NOT an opmode.
 * <p>
 * This class defines all the specific hardware for a Rangerbot, a simplified
 * version of the Pitsco TETRIX Ranger bot. It is designed to test robot
 * vision by running around and looking at things.
 * <p>
 * Equipment on board:
 *   Two TETRIX motors, unencoded.
 *   Android ZTE phone and phone holder. The phone's camera is the vision
 *   sensor. It can run OpenCV and Vuforia software.
 * <p>
 * Version history
 * v 0.1 jmr 8/16/17 initial Rangerbot class, based on last year's
 * HardwareRangerbot class.
 * v 0.2 jmr 8/17/17 imported some Vuforia code from Sliderbot.
 * v 0.21 jmr 9/5/17 motors brake to dead stop on zero power; they don't coast.
 * Poor: delays too long.
 *
 * @see VuforiaLocalizer
 * @see VuforiaTrackableDefaultListener
 * see  ftc_app/doc/tutorial/FTC_FieldCoordinateSystemDefinition.pdf
 * <p>
 * IMPORTANT: In order to use this class, you need to get your own Vuforia
 * license key as explained below.
 */

public class Rangerbot {
  // OpMode members.
  public static final double CAMERA_FROM_FRONT_MM = 220.0; // About 11 inches.
  public static final double SLOW_SPEED = 0.4;
  public static final double FAST_SPEED = 1.0;
  public DcMotor leftMotor = null; // Front wheel drive
  public DcMotor rightMotor = null;
  private LinearOpMode currentOpMode;
  private LinearVisionOpMode currentVisionMode;
  public OpenGLMatrix lastLocation = null;
  public double knownX;
  public double knownY;
  public double knownHeading;
  public boolean seeking = false;
  public List<VuforiaTrackable> allTrackables = null;
  public VuforiaTrackables beacons = null;
  public boolean approaching = false;

  /* local robot members. */ HardwareMap hwMap = null;

  /* Constructors */
  public Rangerbot() {
  }

  ;

  public Rangerbot(LinearOpMode linearOpMode) {
    currentOpMode = linearOpMode;
  }

  /*************************************************************************
   * Robot initialization methods.
   *************************************************************************/
    /* Register installed equipment according to the configuration file.     */
  public void initFromConfiguration(HardwareMap ahwMap) {
    // Save reference to Hardware map
    hwMap = ahwMap;

    // Define Motors
    leftMotor = hwMap.dcMotor.get("leftMotor");
    rightMotor = hwMap.dcMotor.get("rightMotor");
  }

  public void stopDriveMotors() {
    // Set all motors to zero power
    leftMotor.setPower(0);
    rightMotor.setPower(0);
  }

  /* Initialize standard drive train equipment. */
  public void initDrive() {
    // Left motor is connected to REV Robotics hub motor port 0.
    leftMotor.setDirection(DcMotor.Direction.REVERSE); // AndyMark motor:
      // REVERSE
    rightMotor.setDirection(DcMotor.Direction.FORWARD);// AndyMark motor:
      // FORWARD
    stopDriveMotors();
    ;

    // Set all motors to run without encoders.
    leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    rightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    // Set all motors to stop dead, not coast, when power removed.
    //leftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    //rightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
  }

  /****************************************************************************
   * Motor movement methods.
   ***************************************************************************/
    /* Most primitive: tells motors to run at given speeds, for given
    distances.   */
  public void justDrive(double leftSpeed, double rightSpeed) {
    // Go!
    leftMotor.setPower(Math.abs(leftSpeed));
    rightMotor.setPower(Math.abs(rightSpeed));

    // keep looping while we are still active, and any motors are running.
    while (currentOpMode.opModeIsActive() && (leftMotor.isBusy())) {
    }

    stopDriveMotors();

    // Turn off RUN_TO_POSITION
    leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    rightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
  }

  /****************************************************************************
   * Robot vision members.
   ****************************************************************************/
  public VuforiaLocalizer.Parameters parameters;

  /* Start up Vuforia. */
  public VuforiaLocalizer initVuforia() {
    VuforiaLocalizer vuforia;
    parameters = new VuforiaLocalizer.Parameters(com.qualcomm
      .ftcrobotcontroller.R.id.cameraMonitorViewId);
    parameters.vuforiaLicenseKey =
      "ASkv3nr/////AAAAGYZ9CexhH0K0lDbV090F719DkwXCIXEUmExgnQNDFGjrDrk" +
      "VJnU7xNhuKHLsC32Pb1jmr+6vp6JtpVKvNmTf28ZYkUphDeajNPCLgGVxLjD6xs" +
      "fgBayqSO9bfQFeGkrdEgXlP+2oaz234afhWti9Jn8k71mzbQ4W2koX9yBMWz0YL" +
      "zUWClcasxi6Nty7SUvV+gaq3CzpKVtjKk+2EwV6ibIc0V47LAeB0lDGsGkSzuJ+" +
      "93/Ulpoj+Lwr/jbI2mu/Bs2W7U9mw73CMxvDix9o1FxyPNablla4W5C5lUDm0j2" +
      "lW5gsUNOhgvlWKQ+eCu9IBp53WbW5nfNzhXPaDDh/IlBbZuAMIJuMDEHI5PVLKT9L";
    parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
    vuforia = ClassFactory.createVuforiaLocalizer(parameters);
    //currentVisionMode.telemetry.addData("", "Vuforia initialized.");
    return vuforia;
  }

  public List<VuforiaTrackable> initBeaconImages
    (VuforiaLocalizer someLocalizer) {
    /**
     * Load the data sets for the four beacon images we wish to track. These
     * particular data sets are stored in the 'assets' part of our application
     * (you'll see them in the Android Studio 'Project files' view over there
     * on the left of the screen, under FtcRobotController /src/main).
     * PDFs for the example "FTC_2016-17", datasets can be found in at
     * http://www.firstinspires.org/resource-library/ftc/game-and-season-info.
     */
    VuforiaTrackables beacons =
      someLocalizer.loadTrackablesFromAsset ("FTC_2016-17");
    //currentVisionMode.telemetry.addData("", "Allocated beacons.");
    beacons.get(0).setName("Wheels");
    beacons.get(1).setName("Tools");
    beacons.get(2).setName("Legos");
    beacons.get(3).setName("Gears");
    //currentVisionMode.telemetry.addData("", "Named beacons.");

    /* Assign locations to those images.
     * Just plaster all the images onto the middle of the Field, facing the
     * Red Alliance Wall (X=0, Y=0). Later, we'll put them onto absolute
     * positions as does the  sample code ConceptVuforiaNavigation.
     *
     * To place an image Target at the origin, facing the Red Audience wall:
     * We only need rotate it 90 around the field's X axis to flip it
     * upright, and move it up to the level of the phone. We make one matrix
     * to do that, and  apply it to all the beacon images.
     */

    OpenGLMatrix imageTargetLocationOnField = OpenGLMatrix.translation(0, 0,
      (float) 146.0) // Image height off floor
      .multiplied(Orientation.getRotationMatrix(AxesReference.EXTRINSIC,
        AxesOrder.XYZ, AngleUnit.DEGREES, 90, 0, 0));
    beacons.get(0).setLocation(imageTargetLocationOnField);
    beacons.get(1).setLocation(imageTargetLocationOnField);
    beacons.get(2).setLocation(imageTargetLocationOnField);
    beacons.get(3).setLocation(imageTargetLocationOnField);
    //currentVisionMode.telemetry.addData("", "Located beacons.");

    /**
     * Create a transformation matrix describing where the phone is on the
     * robot. Here, we put the phone on the front middle of the robot with
     * the screen facing in (see our choice of BACK camera above) and in
     * portrait mode. This requires no rotation of axes.
     */
    OpenGLMatrix phoneLocationOnRobot = OpenGLMatrix.translation(0.0f, 0.0f,
      0.0f); // Just locate the phone for now

    /**
     * Let the trackable listeners we care about know where the phone is. We
     * know that each listener is a {@link VuforiaTrackableDefaultListener}
     * and can so safely cast because we have not ourselves installed a
     * listener of a different type.
     */
    ((VuforiaTrackableDefaultListener) beacons.get(0).getListener())
      .setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);
    ((VuforiaTrackableDefaultListener) beacons.get(1).getListener())
      .setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);
    ((VuforiaTrackableDefaultListener) beacons.get(2).getListener())
      .setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);
    ((VuforiaTrackableDefaultListener) beacons.get(3).getListener())
      .setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);

    /** Start tracking the beacon image data sets. Typically, we'll see none
     * or one. */
    beacons.activate();
    /* For convenience, gather together all the trackable objects in one
    easily-iterable collection. */
    List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
    allTrackables.addAll(beacons);
    return allTrackables;
  }

  public String findBeaconImage(List<VuforiaTrackable> someImageSet) {
    String imageName = "nothing";
    for (VuforiaTrackable trackable : someImageSet) {
      /**
       * getUpdatedRobotLocation() will return null if no new information is
       * available since the last time that call was made, or if the
       * trackable is not currently visible.
       * getRobotLocation() will return null if the trackable is not
       * currently visible.
       */
      if (((VuforiaTrackableDefaultListener) trackable.getListener())
        .isVisible()) {
        imageName = trackable.getName();
        OpenGLMatrix robotLocationTransform = (
          (VuforiaTrackableDefaultListener) trackable.getListener())
          .getUpdatedRobotLocation();
        if (robotLocationTransform != null) {
          lastLocation = robotLocationTransform;
          break; // Never interested in more than one image.
        }
      }
    }
    return imageName;
  }

  /***************************************************************************
   *          Robot navigation members.
   ***************************************************************************/

  //  Set robot heading and coordinates from some location object.
  //  Fixit: convert to Meet 3 build.
  public void setXYHeading(OpenGLMatrix aLocation) {
    VectorF translation = aLocation.getTranslation();
    knownX = -translation.get(0);
    knownY = -translation.get(1);
    Orientation orientation = Orientation.getOrientation(lastLocation,
      AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.RADIANS);
    knownHeading = orientation.thirdAngle;
  }
}
