package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by josh on 11/2/17.
 */

@TeleOp(name="Test Arm", group="Test")
public class ArmTestOpMode extends LinearOpMode {
    private DcMotor armMotor;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized, waiting for match to start.");
        telemetry.update();

        //Load the motor.
        armMotor = hardwareMap.get(DcMotor.class, "frontLeft");

        //Wait for the match to start.
        waitForStart();
        //elapsedTime.reset();

        while(opModeIsActive()) {
            if(gamepad1.dpad_up && (armMotor.getCurrentPosition() < 100)) {
                armMotor.setPower(0.1);
            } else if(gamepad1.dpad_down && (armMotor.getCurrentPosition() > 0)) {
                armMotor.setPower(-0.1);
            } else {
                armMotor.setPower(0.0);
            }
            telemetry.addData("Status", "RUNNING");
            telemetry.addData("Encoder Position", armMotor.getCurrentPosition());
        }
    }
}
