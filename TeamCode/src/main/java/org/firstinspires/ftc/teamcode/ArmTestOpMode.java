package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by josh on 11/2/17.
 */

@TeleOp(name="Test Arm", group="Test")
public class ArmTestOpMode extends LinearOpMode {
    private DcMotor armMotor1, armMotor2;
    private double claw = 0.5, wrist = 1.0;
    private Servo wristMotor, clawMotor;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized, waiting for match to start.");
        telemetry.update();

        //Load the motor.
        armMotor1 = hardwareMap.get(DcMotor.class, "arm1");
        armMotor2 = hardwareMap.get(DcMotor.class, "arm2");
        wristMotor = hardwareMap.get(Servo.class, "wrist");
        clawMotor = hardwareMap.get(Servo.class, "claw");


        //Wait for the match to start.
        waitForStart();
        //elapsedTime.reset();

        while(opModeIsActive()) {
            /*
            if(gamepad1.dpad_up && (armMotor.getCurrentPosition() < 100)) {
                armMotor.setPower(0.25);
            } else if(gamepad1.dpad_down && (armMotor.getCurrentPosition() > 0)) {
                armMotor.setPower(-0.25);
            } else {
                armMotor.setPower(0.0);
            }*/

            double power = 0.0;
            double encoder = armMotor1.getCurrentPosition();

            claw -= gamepad1.right_stick_y * 0.01; // Stick Y is backwards.
            claw = Math.max(Math.min(claw, 1.0), 0.0); // Restrict to 0.0 - 1.0
            clawMotor.setPosition(claw);
            wrist -= gamepad1.left_stick_y * 0.002; // Stick Y is backwards.
            wrist = Math.max(Math.min(wrist, 1.0), 0.6); // Restrict to 0.6 - 1.0
            wristMotor.setPosition(wrist);

            if(gamepad1.y) {
                // TODO: Make these parameters into constants.
                if(encoder < -800) { // Need to apply negative power
                    // Encoder = -800, output = 0. Encoder = -1200, output = -0.35
                    power = Math.min(-(encoder + 600.0) / 400.0, 1.0) * -0.35;
                } else { //Need to apply positive power
                    // Encoder = -800, output = 0. Encoder = -400, output = 0.35
                    power = Math.min((encoder + 600.0) / 200.0, 1.0) * 0.35;
                }
            } else if (gamepad1.x) {
                if(encoder < -400.0)
                    power = -0.35;
            } else if (gamepad1.b) {
                if(encoder > -800.0)
                   power = 0.35;
            }
            armMotor1.setPower(-power); // It is backwards compared to the other motor.
            armMotor2.setPower(power);

            telemetry.addData("Status", "RUNNING");
            telemetry.addData("Encoder Position", armMotor1.getCurrentPosition());
            telemetry.addData("Claw", claw);
            telemetry.addData("Wrist", wrist);
            telemetry.update();
        }
    }
}
