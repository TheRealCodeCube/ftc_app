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
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Locale;

import static android.os.SystemClock.sleep;

/**
 * This is NOT an opmode.
 * <p>
 * This class defines all the specific hardware for the REV Expansion hub and
 * basic hardware connected to it:
 *   o Battery
 *   o Tetrix motor (was 2 AndyMark NeverRest 40 encoded gearmotors in v 0.3)
 *   o REV Color Range sensor
 *   o REV Touch sensor
 *   o HiTEK HS-485HB servo
 * This robot is a Tablebot. It is just a bunch of electronics, cables, and
 * the stuff listed above wired up and sitting on a table top. It can sense,
 * run its two motors, no more.
 * <p>
 * This robot class assumes motor, servo and sensor names have been
 * configured in a robot configuration file. It is named "REVonTable", and
 * should be activated on the Controller phone before an attempt to use this
 * robot class.
 * <p>
 * Version history
 * v 0.1 jmr 8/5/17 does not run 2nd segment. This version moves much code
 *   from external sample PushbotAutoDriveByEncoder:runOpmode to the robot
 *   class Tablebot. Now, TablebotAutoDriveByEncoder:runOpmode does nothing
 *   more than initialize the drive for encoded running, wait for Start button,
 *   then call two segments of robot method encoderDrive.
 * v 0.2 jmr 8/9/17 corrected, using intersegment logic of external sample
 *   PushbotAutoDriveByEncoder_Linear.
 * v 0.3 jmr 8/10/17 gangs two motors, powered and encoded as one. This was
 *   done successfully on last year's Runnerbot.
 * v 0.4 jmr 8/10/17 adds servo support. Fixit: servo quietly does nothing.
 *   Logcat says PWM is not initialized.
 * v 0.5 jmr 8/20/17 simplified to one unencoded TETRIX motor. Still no servo
 *   or sensor support.
 * v 0.6 jmr 9/25/17 added support for REV Color Range sensor, REV touch
 *   sensor and HiTEC485HB servo. Code re-formatted some.
 */

public class Tablebot {
  // OpMode members.
  private static final double COUNTS_PER_MOTOR_REV = 1120.0; // use 1440 for
    // TETRIX Encoder

  //    public static final double CAMERA_FROM_FRONT = 254.0; // May be added
  // later.
  public static final double SLOW_SPEED = 0.4;
  public static final double FAST_SPEED = 1.0;

  public DcMotor motor0 = null;
  public Servo HiTEC485HB = null;
  public DigitalChannel REVTouch1 = null;
  public ColorSensor REVColorSensor = null;
  public DistanceSensor REVDistanceSensor = null;

  public static final double MID_SERVO = 0.5;
  static final double MAX_POS = 1.0;     // Maximum servo position
  static final double MIN_POS = 0.0;     // Minimum servo position
  public I2cDevice range = null;
  private LinearOpMode currentOpMode;

  /* local robot members. */ HardwareMap hwMap = null;

  /* Constructors */
  public Tablebot() {
  }

  ;

  public Tablebot(LinearOpMode linearOpMode) {
    currentOpMode = linearOpMode;
  }

  /***************************************************************************
   * Robot initialization methods.
   ***************************************************************************/

    /* Register installed equipment according to the configuration file. */
  public void initFromConfiguration(HardwareMap ahwMap) {
    // Save reference to Hardware map
    hwMap = ahwMap;

    // Define Motors
    motor0 = hwMap.dcMotor.get("motor0");

    // Define the installed servos...
    HiTEC485HB = hwMap.servo.get("HiTEC485HB");
    HiTEC485HB.setPosition(MID_SERVO);

    // ... and the installed sensors.
    REVTouch1 = hwMap.get(DigitalChannel.class, "REVTouch1");
    REVColorSensor = hwMap.get(ColorSensor.class, "REVColorRange");
    REVDistanceSensor = hwMap.get(DistanceSensor.class, "REVColorRange");
  }

  /* Initialize standard drive train equipment. */
  public void initDrive() {
    // Initialize motors
    motor0.setDirection(DcMotor.Direction.FORWARD); // Tetrix motor: REVERSE
    motor0.setPower(0);
  }

  /*   Initialize drive equipment to use encoders.   */
  public void initEncodedDrive() {
    //   Zero the encoder counts targets. Side effect is to remove
    // power, but we will do that explicitly.
    motor0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    motor0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
  }

  /*   Initialize drive equipment not to use encoders.   */
  public void initUnencodedDrive() {
    // Zero the encoder counts targets. Side effect is to remove
    //   power, but we will do that explicitly.
    motor0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    motor0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
  }

  /***************************************************************************
   * Motor movement methods.
   ***************************************************************************/
    /* Most primitive: tells motor to run at some speed through some
    revolutions. */
  public void encoderDrive(double speed0, double revs0) {
    int newTarget0;

    // Set new target positions
    newTarget0 = motor0.getCurrentPosition() + (int) (revs0 *
      COUNTS_PER_MOTOR_REV);
    motor0.setTargetPosition(newTarget0);
    motor0.setMode(DcMotor.RunMode.RUN_TO_POSITION);

    // Go!
    motor0.setPower(Math.abs(speed0));

    // keep looping while we are still active, and any motors are running.
    while (currentOpMode.opModeIsActive() && (motor0.isBusy())) {
    }
    // Stop all motion
    motor0.setPower(0);

    // Turn off RUN_TO_POSITION
    motor0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
  }

  public void unencodedDrive(double speed0, long time) {
    // Go for some time in milliseconds.
    motor0.setPower(Math.abs(speed0));
    sleep(time);
  }

  /***************************************************************************
   *  Servo methods.
   ***************************************************************************/
    /*  Set up servos   */
  public void initServos() {
    HiTEC485HB.setPosition(MIN_POS);
    HiTEC485HB.setPosition(MAX_POS);
  }

  /***************************************************************************
   *  Sensor methods.
   ***************************************************************************/
    /*  Set up sensors   */
  // set the digital channel to input.
  public void initSensors() {
    // initialize Color Range sensor here
    REVTouch1.setMode(DigitalChannel.Mode.INPUT);
  }
}