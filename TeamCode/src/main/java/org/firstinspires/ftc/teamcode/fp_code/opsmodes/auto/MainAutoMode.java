package org.firstinspires.ftc.teamcode.fp_code.opsmodes.auto;

import com.pedropathing.math.Pose;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.fp_code.subsystems.Driver;
import org.firstinspires.ftc.teamcode.pedro.Constants;

import java.util.Map;

@Autonomous(name = "F - MainAutoMode", group = "Competition")
public class MainAutoMode extends LinearOpMode {

    // Strict 30-second autonomous timer budget
    private static final long AUTO_BUDGET_MS = 30000;
    private static final long END_MOVE_BUFFER_MS = 5500;

    // Step up in +X first before moving to the shooting spot to avoid hitting the wall
    private static final double PRE_SHOOT_X = 12.0;
    private static final double PRE_SHOOT_Y = 0.0;
    private static final double PRE_SHOOT_HEADING_DEG = 324.8;

    // First shooting pose (inches and degrees)
    private static final double SHOOT_X = 0.4;
    private static final double SHOOT_Y = 14.5;
    private static final double SHOOT_HEADING_DEG = 324.8;

    // Final destination after shooting (replaces returning to 0, 0)
    private static final double END_X = 94.6;
    private static final double END_Y = 50.0;
    private static final double END_HEADING_DEG = 3.4;

    // Drive signs (kept from your working movement setup)
    private static final double FORWARD_SIGN = -1.0;
    private static final double STRAFE_SIGN = -1.0;
    private static final double TURN_SIGN = 1.0;

    // Motor directions: flywheel stays FORWARD; intake & transfer flipped to REVERSE
    private static final DcMotorSimple.Direction INTAKE_DIRECTION = DcMotorSimple.Direction.REVERSE;
    private static final DcMotorSimple.Direction TRANSFER_DIRECTION = DcMotorSimple.Direction.REVERSE;
    private static final DcMotorSimple.Direction FLYWHEEL_DIRECTION = DcMotorSimple.Direction.FORWARD;

    // Motor powers
    private static final double INTAKE_IN_POWER = 1.0;
    private static final double INTAKE_OUT_POWER = -1.0;
    private static final double TRANSFER_FORWARD_POWER = 1.0;
    private static final double TRANSFER_BACKWARD_POWER = -1.0;
    private static final double FLYWHEEL_SHOOT_POWER = 1.0;

    // Possible Robot Config names for each goBILDA 5202/5203/5204 motor
    private static final String[] INTAKE_NAMES = {"intake", "Intake", "intakeMotor", "intake_motor"};
    private static final String[] TRANSFER_NAMES = {"transfer", "Transfer", "transferMotor", "transfer_motor", "indexer", "feeder"};
    private static final String[] FLYWHEEL_NAMES = {"flywheel", "Flywheel", "flywheelMotor", "flywheel_motor", "shooter", "launcher"};

    // Closed-loop drive tolerances & gains
    private static final double POS_TOLERANCE_IN = 1.0;
    private static final double HEADING_TOLERANCE_DEG = 1.8;
    private static final double KP_DRIVE = 0.045;
    private static final double KP_TURN = 0.025;
    private static final double MAX_DRIVE_POWER = 0.72;
    private static final double MIN_DRIVE_POWER = 0.16;
    private static final long MOVE_TIMEOUT_MS = 6500;

    // Redundant stall / wall-avoidance settings (pushes out/sidesteps away from wall, never into it)
    private static final long STALL_CHECK_WINDOW_MS = 400;
    private static final double STALL_MIN_MOVE_IN = 0.6;
    private static final int MAX_STALL_ESCAPES = 4;
    private static final long ESCAPE_MOVE_MS = 380;
    private static final long ESCAPE_STRAFE_MS = 350;
    private static final double ESCAPE_POWER = 0.35;

    // Shooting & unjam timings (in milliseconds)
    private static final long SPINUP_MS = 1100;
    private static final long FEED_BURST_MS = 850;
    private static final long RECOVERY_MS = 450;
    private static final long UNJAM_MS = 300;
    private static final long CLEAR_TAIL_MS = 200;

    private Driver driver;
    private DcMotor intakeMotor;
    private DcMotor transferMotor;
    private DcMotor flywheelMotor;
    private PinpointLocalizer odometry;
    private long autoStartTimeMs = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        driver = new Driver(hardwareMap);
        driver.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intakeMotor = findDcMotor(INTAKE_NAMES, "intake");
        transferMotor = findDcMotor(TRANSFER_NAMES, "transfer");
        flywheelMotor = findDcMotor(FLYWHEEL_NAMES, "flywheel");

        intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeMotor.setDirection(INTAKE_DIRECTION);

        transferMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        transferMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        transferMotor.setDirection(TRANSFER_DIRECTION);

        flywheelMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheelMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheelMotor.setDirection(FLYWHEEL_DIRECTION);

        odometry = new PinpointLocalizer(hardwareMap, Constants.localizerConfig);

        telemetry.addLine("MainAutoMode READY - +X Step -> (0.4, 14.5) @ 324.8 deg -> (94.6, 50.0) @ 3.4 deg");
        telemetry.addLine("Keep the robot still, then press START");
        telemetry.update();

        waitForStart();

        if (!opModeIsActive()) {
            return;
        }

        autoStartTimeMs = System.currentTimeMillis();

        // Start at (0, 0, 0 deg)
        odometry.setPose(new Pose(0, 0, 0));
        sleep(40);
        odometry.update();

        // 1. Go up in +X axis first before moving to (0.4, 14.5) so it doesn't hit the wall
        if (hasTimeForShootingAndEnd()) {
            driveToPose("Step +X First", PRE_SHOOT_X, PRE_SHOOT_Y, PRE_SHOOT_HEADING_DEG, false, 3500);
        }
        if (hasTimeForShootingAndEnd()) {
            driveToPose("Move to Shoot (0.4, 14.5)", SHOOT_X, SHOOT_Y, SHOOT_HEADING_DEG, true, 5000);
        }
        if (hasTimeForShootingAndEnd()) {
            driveToPose("Lock Shoot Pose", SHOOT_X, SHOOT_Y, SHOOT_HEADING_DEG, true, 1600);
        }

        // 2. First shooting set
        if (hasTimeForShootingAndEnd()) {
            shootSet("Set 1", true);
        }

        // 3. Brief feeder unstick & re-lock onto (0.4, 14.5) @ 324.8 deg
        if (hasTimeForShootingAndEnd()) {
            intakeMotor.setPower(INTAKE_OUT_POWER);
            transferMotor.setPower(TRANSFER_BACKWARD_POWER);
            sleepAndTrack(UNJAM_MS, "Unstick feeder");

            intakeMotor.setPower(0);
            transferMotor.setPower(0);
        }

        if (hasTimeForShootingAndEnd()) {
            driveToPose("Re-lock Shoot Pose", SHOOT_X, SHOOT_Y, SHOOT_HEADING_DEG, true, 2200);
        }

        // 4. Second shooting set
        if (hasTimeForShootingAndEnd()) {
            shootSet("Set 2", false);
        }

        stopShooterAndFeeders();

        // 5. Move to final target (94.6, 50.0) at 3.4 deg instead of returning to (0, 0)
        moveToFinalPose();

        odometry.update();
        Pose finalPose = odometry.pose();
        telemetry.addData("Status", "DONE");
        telemetry.addData("Elapsed (s)", "%.1f / 30.0", elapsedSeconds());
        telemetry.addData("Final X (in)", "%.1f", finalPose.x());
        telemetry.addData("Final Y (in)", "%.1f", finalPose.y());
        telemetry.addData("Final Heading (deg)", "%.1f", Math.toDegrees(finalPose.heading()));
        telemetry.update();
    }

    private void shootSet(String label, boolean useIntake) throws InterruptedException {
        flywheelMotor.setPower(FLYWHEEL_SHOOT_POWER);
        if (useIntake) {
            intakeMotor.setPower(INTAKE_IN_POWER);
        }
        sleepAndTrack(SPINUP_MS, label + " - Spin up flywheel");

        if (!hasTimeForShootingAndEnd()) {
            return;
        }

        // Burst 1
        transferMotor.setPower(TRANSFER_FORWARD_POWER);
        sleepAndTrack(FEED_BURST_MS, label + " - Shooting burst 1");
        transferMotor.setPower(0);

        if (!hasTimeForShootingAndEnd()) {
            return;
        }

        // Short recovery while keeping flywheel spinning
        sleepAndTrack(RECOVERY_MS, label + " - Flywheel recovery");

        // Burst 2
        transferMotor.setPower(TRANSFER_FORWARD_POWER);
        sleepAndTrack(FEED_BURST_MS, label + " - Shooting burst 2");
        transferMotor.setPower(0);

        sleepAndTrack(CLEAR_TAIL_MS, label + " - Clear tail");
        if (useIntake) {
            intakeMotor.setPower(0);
        }
    }

    private void moveToFinalPose() throws InterruptedException {
        long remainingMs = remainingBudgetMs();
        if (remainingMs > 200) {
            driveToPose("Go to (94.6, 50.0) @ 3.4 deg", END_X, END_Y, END_HEADING_DEG, false, remainingMs);
        }
        driver.stop();
    }

    private void stopShooterAndFeeders() {
        intakeMotor.setPower(0);
        transferMotor.setPower(0);
        flywheelMotor.setPower(0);
    }

    private long elapsedMs() {
        return System.currentTimeMillis() - autoStartTimeMs;
    }

    private double elapsedSeconds() {
        return elapsedMs() / 1000.0;
    }

    private long remainingBudgetMs() {
        return Math.max(0L, AUTO_BUDGET_MS - elapsedMs());
    }

    private boolean hasTimeForShootingAndEnd() {
        return remainingBudgetMs() > END_MOVE_BUFFER_MS;
    }

    private void driveToPose(String stepName, double targetX, double targetY, double targetHeadingDeg, boolean preSpinFlywheel, long maxStepMs) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        long stallWindowStart = System.currentTimeMillis();
        odometry.update();
        double lastCheckX = odometry.pose().x();
        double lastCheckY = odometry.pose().y();
        int stallCount = 0;

        while (opModeIsActive() && remainingBudgetMs() > 100) {
            odometry.update();
            Pose pose = odometry.pose();

            double currentX = pose.x();
            double currentY = pose.y();
            double currentHeadingRad = pose.heading();
            double currentHeadingDeg = Math.toDegrees(currentHeadingRad);

            double errorX = targetX - currentX;
            double errorY = targetY - currentY;
            double distance = Math.hypot(errorX, errorY);
            double headingErrorDeg = angleWrapDeg(targetHeadingDeg - currentHeadingDeg);

            if (preSpinFlywheel && distance < 24.0) {
                flywheelMotor.setPower(FLYWHEEL_SHOOT_POWER);
            }

            if (distance <= POS_TOLERANCE_IN && Math.abs(headingErrorDeg) <= HEADING_TOLERANCE_DEG) {
                break;
            }

            if (System.currentTimeMillis() - startTime > Math.min(MOVE_TIMEOUT_MS, maxStepMs)) {
                break;
            }

            double cosH = Math.cos(currentHeadingRad);
            double sinH = Math.sin(currentHeadingRad);
            double robotForwardError = errorX * cosH + errorY * sinH;
            double robotStrafeError = errorX * sinH - errorY * cosH;

            double forward = Range.clip(robotForwardError * KP_DRIVE, -MAX_DRIVE_POWER, MAX_DRIVE_POWER) * FORWARD_SIGN;
            double strafe = Range.clip(robotStrafeError * KP_DRIVE, -MAX_DRIVE_POWER, MAX_DRIVE_POWER) * STRAFE_SIGN;
            double turn = Range.clip(headingErrorDeg * KP_TURN, -MAX_DRIVE_POWER, MAX_DRIVE_POWER) * TURN_SIGN;

            if (Math.abs(robotForwardError) > POS_TOLERANCE_IN && Math.abs(forward) < MIN_DRIVE_POWER) {
                forward = Math.copySign(MIN_DRIVE_POWER, forward);
            }
            if (Math.abs(robotStrafeError) > POS_TOLERANCE_IN && Math.abs(strafe) < MIN_DRIVE_POWER) {
                strafe = Math.copySign(MIN_DRIVE_POWER, strafe);
            }
            if (Math.abs(headingErrorDeg) > HEADING_TOLERANCE_DEG && Math.abs(turn) < MIN_DRIVE_POWER) {
                turn = Math.copySign(MIN_DRIVE_POWER, turn);
            }

            driver.drive(forward, strafe, turn);

            // Redundant wall avoidance: if stalled, push outward and sidestep around the wall
            long now = System.currentTimeMillis();
            if (now - stallWindowStart >= STALL_CHECK_WINDOW_MS) {
                double movedDist = Math.hypot(currentX - lastCheckX, currentY - lastCheckY);
                if (movedDist < STALL_MIN_MOVE_IN && distance > POS_TOLERANCE_IN) {
                    stallCount++;
                    if (stallCount > MAX_STALL_ESCAPES) {
                        telemetry.addData("Step", stepName + " [Max escapes reached -> holding]");
                        telemetry.update();
                        driver.stop();
                        break;
                    }

                    // Push outward away from the wall
                    driver.drive(ESCAPE_POWER * FORWARD_SIGN, 0, 0);
                    sleepAndTrack(ESCAPE_MOVE_MS, stepName + " [Wall Escape #" + stallCount + ": Push out]");
                    driver.stop();

                    // Strafe to clear the wall/obstacle (alternates direction on retries)
                    double strafeDir = (stallCount % 2 == 1) ? 1.0 : -1.0;
                    driver.drive(0, strafeDir * ESCAPE_POWER * STRAFE_SIGN, 0);
                    sleepAndTrack(ESCAPE_STRAFE_MS, stepName + " [Wall Escape #" + stallCount + ": Sidestep]");
                    driver.stop();
                }
                stallWindowStart = System.currentTimeMillis();
                odometry.update();
                lastCheckX = odometry.pose().x();
                lastCheckY = odometry.pose().y();
            }

            telemetry.addData("Step", stepName);
            telemetry.addData("Time Left (s)", "%.1f", remainingBudgetMs() / 1000.0);
            telemetry.addData("Current (X, Y, H)", "(%.1f, %.1f, %.1f deg)", currentX, currentY, currentHeadingDeg);
            telemetry.addData("Target  (X, Y, H)", "(%.1f, %.1f, %.1f deg)", targetX, targetY, targetHeadingDeg);
            telemetry.addData("Distance Left (in)", "%.1f", distance);
            telemetry.addData("Stall Escapes", "%d/%d", stallCount, MAX_STALL_ESCAPES);
            telemetry.update();
            sleep(20);
        }

        driver.stop();
    }

    private DcMotor findDcMotor(String[] candidateNames, String label) {
        for (String name : candidateNames) {
            DcMotor motor = hardwareMap.tryGet(DcMotor.class, name);
            if (motor != null) {
                return motor;
            }
        }

        StringBuilder configuredMotors = new StringBuilder();
        for (Map.Entry<String, DcMotor> entry : hardwareMap.dcMotor.entrySet()) {
            if (configuredMotors.length() > 0) {
                configuredMotors.append(", ");
            }
            configuredMotors.append("\"").append(entry.getKey()).append("\"");
        }

        String foundList = configuredMotors.length() > 0 ? configuredMotors.toString() : "NONE";
        telemetry.addLine("MISSING MOTOR: " + label);
        telemetry.addLine("Motors in Robot Config: " + foundList);
        telemetry.update();

        throw new IllegalArgumentException(
                "Could not find " + label + " as DcMotor. Motors found in Robot Config: [" + foundList + "]"
        );
    }

    private void sleepAndTrack(long ms, String phase) throws InterruptedException {
        long start = System.currentTimeMillis();
        while (opModeIsActive() && remainingBudgetMs() > 100 && (System.currentTimeMillis() - start) < ms) {
            odometry.update();
            Pose pose = odometry.pose();
            telemetry.addData("Step", phase);
            telemetry.addData("Time Left (s)", "%.1f", remainingBudgetMs() / 1000.0);
            telemetry.addData("Current (X, Y, H)", "(%.1f, %.1f, %.1f deg)", pose.x(), pose.y(), Math.toDegrees(pose.heading()));
            telemetry.update();
            sleep(25);
        }
        if (!opModeIsActive()) {
            throw new InterruptedException("OpMode stopped");
        }
    }

    private double angleWrapDeg(double deg) {
        while (deg > 180.0) deg -= 360.0;
        while (deg <= -180.0) deg += 360.0;
        return deg;
    }
}