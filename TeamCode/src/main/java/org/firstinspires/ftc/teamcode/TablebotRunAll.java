package org.firstinspires.ftc.teamcode;

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

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Locale;

/*
 *   This is an example LinearOpMode that shows how to use the Tablebot robot
  *   class. See that class in this project for the hardware systems on that
  *   robot. All will be available for TeleOp control.
 */

@TeleOp(name = "Tablebot All Systems", group = "Tablebot")
//@Disabled
public class TablebotRunAll extends LinearOpMode {
  public RRField field = new RRField(this);
  Tablebot robot = new Tablebot(this);
  private double motorPower = 0.0;
  private double servoPosition = 0.0;

  @Override
  public void runOpMode() {
    //  Initialize robot equipment
    robot.initFromConfiguration(hardwareMap);
    robot.initDrive();
    robot.initSensors();
    robot.initServos();

    // wait for the start button to be pressed.
    waitForStart();

    //   Use opModeIsActive() as our loop condition because it is an
    // interruptible method.
    while (opModeIsActive()) {
      //   Gamepad control of the single motor.
      motorPower = -gamepad1.left_stick_y; // forward stick is negative.
      telemetry.addData("Motor power", motorPower);
      robot.motor0.setPower(motorPower);

      //   Gamepad control of the single servo.
      servoPosition = gamepad1.right_stick_y;
      robot.HiTEC485HB.setPosition(servoPosition);

      //   Get and report on touch sensor state.
      //   True means sensor bit is HIGH and the button is unpressed.
      if (robot.REVTouch1.getState()) {
        telemetry.addData("Touch button", "not pressed");
      } else {
        telemetry.addData("Touch button", "pressed");
      }

      //   Get and report on the distance and color sensor state.
      // See sample SensorREVColorDistance for a more sophisticated use
      // of the information available from this sensor.
      telemetry.addData("Distance (cm)", String.format(Locale.US, "%.02f",
        robot.REVDistanceSensor.getDistance(DistanceUnit.CM)));
      telemetry.addData("Red", robot.REVColorSensor.red());
      telemetry.addData("Green", robot.REVColorSensor.green());
      telemetry.addData("Blue", robot.REVColorSensor.blue());
      telemetry.update();
    }
  }
}
