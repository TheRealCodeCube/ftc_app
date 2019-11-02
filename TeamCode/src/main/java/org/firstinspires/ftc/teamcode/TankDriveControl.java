package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Created by josh on 11/7/17.
 */

public class TankDriveControl {
    private long nudgeStart = 0; //Records the time a user first pressed a button to start a nudge.
    private static final long NUDGE_TIME = 200; //How many milliseconds a nudge should be run for.
    private Gamepad gamepad;
    
    public void setGamepad(Gamepad gamepad) {
        this.gamepad = gamepad;
    }
    
    public double[] getSpeedAndTurn(boolean useFineTuningControls) {
        double speed = -gamepad.left_stick_y;
        double turn  =  gamepad.right_stick_x;

        if(useFineTuningControls) {
            //The driver can press a dpad button to nudge the robot in a particular direction.
            boolean nudgePressed = gamepad.dpad_up || gamepad.dpad_down || gamepad.dpad_left ||
                    gamepad.dpad_right;
            if (nudgePressed) {
                speed = 0.0;
                turn = 0.0;
                //nudgeTimer is used to perform a nudge for a specified number of frames, even if
                //the user continues to hold the nudge button.
                if (nudgeStart == 0) {
                    nudgeStart = System.currentTimeMillis();
                }
                if (nudgeStart + NUDGE_TIME > System.currentTimeMillis()) {
                    if (gamepad.dpad_up) {
                        speed = 0.5;
                    } else if (gamepad.dpad_down) {
                        speed = -0.5;
                    } else if (gamepad.dpad_left) {
                        turn = -0.5;
                    } else if (gamepad.dpad_right) {
                        turn = 0.5;
                    }
                }
            } else {
                nudgeStart = 0;
            }
        }

        //Reduce speed speed if left trigger or bumper are pressed.
        if(gamepad.left_bumper || (gamepad.left_trigger > 0.5)) {
            speed *= 0.5;
        }
        //Reduce turn speed if right trigger or bumper are pressed.
        if(gamepad.right_bumper || (gamepad.right_trigger > 0.5)) {
            turn *= 0.5;
        }
        double[] tr = { speed, turn };
        return tr;
    }

    public double[] getSpeedAndTurn() {
        return getSpeedAndTurn(true);
    }
}
