package org.firstinspires.ftc.teamcode.fp_code.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Driver {
    private final DcMotor frontLeft;
    private final DcMotor backLeft;
    private final DcMotor frontRight;
    private final DcMotor backRight;

    public Driver(HardwareMap hardwareMap) {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        // If the entire robot moves backward when given positive power,
        // swap FORWARD and REVERSE here:
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior) {
        frontLeft.setZeroPowerBehavior(behavior);
        backLeft.setZeroPowerBehavior(behavior);
        frontRight.setZeroPowerBehavior(behavior);
        backRight.setZeroPowerBehavior(behavior);
    }

    public void drive(double forward, double strafe, double turn) {
        double frontLeftPower = forward + strafe + turn;
        double backLeftPower = forward - strafe + turn;
        double frontRightPower = forward - strafe - turn;
        double backRightPower = forward + strafe - turn;

        double maxPower = Math.max(
                Math.abs(frontLeftPower),
                Math.max(Math.abs(backLeftPower), Math.max(Math.abs(frontRightPower), Math.abs(backRightPower)))
        );

        if (maxPower > 1.0) {
            frontLeftPower /= maxPower;
            backLeftPower /= maxPower;
            frontRightPower /= maxPower;
            backRightPower /= maxPower;
        }

        frontLeft.setPower(frontLeftPower);
        backLeft.setPower(backLeftPower);
        frontRight.setPower(frontRightPower);
        backRight.setPower(backRightPower);
    }

    public void teleopDrive(Gamepad gamepad) {
        // FTC Gamepad Y-axis is inverted by default (pushing UP gives -1.0).
        // Change signs below to match your preference:
        double forward = gamepad.left_stick_y; // Flip to -gamepad.left_stick_y if forward/back is backward
        double strafe  = gamepad.left_stick_x; // Flip to -gamepad.left_stick_x if left/right is inverted
        double turn    = gamepad.right_stick_x; // Flip to -gamepad.right_stick_x if turning is inverted

        drive(forward, strafe, turn);
    }

    public void stop() {
        drive(0, 0, 0);
    }

    public void addTelemetry(Telemetry telemetry) {
        telemetry.addData("Front Left", frontLeft.getCurrentPosition());
        telemetry.addData("Back Left", backLeft.getCurrentPosition());
        telemetry.addData("Front Right", frontRight.getCurrentPosition());
        telemetry.addData("Back Right", backRight.getCurrentPosition());
    }

    public double getAverageEncoder() {
        return (frontLeft.getCurrentPosition() + backLeft.getCurrentPosition() +
                frontRight.getCurrentPosition() + backRight.getCurrentPosition()) / 4.0;
    }
}