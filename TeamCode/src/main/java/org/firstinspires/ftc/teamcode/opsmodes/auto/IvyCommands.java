package org.firstinspires.ftc.teamcode.opsmodes.auto;

import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.commands.Commands.waitUntil;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.behaviors.EndCondition;
import com.pedropathing.ivy.pedro.PedroCommands;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.utils.Angle;


/*
 * IVY COMMANDS -- all of our robot's commands for autonomous, in one place
 * -------------------------------------------------------------------------
 * Each method gives back a Command for Ivy to run. Line them up with
 * sequential(...) and Ivy runs them one after another.
 *
 *   IvyCommands commands = new IvyCommands(follower);
 *   Command auto = sequential(
 *       commands.drive(somePath),
 *       commands.turn(somePose),
 *       commands.hold(somePose));
 *
 * Every command should have a time limit, so a bump or a stuck POLLEN
 * can't freeze the whole autonomous. Keep each group of commands under its
 * own heading below (DRIVING, and later SHOOTING, INTAKE...) so it stays
 * easy to find things.
 */
public class IvyCommands {

    // A turn is "done" within 3 degrees. Too small, and the robot can
    // wiggle back and forth forever without ever finishing (last season!).
    public static final double TURN_TOLERANCE = Math.toRadians(3);

    // Time limits, in milliseconds.
    public static final double DRIVE_TIMEOUT_MS = 4000;
    public static final double TURN_TIMEOUT_MS = 2000;

    private final Follower follower;

    public IvyCommands(Follower follower) {
        this.follower = follower;
    }

    // ---------------------------------------------------------------------
    // DRIVING
    // ---------------------------------------------------------------------

    /** Drive along a path. Finishes at the end of the path, or after the time limit. */
    public Command drive(Path path) {
        return PedroCommands.follow(follower, path).setEnd(how -> {
            // If the time limit cut us off, stop right where we are.
            if (how == EndCondition.INTERRUPTED) {
                follower.hold(follower.pose());
            }
        }).raceWith(waitMs(DRIVE_TIMEOUT_MS));
    }

    /** Move to a spot and face a direction. Same x and y as now = turn in place. */
    public Command turn(Pose target) {
        return PedroCommands.hold(follower, target)            // start turning
                .then(waitUntil(() -> isHeadingWithinTolerance(target))           // wait until we face the right way
                        .raceWith(waitMs(TURN_TIMEOUT_MS)));    // ...but not forever
    }

    /** Hold still at a spot, so the robot doesn't drift. */
    public Command hold(Pose target) {
        return PedroCommands.hold(follower, target);
    }

    /** Are we within TURN_TOLERANCE of the target heading? Handles 359° vs 1°. */
    private boolean isHeadingWithinTolerance(Pose target) {
        return Angle.smallestDifference(follower.pose().heading(), target.heading()) < TURN_TOLERANCE;
    }
}
