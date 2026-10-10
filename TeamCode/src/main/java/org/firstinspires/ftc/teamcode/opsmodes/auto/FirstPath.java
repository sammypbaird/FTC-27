package org.firstinspires.ftc.teamcode.opsmodes.auto;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.groups.Groups.sequential;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.Moves;

/*
 * FIRST PATH -- our first autonomous
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
 * BEFORE RUNNING
 *   Run the Pedro tuners first (see Tuning.java). The Foresight values in
 *   Constants.java are placeholders until then. Clear some space around the
 *   robot, and keep a hand near STOP the first time!
 */
@Autonomous(name = "First Path", group = "Odometry")
public class FirstPath extends LinearOpMode {

    // The spots the robot drives through.
    private final Pose start = new Pose(0, 0, 0);                         // facing forward
    private final Pose afterDrive = new Pose(24, 0, 0);                   // 24" forward
    private final Pose afterTurn = new Pose(24, 0, Math.toRadians(90));   // same spot, turned left
    private final Pose end = new Pose(24, 12, Math.toRadians(90));        // 12" further

    @Override
    public void runOpMode() {
        Follower follower = Constants.create(hardwareMap);
        Moves moves = new Moves(follower);

        // THE PLAN: one move after another.
        // ".constant(heading)" keeps the robot facing one way for the whole path.
        Command auto = sequential(
                moves.drive(line(start, afterDrive).constant(0)),
                moves.turn(afterTurn),
                moves.drive(line(afterTurn, end).constant(Math.toRadians(90))),
                moves.hold(end));

        telemetry.addLine("Ready. Press START.");
        telemetry.update();
        waitForStart();

        follower.setPose(start);   // tell Pedro where the robot is starting
        Scheduler.reset();         // clear out commands left over from the last OpMode
        Scheduler.schedule(auto);  // start the plan

        while (opModeIsActive()) {
            follower.update();     // Pedro: read the odometry and set the motor powers
            Scheduler.execute();   // Ivy: run the current command, move on when it's done

            Pose pose = follower.pose();
            telemetry.addData("X (in)", "%.1f", pose.x());
            telemetry.addData("Y (in)", "%.1f", pose.y());
            telemetry.addData("Heading (deg)", "%.1f", Math.toDegrees(pose.heading()));
            telemetry.update();
        }

        follower.stop();
    }
}
