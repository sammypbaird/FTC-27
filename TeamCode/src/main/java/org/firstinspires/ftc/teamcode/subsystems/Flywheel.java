package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/*
 * This class is in charge of the flywheel: the heavy wheel that spins fast
 * to LAUNCH pieces into the HIVE.
 */
public class Flywheel {

    // Flywheel power, from 0 (stopped) to 1 (full power). The operator can
    // change it during the match with increasePower() and decreasePower().
    private static final double STARTING_POWER = 1.0;
    private static final double POWER_STEP = 0.1;   // 10% per button press
    private static final double MIN_POWER = 0.1;    // never below 10%, or spin() would do nothing
    private static final double MAX_POWER = 1.0;    // motors can't go above 100%

    private final DcMotorEx motor;

    // The power spin() uses right now
    private double power = STARTING_POWER;

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

    /** Spin the flywheel up to launch, at the current power. */
    public void spin() {
        motor.setPower(power);
        state = "spinning";
    }

    /** Spin 10% faster next time (up to 100%). */
    public void increasePower() {
        setPower(power + POWER_STEP);
    }

    /** Spin 10% slower next time (down to 10%). */
    public void decreasePower() {
        setPower(power - POWER_STEP);
    }

    private void setPower(double newPower) {
        // Range.clip keeps the number between MIN_POWER and MAX_POWER.
        newPower = Range.clip(newPower, MIN_POWER, MAX_POWER);
        // Round to the nearest 10%. Computers store 0.1 slightly wrong, so
        // without this, 0.1 + 0.1 + 0.1 can turn into 0.30000000000000004.
        power = Math.round(newPower * 10) / 10.0;
    }

    /** Turn off the flywheel. It coasts to a stop. */
    public void stop() {
        motor.setPower(0);
        state = "stopped";
    }

    /** Show what the flywheel is doing on the Driver Station screen. */
    public void addTelemetry(Telemetry telemetry) {
        telemetry.addData("Flywheel", state);
        telemetry.addData("Flywheel power", "%.0f%%", power * 100);
        // How fast it's really turning. Needs the motor's encoder cable plugged in.
        telemetry.addData("Flywheel speed (ticks/sec)", "%.0f", motor.getVelocity());
    }
}
