package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by josh on 10/31/17.
 */

/*
 * Bot structure:
 *y       x
 * fl---fr
 *  |\ /|
 *  |/ \|
 * bl---br
 *-x     -y
 */

@TeleOp(name="Slider Bot", group="Linear Opmode")
public class SliderBotOpMode extends LinearOpMode {
    private ElapsedTime elapsedTime = new ElapsedTime();
    private DcMotor frontLeft, frontRight, backLeft, backRight;

    private double wrist = 1.0, claw = 0.5;
    private Servo wristMotor, clawMotor;
    private DcMotor armMotor1, armMotor2;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized, waiting for match to start.");
        telemetry.update();

        //Load all the motors.
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        //Stuff for the arm.
        armMotor1 = hardwareMap.get(DcMotor.class, "arm1");
        armMotor2 = hardwareMap.get(DcMotor.class, "arm2");
        wristMotor = hardwareMap.get(Servo.class, "wrist");
        clawMotor = hardwareMap.get(Servo.class, "claw");

        //Wait for the match to start.
        waitForStart();
        elapsedTime.reset();

        while(opModeIsActive()) {
            double x = gamepad1.right_stick_x, y = -gamepad1.left_stick_y;
            double turn = gamepad1.right_trigger - gamepad1.left_trigger;
            double magnitude = Math.sqrt(x * x + y * y); //a^2+b^2=c^2
            double angle = Math.PI / 2;
            if(x != 0.0) { //To prevent DIV0 errors.
                angle = Math.atan(y / x); //tan(theta) = opposite (y) / adjacent (x)
            }
            if((y < 0.0) || (y == 0.0 && x < 0.0)) {
                angle += Math.PI; //Because 1 / 1 and -1 / -1 give the same angle.
            }
            angle -= Math.PI / 4; //Refer to diagram at top of source. The wheels' coordinate system
                             //is 45 degrees off from the robot's, making this correction necessary.
            double wheelX = Math.cos(angle), wheelY = Math.sin(angle);
            //'Normalization'. This gives a competitive advantage. For example, when going at 45
            //degrees, plugging that into sin and cos gives sqrt(2) for both axes. But the robot
            //can go faster by applying max power to both axes. This still preserves the direction,
            //because the ratio of the speeds of each axis is still the same. This code does a
            //similar process, but works for all directions.
            double wheelMax = Math.max(Math.abs(wheelX), Math.abs(wheelY));
            wheelX /= wheelMax;
            wheelY /= wheelMax;
            //And then scale those normalized values based on how fast the driver wants it to go.
            wheelX *= magnitude;
            wheelY *= magnitude;
            //Refer to diagram for explanation of this.
            frontRight.setPower(wheelX - turn);
            backLeft.setPower(wheelX + turn);
            frontLeft.setPower(wheelY + turn);
            backRight.setPower(wheelY - turn);

            //Copied from ArmTestOpMode, except gamepad1 was changed to gamepad2.
            double power = 0.0;
            double encoder = armMotor1.getCurrentPosition();

            claw -= gamepad2.right_stick_y * 0.01; // Stick Y is backwards.
            claw = Math.max(Math.min(claw, 1.0), 0.0); // Restrict to 0.0 - 1.0
            clawMotor.setPosition(claw);
            wrist -= gamepad2.left_stick_y * 0.002; // Stick Y is backwards.
            wrist = Math.max(Math.min(wrist, 1.0), 0.6); // Restrict to 0.6 - 1.0
            wristMotor.setPosition(wrist);

            if(gamepad2.y) {
                // TODO: Make these parameters into constants.
                if(encoder < -800) { // Need to apply negative power
                    // Encoder = -800, output = 0. Encoder = -1200, output = -0.35
                    power = Math.min(-(encoder + 600.0) / 400.0, 1.0) * -0.35;
                } else { //Need to apply positive power
                    // Encoder = -800, output = 0. Encoder = -400, output = 0.35
                    power = Math.min((encoder + 600.0) / 200.0, 1.0) * 0.35;
                }
            } else if (gamepad2.x) {
                if(encoder < -400.0)
                    power = -0.35;
            } else if (gamepad2.b) {
                if(encoder > -800.0)
                    power = 0.35;
            }
            armMotor1.setPower(-power); // It is backwards compared to the other motor.
            armMotor2.setPower(power);

            telemetry.addData("Status", "Running (" + elapsedTime.toString() + ")");
            telemetry.addData("Wheel X", "%.2f", wheelX);
            telemetry.addData("Wheel Y", "%.2f", wheelY);
            telemetry.update();
        }
    }
}
