package org.firstinspires.ftc.teamcode.fp_code.opsmodes.auto;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.fp_code.subsystems.Driver;

@Autonomous(name = "F - GoToPollen", group = "Test")
public class GoToPollenOp extends LinearOpMode {
    private static final double MIN_CONFIDENCE = 0.5;

    // Negated gains to correct inverted direction towards detected target
    private static final double TURN_GAIN = -0.010;
    private static final double STRAFE_GAIN = -0.015;
    private static final double FORWARD_GAIN = 0.04;

    private static final double MAX_TURN = 0.12;
    private static final double MAX_FORWARD = 0.20;
    private static final double DESIRED_AREA = 15.0;
    private static final double INTAKE_DRIVE_INCHES = 5.0;
    private static final double TICKS_PER_INCH = 50.0;

    // Controlled search turn speed
    private static final double FAST_SEARCH_TURN_SPEED = 0.12;
    private static final int REQUIRED_FRAMES = 3;

    private boolean goToPollenActive = false;
    private boolean wasSeeingPollen = false;
    private Servo indicator;
    private int consecutiveFrames = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        Driver driver = new Driver(hardwareMap);

        // Ensure motor brake mode is active
        driver.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");
        indicator = hardwareMap.get(Servo.class, "indicator");

        limelight.pipelineSwitch(1);
        limelight.start();

        waitForStart();

        while (opModeIsActive()) {
            limelight.pipelineSwitch(1);

            if (gamepad1.aWasPressed()) {
                goToPollenActive = !goToPollenActive;
                wasSeeingPollen = false;
            }

            LLResult result = limelight.getLatestResult();
            LLResultTypes.DetectorResult pollen = findClosestPollen(result);

            if (pollen != null) {
                consecutiveFrames = Math.min(consecutiveFrames + 1, REQUIRED_FRAMES + 1);
            } else {
                consecutiveFrames = 0;
            }

            if (goToPollenActive) {
                if (consecutiveFrames >= REQUIRED_FRAMES && pollen != null) {
                    indicator.setPosition(0.4);
                    wasSeeingPollen = true;

                    double tx = pollen.getTargetXDegrees();
                    double ta = pollen.getTargetArea();

                    // Deadband check: skip turn power if target is within 1.5 degrees of center
                    double yaw = 0;
                    if (Math.abs(tx) > 1.5) {
                        yaw = Range.clip(tx * TURN_GAIN, -MAX_TURN, MAX_TURN);
                    }

                    double strafe = Range.clip(tx * STRAFE_GAIN, -0.2, 0.2);
                    double forwardError = DESIRED_AREA - ta;
                    double forward = Range.clip(forwardError * FORWARD_GAIN, -MAX_FORWARD, MAX_FORWARD);

                    driver.drive(forward, strafe, yaw);
                } else {
                    if (wasSeeingPollen) {
                        indicator.setPosition(0.4);
                        double startPos = driver.getAverageEncoder();
                        double targetTicks = INTAKE_DRIVE_INCHES * TICKS_PER_INCH;

                        while (opModeIsActive() && Math.abs(driver.getAverageEncoder() - startPos) < targetTicks) {
                            if (findClosestPollen(limelight.getLatestResult()) != null) break;
                            driver.drive(0.15, 0, 0);
                        }
                        driver.stop();
                        wasSeeingPollen = false;
                    } else {
                        indicator.setPosition(0.0);
                        driver.drive(0, 0, FAST_SEARCH_TURN_SPEED);
                    }
                }
            } else {
                indicator.setPosition(0.0);
                driver.teleopDrive(gamepad1);
                wasSeeingPollen = false;
            }
            telemetry.update();
        }
    }

    private LLResultTypes.DetectorResult findClosestPollen(LLResult result) {
        if (result == null || !result.isValid()) return null;
        LLResultTypes.DetectorResult best = null;
        for (LLResultTypes.DetectorResult d : result.getDetectorResults()) {
            if (!d.getClassName().equals("pollen")) continue;
            if (d.getConfidence() < MIN_CONFIDENCE) continue;
            if (best == null || d.getTargetArea() > best.getTargetArea()) best = d;
        }
        return best;
    }
}