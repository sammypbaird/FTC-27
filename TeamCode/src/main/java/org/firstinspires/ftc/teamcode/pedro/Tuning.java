package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.pedropathing.tuning.autotune.Procedure;
import com.pedropathing.tuning.autotune.Tuner;
import com.pedropathing.tuning.autotune.TunerScanner;

import org.firstinspires.ftc.teamcode.pedro.procedures.ForesightTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.MecanumTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.PinpointTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.Tests;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

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

    /*
     * WORKAROUND for a bug in Pedro's tuning library (version 1.0.2)
     * -------------------------------------------------------------------------
     * When Pedro finds our first @Tuner method, it tries to add it to a list
     * that doesn't exist yet, and the app crashes with:
     *   NullPointerException ... List.add(Object) ... TunerScanner.scan
     *
     * A "static" block runs once, the first time this class is used. Pedro
     * uses this class (by calling a @Tuner method) just BEFORE the line that
     * crashes, so this block runs first and creates that missing list for it.
     *
     * Students don't need to understand how this works (it uses "reflection",
     * which lets code reach inside another class). If Pedro fixes the bug in
     * a newer version, this block does nothing and can be deleted.
     */
    static {
        try {
            Object scanner = TunerScanner.INSTANCE;
            Field field = TunerScanner.class.getDeclaredField("proceduresByLoader");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<ClassLoader, Object> listsByLoader = (Map<ClassLoader, Object>) field.get(scanner);

            // Make an empty list for every class loader Pedro might be using.
            for (ClassLoader loader = Tuning.class.getClassLoader(); loader != null; loader = loader.getParent()) {
                listsByLoader.putIfAbsent(loader, new ArrayList<>());
            }
            ClassLoader threadLoader = Thread.currentThread().getContextClassLoader();
            if (threadLoader != null) {
                listsByLoader.putIfAbsent(threadLoader, new ArrayList<>());
            }
        } catch (Exception e) {
            // Pedro changed its code (maybe it fixed the bug), so skip the workaround.
        }
    }

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
