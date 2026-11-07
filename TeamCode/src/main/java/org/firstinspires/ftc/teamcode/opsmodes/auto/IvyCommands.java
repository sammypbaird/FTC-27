package org.firstinspires.ftc.teamcode.opsmodes.auto;

import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.commands.Commands.waitUntil;
import static com.pedropathing.ivy.groups.Groups.sequential;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.behaviors.EndCondition;
import com.pedropathing.ivy.pedro.PedroCommands;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.utils.Angle;

import org.firstinspires.ftc.teamcode.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Transfer;


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

    private static final double LAUNCH_MS = 3000;
    private static final double UNJAM_MS = 1000;
    private static final double SPINUP_MS = 500;

    private final Follower follower;
    private final Flywheel flywheel;
    private final Transfer transfer;
    private final Intake intake;

    public IvyCommands(Follower follower, Flywheel flywheel, Transfer transfer, Intake intake) {
        this.follower = follower;
        this.flywheel = flywheel;
        this.transfer = transfer;
        this.intake = intake;
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

    // ------------------------------------------------
    // SHOOTING
    // ------------------------------------------------

    /** Start the flywheel It keeps spinning until stop.Flywheel(). */
    public Command startFlywheel(double ms){
        return sequential(
                instant(flywheel::spin),
                waitMs(ms)
        );
    }

    /** Stop the flywheel. It coasts to a stop. */
    public Command stopFlywheel() {
        return instant(flywheel::stop);
    }

    /** Run the transfer FORWARD for some time, launching balls through the spinning flywheel. */
    public Command launchBalls(double ms) {
        return sequential (
                instant(transfer::forward),
                instant(flywheel::spin),
                instant(intake::in),
                waitMs(ms),
                instant(transfer::stop),
                instant(flywheel::stop),
                instant(intake::stop)
        );
    }

    /** Run the transfer BACKWARD for some time to free a stuck ball. */
    public Command unjam(double ms) {
        return sequential (
                instant(transfer::backward),
                waitMs(ms),
                instant(transfer::stop)
        );
    }

    public Command shootSequence(){
        return sequential(
                launchBalls(LAUNCH_MS),
                unjam(UNJAM_MS),
                startFlywheel(SPINUP_MS),
                launchBalls(LAUNCH_MS)
        );
    }

    //------------------
    //INTAKE
    //----------------------------------------

    public Command intakeOn() {
        return instant(intake::in);
    }

    public Command intakeOff() {
        return instant(intake::stop);
    }

}
