package org.firstinspires.ftc.teamcode.finn.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Driver {

    private final DcMotorEx frontLeft;
    private final DcMotorEx backLeft;
    private final DcMotorEx frontRight;
    private final DcMotorEx backRight;
    private static final double SLOW_MODE_SCALE = 0.5;
    private double maxPower;
    private boolean slowModeEnabled;
    public Driver(HardwareMap hardwareMap){
        frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRight = hardwareMap.get(DcMotorEx.class, "backRight");

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void teleopDrive(Gamepad gamepad){
        double axial = -gamepad.left_stick_y; //-1 to 1
        double lateral = gamepad.left_stick_x; //-1 to 1
        double yaw = gamepad.right_stick_x; //-1 to 1
        if(gamepad.xWasPressed(){
            slowModeEnabled = !slowModeEnabled;
        }
        drive(axial, lateral, yaw);
    }

    private void drive(double axial, double lateral, double yaw){
        double frontLeftPower = axial + lateral + yaw;
        double frontRightPower = axial - lateral - yaw;
        double backLeftPower = axial - lateral + yaw;
        double backRightPower = axial + lateral - yaw;

        double max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(backRightPower));
        maxPower = max;

        if (max > 1) {
            frontLeftPower /= max;
            frontRightPower /= max;
            backLeftPower /= max;
            backRightPower /= max;
        }
        if(slowModeEnabled){
            frontLeftPower *= SLOW_MODE_SCALE;
            frontRightPower *= SLOW_MODE_SCALE;
            backLeftPower *= SLOW_MODE_SCALE;
            backRightPower *= SLOW_MODE_SCALE;

        }
        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);

    }
    public void addTelemetry(Telemetry telemetry){
        telemetry.addData("Max Power", "%.2f", maxPower);
    }
    public void stop(){
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }
    public void setSlowMode(boolean enabled){
        this.enabled = enabled;
    }
}
