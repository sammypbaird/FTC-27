package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/*
 * This class is in charge of the transfer: the wheels that carry pieces
 * from the intake up to the flywheel. run() feeds pieces into the flywheel
 * to launch them. out() runs backwards to push a piece back out through
 * the intake.
 */
public class Transfer {

    // How fast the transfer spins, from 0 (stopped) to 1 (full power).
    private static final double TRANSFER_POWER = 1.0;

    private final DcMotorEx motor;

    // What the transfer is doing right now, so we can show it on telemetry
    private String state = "stopped";

    public Transfer(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotorEx.class, "transfer");

        // REVERSE because pieces were moving AWAY from the flywheel (checked on the robot).
        // If they move away from the flywheel again, change REVERSE back to FORWARD.
        motor.setDirection(DcMotorSimple.Direction.REVERSE);

        // Stop right away, so a piece doesn't roll into the flywheel by itself.
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /** Carry pieces toward the flywheel. */
    public void run() {
        motor.setPower(TRANSFER_POWER);
        state = "running";
    }

    /** Run backwards, carrying pieces back toward the intake. */
    public void out() {
        motor.setPower(-TRANSFER_POWER);
        state = "out";
    }

    /** Stop the transfer. */
    public void stop() {
        motor.setPower(0);
        state = "stopped";
    }

    /** Show what the transfer is doing on the Driver Station screen. */
    public void addTelemetry(Telemetry telemetry) {
        telemetry.addData("Transfer", state);
    }
}
