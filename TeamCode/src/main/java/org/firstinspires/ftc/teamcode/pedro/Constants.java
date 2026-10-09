package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

/*
 * CONSTANTS -- everything Pedro Pathing needs to know about OUR robot
 * -------------------------------------------------------------------------
 * Pedro Pathing is a library that drives the robot along paths for us.
 * It needs three things, and each one has a section below:
 *
 *   1. The DRIVETRAIN  -- which motors drive the wheels, and which way they spin
 *   2. The LOCALIZER   -- how the robot knows where it is (our goBILDA Pinpoint)
 *   3. The ALGORITHM   -- "Foresight", the math that steers the robot along a path
 *
 * Every OpMode that follows paths gets its Follower from Constants.create().
 *
 * DISTANCES ARE IN INCHES. ANGLES ARE IN RADIANS (use Math.toRadians(90)).
 *
 * The Pedro tuners (in Tuning.java) measure most of these numbers for us.
 * When a tuner finishes, it prints code. Paste that code into this file.
 */
public class Constants {

    // ---------------------------------------------------------------------
    // 1. DRIVETRAIN: the four mecanum wheel motors
    // ---------------------------------------------------------------------
    // These names must match the robot configuration on the Driver Station.
    // They're the same names and directions our Driver class uses.
    // Check them with the Mecanum Tuner.
    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("frontLeft");
        c.frontRightName.set("frontRight");
        c.backLeftName.set("backLeft");
        c.backRightName.set("backRight");

        c.frontLeftDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backLeftDirection.set(DcMotorSimple.Direction.FORWARD);
        c.frontRightDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backRightDirection.set(DcMotorSimple.Direction.REVERSE);
    });

    // ---------------------------------------------------------------------
    // 2. LOCALIZER: the goBILDA Pinpoint odometry computer
    // ---------------------------------------------------------------------
    // The Pinpoint reads two small "odometry pods" (unpowered wheels that
    // roll on the floor) plus its own gyro, and works out where we are.
    //
    // These values are from last season's robot (FTC-26), which has the same
    // hardware. If the pods were moved, re-measure them with the Pinpoint Tuner.
    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");   // name in the robot configuration
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);

        // Where the pods are, measured from the center of the robot, in inches.
        //   xPodOffset: how far LEFT of center the forward-rolling pod is (right = negative)
        //   yPodOffset: how far FORWARD of center the sideways-rolling pod is (back = negative)
        c.xPodOffset.set(-3.5);   // forward pod is 3.5" to the right of center
        c.yPodOffset.set(6.75);   // sideways pod is 6.75" in front of center

        // Which way each pod counts. Pushing the robot forward should make X
        // go UP, and pushing it left should make Y go UP. If one goes the wrong
        // way, flip it between FORWARD and REVERSED.
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
    });

    // ---------------------------------------------------------------------
    // 3. ALGORITHM: Foresight, which steers the robot along a path
    // ---------------------------------------------------------------------
    // !!! PLACEHOLDER VALUES !!!
    // These come from Pedro's example robot, NOT ours. Pedro won't start
    // without them, so they're here as a starting point. Before relying on
    // paths, run the Foresight Tuner and replace this whole block with the
    // code it prints.
    public static ForesightConfig foresightConfig = new ForesightConfig(c -> {
        // How hard to correct when the robot drifts off the path.
        // (Two strengths: a gentle one, and a stronger one once it's more than 2.5" off.)
        Controller gentleForward = Controller.proportional(0.1);
        Controller strongForward = Controller.proportional(0.3);
        Controller gentleSideways = Controller.proportional(0.1);
        Controller strongSideways = Controller.proportional(0.3);
        c.forwardTranslational.set(Controller.piecewise(gentleForward).put(2.5, strongForward));
        c.strafeTranslational.set(Controller.piecewise(gentleSideways).put(2.5, strongSideways));

        // How the robot speeds up, coasts and brakes.
        c.coast.set(Controller.proportionalFeedforward(0.010978350889324107));
        c.brake.set(Controller.proportionalFeedforward(0.008731598255925491));
        c.linearBrakeCoefficients.set(Matrix.diag(0.10605894992901523, 0.08719146175596092));
        c.quadraticBrakeCoefficients.set(Matrix.diag(0.0014663966976606565, 0.0013837064502458813));

        // How hard to correct when the robot points the wrong way.
        c.headingFeedback.set(Controller.proportional(5.258721785960744));
        c.headingBrakeCoefficients.set(Vector2D.cartesian(0.05642143125655298, 0.0063829525363003695));

        // How fast the robot can go (inches/second) and how fast it slows down on its own.
        c.maxAchievableForwardVelocity.set(72.72923108818539);
        c.maxAchievableStrafeVelocity.set(52.34323936525474);
        c.naturalForwardDeceleration.set(85.01144677379789);
        c.naturalStrafeDeceleration.set(104.49787535782846);
    });

    /** Builds the Follower: the object that actually drives our paths. */
    public static Follower create(HardwareMap hardwareMap) {
        return new Follower(
                new PinpointLocalizer(hardwareMap, localizerConfig),
                new Mecanum(hardwareMap, drivetrainConfig),
                new Foresight(foresightConfig));
    }
}
