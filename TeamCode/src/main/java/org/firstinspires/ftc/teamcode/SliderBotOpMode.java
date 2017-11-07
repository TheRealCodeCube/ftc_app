package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

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

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized, waiting for match to start.");
        telemetry.update();

        //Load all the motors.
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

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

            telemetry.addData("Status", "Running (" + elapsedTime.toString() + ")");
            telemetry.addData("Wheel X", "%.2f", wheelX);
            telemetry.addData("Wheel Y", "%.2f", wheelY);
            telemetry.update();
        }
    }
}
