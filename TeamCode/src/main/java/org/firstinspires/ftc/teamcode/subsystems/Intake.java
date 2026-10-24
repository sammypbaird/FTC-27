package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/*
 * This class is in charge of the intake: the roller that pulls POLLEN and
 * NECTAR into the robot. Other files ask the Intake to run instead of
 * touching the intake motor themselves.
 */
public class Intake {

    // How fast the intake spins, from 0 (stopped) to 1 (full power).
    private static final double INTAKE_POWER = 1.0;

    private final DcMotorEx motor;

    // What the intake is doing right now, so we can show it on telemetry
    private String state = "stopped";

    public Intake(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotorEx.class, "intake");

        // REVERSE because in() was pushing pieces OUT (checked on the robot).
        // If in() pushes pieces out again, change REVERSE back to FORWARD.
        motor.setDirection(DcMotorSimple.Direction.REVERSE);

        // Stop right away when we let go, so pieces don't keep rolling in.
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /** Pull pieces into the robot. */
    public void in() {
        motor.setPower(INTAKE_POWER);
        state = "in";
    }

    /** Push pieces back out, for when one gets stuck. */
    public void out() {
        motor.setPower(-INTAKE_POWER);
        state = "out";
    }

    /** Stop the intake. */
    public void stop() {
        motor.setPower(0);
        state = "stopped";
    }

    /** Show what the intake is doing on the Driver Station screen. */
    public void addTelemetry(Telemetry telemetry) {
        telemetry.addData("Intake", state);
    }
}
