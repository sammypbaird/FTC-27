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


public class AutoMoves {

    // A turn is "done" within 3 degrees. Too small, and the robot can
    // wiggle back and forth forever without ever finishing (last season!).
    public static final double TURN_TOLERANCE = Math.toRadians(3);

    // Time limits, in milliseconds.
    public static final double DRIVE_TIMEOUT_MS = 4000;
    public static final double TURN_TIMEOUT_MS = 2000;

    private final Follower follower;

    public AutoMoves(Follower follower) {
        this.follower = follower;
    }

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
