package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/*
 * This class is in charge of the 4 wheel motors and the IMU (the sensor
 * that knows which way the robot is facing). Every other file should ask
 * the Driver to move the robot instead of touching the wheel motors itself.
 * That way, all the driving code lives in ONE place.
 *
 * This is a MECANUM drive. Mecanum wheels let the robot slide sideways
 * (strafe) as well as go forward, backward, and turn.
 */
public class Driver {

    private final DcMotorEx frontLeft;
    private final DcMotorEx frontRight;
    private final DcMotorEx backLeft;
    private final DcMotorEx backRight;
    private static final double SLOW_MODE_SCALE = 0.5;

    // We store the maxPower so that we can show it on telemetry
    private double maxPower;

    // Slow mode is a toggle that slows down all drive controls
    private boolean slowModeEnabled;

    public Driver(HardwareMap hardwareMap) {
        // Find each motor by its configuration name
        frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRight = hardwareMap.get(DcMotorEx.class, "backRight");

        // Reverse the right wheels so they all go forward with a positive power
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    /**
     * Called once every time through the main opsmode loop. It reads the
     * joysticks and buttons, then drives the robot.
     */
    public void teleopDrive(Gamepad gamepad) {
        double axial  = -gamepad.left_stick_y;  //-1 to 1
        double lateral = gamepad.left_stick_x;
        double yaw = gamepad.right_stick_x;

        if (gamepad.xWasPressed()) {
            slowModeEnabled = !slowModeEnabled;
        }

        drive(axial, lateral, yaw);
    }

    /**
     * Drive the robot relative to ITSELF ("robot-centric"): forward means
     * the direction the front of the robot is facing.
     * All three numbers go from -1 to +1.
     *
     * @param axial   forward / backward.  +1 = full speed forward
     * @param lateral left / right.        +1 = slide to the right
     * @param yaw     turning.             +1 = spin clockwise
     */
    public void drive(double axial, double lateral, double yaw) {
        double frontLeftPower = axial + lateral + yaw;
        double frontRightPower = axial - lateral - yaw;
        double backLeftPower = axial - lateral + yaw;
        double backRightPower = axial + lateral - yaw;

        // A motor can't go above 1.0, but adding three numbers can give 2.0 or more.
        // Find the biggest power (ignoring plus/minus)...
        double max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(backRightPower));
        maxPower = max;

        // Divide each power by the max, to normalize it between -1 and 1
        if (max > 1) {
            frontLeftPower /= max;
            frontRightPower /= max;
            backLeftPower /= max;
            backRightPower /= max;
        }

        // if slow mode is enabled, multiply it be the slow mode scalar
        if (slowModeEnabled) {
            frontLeftPower *= SLOW_MODE_SCALE;
            frontRightPower *= SLOW_MODE_SCALE;
            backLeftPower *= SLOW_MODE_SCALE;
            backRightPower *= SLOW_MODE_SCALE;
        }

        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower((backLeftPower));
        backRight.setPower(backRightPower);
    }

    /** Show the driving info on the Driver Station screen. */
    public void addTelemetry(Telemetry telemetry) {
        telemetry.addData("Max Power", "%.2f", maxPower);
    }

    /** Stop all four wheels. Called when the OpMode ends. */
    public void stop() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower((0));
        backRight.setPower(0);
    }
}
