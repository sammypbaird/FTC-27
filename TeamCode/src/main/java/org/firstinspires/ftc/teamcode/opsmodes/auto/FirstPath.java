package org.firstinspires.ftc.teamcode.opsmodes.auto;

import static com.pedropathing.api.Paths.line;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.utils.Angle;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedro.Constants;

/*
 * FIRST PATH -- our first Pedro Pathing autonomous
 * -------------------------------------------------------------------------
 * The robot will:
 *   1. drive forward 24 inches
 *   2. turn 90 degrees to the left
 *   3. drive forward 12 inches
 *
 * HOW PEDRO THINKS ABOUT THE FIELD
 *   Everything is a Pose: (x, y, heading).
 *     x       = inches forward from where the robot started
 *     y       = inches to the left of where the robot started
 *     heading = which way the robot faces, in RADIANS. 0 = the way it
 *               started, Math.toRadians(90) = turned 90 degrees left.
 *   So after turning left, "forward" means +y.
 *
 *     y (left)
 *     ^
 *     |                 END (24, 12)
 *     |                   ^
 *     |                   |  3. forward 12"
 *     |                   |
 *   START (0, 0) -----> (24, 0) ----> x (forward)
 *       1. forward 24"    2. turn left 90 degrees here
 *
 * HOW THE CODE WORKS
 *   The robot does one STEP at a time. Each loop checks: "is this step
 *   finished?" If yes, it starts the next step. This is called a state machine.
 *
 * BEFORE RUNNING
 *   Run the Pedro tuners first (see Tuning.java). The Foresight values in
 *   Constants.java are placeholders until then. Clear some space around the
 *   robot, and keep a hand near STOP the first time!
 */
@Autonomous(name = "First Path", group = "Odometry")
public class FirstPath extends LinearOpMode {

    // The steps, in order.
    private enum Step { DRIVE_FORWARD_24, TURN_LEFT_90, DRIVE_FORWARD_12, DONE }

    // A turn is "close enough" when we're within 3 degrees of where we want to face.
    // Last season we learned not to make this too small: if it is, the robot can
    // wiggle back and forth forever without ever "finishing" the turn.
    private static final double TURN_TOLERANCE = Math.toRadians(3);

    // Safety nets: if a step takes longer than this, move on anyway, instead of
    // getting stuck. (Another lesson from last season.)
    private static final double TURN_TIMEOUT_SECONDS = 2.0;
    private static final double DRIVE_TIMEOUT_SECONDS = 4.0;

    // The three spots the robot drives through.
    private final Pose start = new Pose(0, 0, 0);                          // facing forward
    private final Pose afterDrive = new Pose(24, 0, 0);                    // 24" forward
    private final Pose afterTurn = new Pose(24, 0, Math.toRadians(90));    // same spot, turned left
    private final Pose end = new Pose(24, 12, Math.toRadians(90));         // 12" further

    private Follower follower;
    private Step step;
    private final ElapsedTime stepTimer = new ElapsedTime();   // how long the current step has run

    @Override
    public void runOpMode() {
        follower = Constants.create(hardwareMap);

        // The paths. ".constant(heading)" keeps the robot facing one way the whole time.
        Path forward24 = line(start, afterDrive).constant(0);
        Path forward12 = line(afterTurn, end).constant(Math.toRadians(90));

        telemetry.addLine("Ready. Press START.");
        telemetry.update();
        waitForStart();

        // Tell Pedro where the robot is starting.
        follower.setPose(start);

        // Step 1: start driving forward 24".
        follower.follow(forward24);
        startStep(Step.DRIVE_FORWARD_24);

        while (opModeIsActive()) {
            // Pedro must update every loop: it reads the odometry and sets the motor powers.
            follower.update();

            switch (step) {
                case DRIVE_FORWARD_24:
                    if (!follower.isBusy() || stepTimer.seconds() > DRIVE_TIMEOUT_SECONDS) {
                        // Step 2: turn in place by holding the same spot with a new heading.
                        follower.hold(afterTurn);
                        startStep(Step.TURN_LEFT_90);
                    }
                    break;

                case TURN_LEFT_90:
                    if (headingError() < TURN_TOLERANCE || stepTimer.seconds() > TURN_TIMEOUT_SECONDS) {
                        // Step 3: drive forward 12" in the new direction.
                        follower.follow(forward12);
                        startStep(Step.DRIVE_FORWARD_12);
                    }
                    break;

                case DRIVE_FORWARD_12:
                    if (!follower.isBusy() || stepTimer.seconds() > DRIVE_TIMEOUT_SECONDS) {
                        // Finished! Hold this spot so the robot doesn't drift.
                        follower.hold(end);
                        startStep(Step.DONE);
                    }
                    break;

                case DONE:
                    break;
            }

            Pose pose = follower.pose();
            telemetry.addData("Step", step);
            telemetry.addData("X (in)", "%.1f", pose.x());
            telemetry.addData("Y (in)", "%.1f", pose.y());
            telemetry.addData("Heading (deg)", "%.1f", Math.toDegrees(pose.heading()));
            telemetry.update();
        }

        follower.stop();
    }

    /** Switches to a new step and restarts the step timer. */
    private void startStep(Step newStep) {
        step = newStep;
        stepTimer.reset();
    }

    /**
     * How far (in radians) the robot is from facing the direction we want.
     * Angle.smallestDifference handles wrapping around (359 degrees vs 1
     * degree is only 2 apart) and always gives a positive number.
     */
    private double headingError() {
        return Angle.smallestDifference(follower.pose().heading(), afterTurn.heading());
    }
}
