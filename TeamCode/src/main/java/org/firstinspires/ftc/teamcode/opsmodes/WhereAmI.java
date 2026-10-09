package org.firstinspires.ftc.teamcode.opsmodes;

import com.pedropathing.math.Pose;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedro.Constants;

/*
 * WHERE AM I -- shows where the odometry thinks the robot is
 * -------------------------------------------------------------------------
 * Use this to check the Pinpoint before trusting it with paths.
 * The motors don't move. Push the robot around by hand and watch the numbers.
 *
 * The robot's starting spot is (0, 0), facing 0 degrees.
 *   X       goes UP when you push the robot FORWARD
 *   Y       goes UP when you push the robot to the LEFT
 *   Heading goes UP when you turn the robot LEFT (counterclockwise)
 *
 * TRY THIS
 *   1. Push the robot forward exactly 24 inches (use a tape measure).
 *      X should read about 24.
 *   2. Turn it in place one full circle. It should end near (0, 0) again.
 *   If a number goes the wrong way, flip that pod's direction in Constants.java.
 *
 * Press A to reset back to (0, 0, 0).
 */
@TeleOp(name = "Where Am I", group = "Odometry")
public class WhereAmI extends LinearOpMode {

    @Override
    public void runOpMode() {
        // The localizer reads the Pinpoint. We only need that (no driving),
        // so this works even before the Pedro tuners have been run.
        PinpointLocalizer odometry = new PinpointLocalizer(hardwareMap, Constants.localizerConfig);

        telemetry.addLine("Keep the robot still, then press START.");
        telemetry.update();
        waitForStart();

        // Start counting from here: (0, 0), facing 0 degrees.
        odometry.setPose(new Pose(0, 0, 0));

        while (opModeIsActive()) {
            if (gamepad1.aWasPressed()) {
                odometry.setPose(new Pose(0, 0, 0));
            }

            // Ask the Pinpoint for its latest reading. Do this every loop!
            odometry.update();
            Pose pose = odometry.pose();

            telemetry.addData("X (in)", "%.1f", pose.x());
            telemetry.addData("Y (in)", "%.1f", pose.y());
            // Pedro uses radians, so convert to degrees to make it easy to read.
            telemetry.addData("Heading (deg)", "%.1f", Math.toDegrees(pose.heading()));
            telemetry.addLine();
            telemetry.addLine("Press A to reset to (0, 0, 0)");
            telemetry.update();
        }
    }
}
