package org.firstinspires.ftc.teamcode.fp_code.opsmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.fp_code.subsystems.Driver;

@Autonomous(name = "F - Auto Path", group = "Test")
public class AutoPathOp extends LinearOpMode {
    
    // Constants for dead reckoning (ticks per inch)
    private static final double TICKS_PER_INCH = 50.0; // YOU WILL NEED TO TUNE THIS

    private Driver driver;

    @Override
    public void runOpMode() throws InterruptedException {
        driver = new Driver(hardwareMap);

        telemetry.addLine("Ready: Press Start to run simple path");
        telemetry.update();

        waitForStart();

        // Path Example:
        driveForward(24); // Move 24 inches forward
        turnRight(90);    // Turn 90 degrees
        driveForward(12); // Move 12 inches forward

        telemetry.addLine("Path Complete");
        telemetry.update();
    }

    private void driveForward(double inches) {
        double startPos = driver.getAverageEncoder();
        double target = inches * TICKS_PER_INCH;
        
        while (opModeIsActive() && Math.abs(driver.getAverageEncoder() - startPos) < target) {
            driver.drive(0.4, 0, 0); // Slow forward speed
            updateTelemetry();
        }
        driver.stop();
    }

    private void turnRight(double degrees) {
        // Basic turn logic using time (until you have IMU/Gyro)
        double turnTime = degrees / 90.0 * 0.8; // Tune this multiplier
        long start = System.currentTimeMillis();
        while (opModeIsActive() && (System.currentTimeMillis() - start) < (turnTime * 1000)) {
            driver.drive(0, 0, 0.4);
            updateTelemetry();
        }
        driver.stop();
    }

    private void updateTelemetry() {
        telemetry.addData("X (Inches)", driver.getAverageEncoder() / TICKS_PER_INCH);
        telemetry.addData("Status", "Running Path...");
        telemetry.update();
    }
}