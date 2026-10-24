package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.config.Configuration;
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

public class Constants {

    // The four mecanum wheel motors. Names must match the robot configuration.
    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("frontLeft");
        c.frontRightName.set("frontRight");
        c.backRightName.set("backRight");
        c.backLeftName.set("backLeft");

        // Same directions as the Driver class: the LEFT wheels are reversed.
        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
    });


    // The goBILDA Pinpoint. Offsets measured on this year's robot with a tape measure.
    // Run the Pinpoint Tuner to check them (Week 17 of the curriculum).
    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");   // name in the robot configuration
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);

        // Where the pods are, from the center of the robot, in INCHES.
        //   xPodOffset: how far LEFT of center the forward pod is (right = negative)
        //   yPodOffset: how far FORWARD of center the sideways pod is (back = negative)
        c.xPodOffset.set(2.9);    // forward pod is 2.9" to the LEFT of center (measured this year)
        c.yPodOffset.set(5.75);   // sideways pod is 5.75" in front of center (measured this year)

        // Which way each pod counts. Pushing forward should make X go UP,
        // pushing left should make Y go UP. If not, flip FORWARD <-> REVERSED.
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
    });


    // !!! PLACEHOLDER VALUES from Pedro's example robot, NOT ours !!!
    // Run the Foresight Tuner and replace this whole block with what it prints.
    public static ForesightConfig foresightConfig = new ForesightConfig(c -> {
        // How hard to correct when the robot drifts off the path
        // (gentle, then stronger once it's more than 2.5" off).
        c.forwardTranslational.set(Controller.piecewise(Controller.proportional(0.1))
                .put(2.5, Controller.proportional(0.3)));
        c.strafeTranslational.set(Controller.piecewise(Controller.proportional(0.1))
                .put(2.5, Controller.proportional(0.3)));

        // How the robot speeds up, coasts and brakes.
        c.coast.set(Controller.proportionalFeedforward(0.010978350889324107));
        c.brake.set(Controller.proportionalFeedforward(0.008731598255925491));
        c.linearBrakeCoefficients.set(Matrix.diag(0.10605894992901523, 0.08719146175596092));
        c.quadraticBrakeCoefficients.set(Matrix.diag(0.0014663966976606565, 0.0013837064502458813));

        // How hard to correct when the robot points the wrong way.
        c.headingFeedback.set(Controller.proportional(5.258721785960744));
        c.headingBrakeCoefficients.set(Vector2D.cartesian(0.05642143125655298, 0.0063829525363003695));

        // Top speeds (inches/second) and how fast it slows down by itself.
        c.maxAchievableForwardVelocity.set(72.72923108818539);
        c.maxAchievableStrafeVelocity.set(52.34323936525474);
        c.naturalForwardDeceleration.set(85.01144677379789);
        c.naturalStrafeDeceleration.set(104.49787535782846);
    });

    /** Builds the Follower : the object that actually drives our paths. */
    public static Follower createFollower(HardwareMap hardwardeMap) {
        return new Follower(
                new PinpointLocalizer(hardwardeMap, localizerConfig),
                new Mecanum(hardwardeMap, drivetrainConfig),
                new Foresight(foresightConfig)
        );
    }
}
