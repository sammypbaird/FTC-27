package org.firstinspires.ftc.teamcode.fp_code.opsmodes.auto;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.fp_code.subsystems.Driver;

@Autonomous(name = "F - GoToPollen FUN", group = "Test")
public class GoToPollenFUNOp extends LinearOpMode {
    private static final double MIN_CONFIDENCE = 0.5;
    private static final double TURN_GAIN = 0.02;
    private static final double MAX_TURN = 0.3;
    private static final double FORWARD_GAIN = 0.05;
    private static final double MAX_FORWARD = 0.5;
    private static final double REACHED_AREA = 25.0; // Area threshold to trigger the smash sequence

    private enum FunState {
        SEARCHING,
        APPROACHING,
        SPINNING_FOR_TARGET,
        BACKING_UP,
        SMASH_FORWARD
    }

    private FunState currentState = FunState.SEARCHING;
    private boolean goToPollenActive = false;
    private Servo indicator;
    private ElapsedTime actionTimer = new ElapsedTime();

    @Override
    public void runOpMode() throws InterruptedException {
        Driver driver = new Driver(hardwareMap);
        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");
        indicator = hardwareMap.get(Servo.class, "indicator");

        limelight.pipelineSwitch(1);
        limelight.start();

        telemetry.addLine("Ready: Press A to toggle FUN Smash Routine");
        telemetry.update();

        waitForStart();

        while(opModeIsActive()) {
            if (gamepad1.aWasPressed()) {
                goToPollenActive = !goToPollenActive;
                currentState = FunState.SEARCHING;
            }

            LLResult result = limelight.getLatestResult();
            LLResultTypes.DetectorResult pollen = findClosestPollen(result);

            if (goToPollenActive) {
                switch (currentState) {
                    case SEARCHING:
                        indicator.setPosition(0.0); // Red
                        driver.drive(0, 0, 0.5); // Spin to find pollen

                        if (pollen != null) {
                            currentState = FunState.APPROACHING;
                        }
                        telemetry.addData("Fun Status", "Searching for Pollen...");
                        break;

                    case APPROACHING:
                        indicator.setPosition(0.4); // Green
                        if (pollen != null) {
                            double tx = pollen.getTargetXDegrees();
                            double ta = pollen.getTargetArea();

                            double yaw = Range.clip(TURN_GAIN * tx, -MAX_TURN, MAX_TURN);
                            double forwardError = REACHED_AREA - ta;
                            double forward = Range.clip(FORWARD_GAIN * forwardError, -MAX_FORWARD, MAX_FORWARD);

                            driver.drive(forward, 0, yaw);

                            // Once close enough, trigger the spin-reset sequence
                            if (ta >= REACHED_AREA) {
                                currentState = FunState.SPINNING_FOR_TARGET;
                            }
                        } else {
                            // If lost during approach, go back to searching
                            currentState = FunState.SEARCHING;
                        }
                        telemetry.addData("Fun Status", "Approaching Pollen");
                        break;

                    case SPINNING_FOR_TARGET:
                        indicator.setPosition(0.0); // Red
                        driver.drive(0, 0, 0.4); // Spin in place until it finds pollen again

                        if (pollen != null) {
                            // Found it! Now prep for backup
                            actionTimer.reset();
                            currentState = FunState.BACKING_UP;
                        }
                        telemetry.addData("Fun Status", "Spinning to re-acquire target...");
                        break;

                    case BACKING_UP:
                        indicator.setPosition(0.4); // Green
                        // Back up about a foot (~0.6 seconds at -0.4 power)
                        driver.drive(-0.4, 0, 0);

                        if (actionTimer.seconds() >= 0.6) {
                            actionTimer.reset();
                            currentState = FunState.SMASH_FORWARD;
                        }
                        telemetry.addData("Fun Status", "Backing up 1 foot...");
                        break;

                    case SMASH_FORWARD:
                        indicator.setPosition(0.4); // Green
                        // Full speed forward smash for 0.8 seconds to launch/hit the ball far away!
                        driver.drive(0.9, 0, 0);

                        if (actionTimer.seconds() >= 0.8) {
                            currentState = FunState.SEARCHING; // Loop back to hunting next ball
                        }
                        telemetry.addData("Fun Status", "SMASHING forward!");
                        break;
                }
            } else {
                indicator.setPosition(0.0); // Red
                driver.teleopDrive(gamepad1);
                currentState = FunState.SEARCHING;
                telemetry.addData("Fun Status", "Manual Control");
            }

            telemetry.addData("GoToPollen Active?", goToPollenActive);
            telemetry.update();
        }
    }

    private LLResultTypes.DetectorResult findClosestPollen(LLResult result) {
        if (result == null || !result.isValid())
            return null;

        LLResultTypes.DetectorResult best = null;
        for (LLResultTypes.DetectorResult d : result.getDetectorResults()) {
            if (!d.getClassName().equals("pollen")) continue;
            if (d.getConfidence() < MIN_CONFIDENCE) continue;
            if (best == null || d.getTargetArea() > best.getTargetArea()) best = d;
        }
        return best;
    }
}