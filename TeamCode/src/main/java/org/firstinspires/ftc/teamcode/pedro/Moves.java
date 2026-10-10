package org.firstinspires.ftc.teamcode.pedro;

import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.commands.Commands.waitUntil;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.pedro.PedroCommands;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.utils.Angle;

/*
 * MOVES -- the robot's driving commands, ready to use in any autonomous
 * -------------------------------------------------------------------------
 * A COMMAND is one action the robot does, like "drive this path" or "turn
 * to face left". Ivy (our command library) runs commands for us. We line them
 * up in order with sequential(...), and Ivy does one after another.
 *
 *   Moves moves = new Moves(follower);
 *   Command auto = sequential(
 *       moves.drive(somePath),
 *       moves.turn(somePose));
 *
 * Every move has a time limit. If something goes wrong (the robot gets bumped
 * or stuck), the move ends anyway and the autonomous keeps going.
 */
public class Moves {

    // A turn is "done" when we're within 3 degrees of where we want to face.
    // Last season we learned not to make this too small: if it is, the robot
    // can wiggle back and forth forever without ever "finishing" the turn.
    public static final double TURN_TOLERANCE = Math.toRadians(3);

    // Time limits, in milliseconds.
    public static final double DRIVE_TIMEOUT_MS = 4000;
    public static final double TURN_TIMEOUT_MS = 2000;

    private final Follower follower;

    public Moves(Follower follower) {
        this.follower = follower;
    }

    /** Drive along a path. Finishes at the end of the path (or after the time limit). */
    public Command drive(Path path) {
        return PedroCommands.follow(follower, path)
                .raceWith(waitMs(DRIVE_TIMEOUT_MS));   // "race": whichever finishes first wins
    }

    /**
     * Drive to a spot and face a direction, then hold still there.
     * With the same x and y as now, this turns the robot in place.
     * Finishes when we're facing the right way (or after the time limit).
     */
    public Command turn(Pose target) {
        return PedroCommands.hold(follower, target)            // start moving to the target
                .then(waitUntil(() -> facing(target))           // wait until we face the right way
                        .raceWith(waitMs(TURN_TIMEOUT_MS)));    // ...but not forever
    }

    /** Hold still at a spot, so the robot doesn't drift. */
    public Command hold(Pose target) {
        return PedroCommands.hold(follower, target);
    }

    /**
     * Are we facing the same way as the target?
     * Angle.smallestDifference handles wrapping around (359 degrees vs
     * 1 degree is only 2 apart) and always gives a positive number.
     */
    private boolean facing(Pose target) {
        return Angle.smallestDifference(follower.pose().heading(), target.heading()) < TURN_TOLERANCE;
    }
}
