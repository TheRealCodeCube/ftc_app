package org.firstinspires.ftc.teamcode;

/**
 * Created by josh on 11/7/17.
 */

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

/**
 * This class specifies the hardware used in a RunnerBot. It expects the following:
 * DcMotor named left
 * DcMotor named right
 */
public class RunnerBot {
    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;

    public void loadHardware(HardwareMap hardwareMap) {
        leftDrive  = hardwareMap.get(DcMotor.class, "left");
        rightDrive = hardwareMap.get(DcMotor.class, "right");

        //Since one of the sets of motors will be facing the opposite direction.
        leftDrive.setDirection(DcMotor.Direction.FORWARD);
        rightDrive.setDirection(DcMotor.Direction.REVERSE);
    }

    public void drive(double speed, double turn) {
        leftDrive.setPower(Range.clip(speed + turn, -1.0, 1.0));
        rightDrive.setPower(Range.clip(speed - turn, -1.0, 1.0));
    }
}
