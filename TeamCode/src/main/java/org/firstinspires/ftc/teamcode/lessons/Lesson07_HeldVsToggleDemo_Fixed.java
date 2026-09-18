package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

/*
 * LESSON 07 · GAMEPAD INPUT — the fixed version
 * -------------------------------------------------------------------------
 * Same OpMode as Lesson07_HeldVsToggleDemo, but with the xPrev/xCurr
 * rising-edge check already applied. Keep this as a fallback: if live-
 * patching the broken version runs long or goes sideways mid-class, stop
 * that OpMode and run this one instead -- the "before vs after" contrast
 * still lands because the class already watched the broken version buzz.
 *
 * Robot config needed: one Servo named exactly "testServo".
 */
@TeleOp(name = "L07 - Held vs Toggle (FIXED)", group = "Lesson 07 - Gamepad")
public class Lesson07_HeldVsToggleDemo_Fixed extends LinearOpMode {

    private static final double POSITION_UP   = 0.2;
    private static final double POSITION_DOWN = 0.8;

    private boolean xPrev = false;

    @Override
    public void runOpMode() {
        Servo testServo = hardwareMap.get(Servo.class, "testServo");

        boolean isUp = true;
        testServo.setPosition(POSITION_UP);

        telemetry.addLine("Ready. Right bumper = slow mode (held). X = toggle servo (edge-detected, fixed).");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // ---------- HELD pattern ----------
            boolean slowMode = gamepad1.right_bumper;
            double driveScale = slowMode ? 0.5 : 1.0;

            // ---------- TOGGLE pattern: fixed with rising-edge detection ----------
            boolean xCurr = gamepad1.x;
            if (xCurr && !xPrev) {
                isUp = !isUp;
                testServo.setPosition(isUp ? POSITION_UP : POSITION_DOWN);
            }
            xPrev = xCurr;

            telemetry.addData("right_bumper (held)", slowMode);
            telemetry.addData("Drive scale", "%.1fx", driveScale);
            telemetry.addLine();
            telemetry.addData("x (raw, every loop)", gamepad1.x);
            telemetry.addData("Servo state", isUp ? "UP" : "DOWN");
            telemetry.addLine();
            telemetry.addLine("Hold X as long as you want -- it toggles exactly once per press now.");
            telemetry.update();
        }
    }
}
