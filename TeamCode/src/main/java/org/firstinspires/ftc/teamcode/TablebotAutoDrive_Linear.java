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

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * This file illustrates the concept of driving a path using the Tablebot
 * hardware class to define the drive on the robot. The motors run unencoded.
 * The code is structured as a LinearOpMode.
 * <p>
 * The code is written using methods defined in the robot hardware class
 * Tablebot.
 * The desired path can be built up by calls to method robot.unencodedDrive.
 */

@Autonomous(name = "Tablebot: Auto Drive", group = "Tablebot")
//@Disabled
public class TablebotAutoDrive_Linear extends LinearOpMode {

  /* Declare OpMode members. */
  Tablebot robot = new Tablebot(this);   // Use a Tablebot's hardware

  @Override
  public void runOpMode() {
    robot.initFromConfiguration(hardwareMap);
    // Initialize the drive system.
    robot.initDrive();
    robot.initUnencodedDrive();

    // Initialize servos and sensors
    robot.initServos();
    robot.initSensors();

    // Announce
    telemetry.addData("Path0", "Starting at %7d", robot
      .motor0.getCurrentPosition());
    telemetry.update();

    // Wait for driver to press PLAY
    waitForStart();

    // Step through each leg of the path.
    // Note: Reverse movement is obtained by setting negative revolutions
      // (not speed)
    // Forward for one second
    robot.unencodedDrive(robot.FAST_SPEED, 1000);
    // Forward for another second, slower
    robot.unencodedDrive(robot.SLOW_SPEED, 1000);
  }
}
