package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.pedropathing.tuning.autotune.Procedure;
import com.pedropathing.tuning.autotune.Tuner;

import org.firstinspires.ftc.teamcode.pedro.procedures.ForesightTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.MecanumTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.PinpointTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.Tests;

/*
 * TUNING -- the Pedro tuners for our robot
 * -------------------------------------------------------------------------
 * Pedro's "AutoTune" runs on the robot and shows a web page that walks you
 * through measuring the numbers in Constants.java. Each method below adds
 * one tuner to that page.
 *
 * Run them in this order:
 *   1. Mecanum Tuner   -- checks the motor names and directions
 *   2. Pinpoint Tuner  -- checks the pod directions and measures the pod offsets
 *   3. Foresight Tuner -- measures how the robot speeds up, brakes and turns
 *   4. Tests           -- try a straight line, a curve, and holding still
 *
 * When a tuner finishes, it prints code. Paste it into Constants.java.
 *
 * Tuning only: tuning tools that stream data aren't allowed during matches.
 */
public class Tuning {

    @Tuner
    public static Procedure mecanumTuner() {
        return new MecanumTuner();
    }

    @Tuner
    public static Procedure pinpointTuner() {
        return new PinpointTuner();
    }

    @Tuner
    public static Procedure foresightTuner() {
        return new ForesightTuner(
                hardwareMap -> new PinpointLocalizer(hardwareMap, Constants.localizerConfig),
                hardwareMap -> new Mecanum(hardwareMap, Constants.drivetrainConfig));
    }

    @Tuner
    public static Procedure tests() {
        return new Tests(
                hardwareMap -> new Mecanum(hardwareMap, Constants.drivetrainConfig),
                hardwareMap -> new PinpointLocalizer(hardwareMap, Constants.localizerConfig),
                () -> new Foresight(Constants.foresightConfig));
    }
}
