package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/*
 * This class is in charge of the flywheel: the heavy wheel that spins fast
 * to LAUNCH pieces into the HIVE.
 */
public class Flywheel {

    // How fast the flywheel spins, from 0 (stopped) to 1 (full power).
    private static final double FLYWHEEL_POWER = 1.0;

    private final DcMotorEx motor;

    // What the flywheel is doing right now, so we can show it on telemetry
    private String state = "stopped";

    public Flywheel(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotorEx.class, "flywheel");

        // If pieces come out the wrong way, change FORWARD to REVERSE.
        motor.setDirection(DcMotorSimple.Direction.FORWARD);

        // FLOAT lets the flywheel coast to a stop. A heavy wheel that stops
        // instantly puts a lot of strain on the motor and its gears.
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    /** Spin the flywheel up to launch. */
    public void spin() {
        motor.setPower(FLYWHEEL_POWER);
        state = "spinning";
    }

    /** Turn off the flywheel. It coasts to a stop. */
    public void stop() {
        motor.setPower(0);
        state = "stopped";
    }

    /** Show what the flywheel is doing on the Driver Station screen. */
    public void addTelemetry(Telemetry telemetry) {
        telemetry.addData("Flywheel", state);
        // How fast it's really turning. Needs the motor's encoder cable plugged in.
        telemetry.addData("Flywheel speed (ticks/sec)", "%.0f", motor.getVelocity());
    }
}
