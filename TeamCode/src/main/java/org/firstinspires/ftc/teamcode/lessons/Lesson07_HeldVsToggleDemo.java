package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

/*
 * LESSON 07 · GAMEPAD INPUT — "Make It Buzz" + "Held or Toggled?"
 * -------------------------------------------------------------------------
 * Two patterns living side by side on purpose:
 *
 *   RIGHT BUMPER (held)   Correct as written. Reads gamepad1.right_bumper
 *                         directly every loop -- no edge detection needed,
 *                         because the behavior SHOULD track "is it down
 *                         right now", exactly like slow mode on a real robot.
 *
 *   X BUTTON (toggle)     BROKEN ON PURPOSE. Reads gamepad1.x raw, with no
 *                         rising-edge check, so holding it makes the servo
 *                         flicker between positions instead of toggling
 *                         cleanly once. That's the demo.
 *
 * THE LIVE FIX: once the class has watched it buzz, delete the "BROKEN"
 * block below and uncomment the "FIXED" block underneath it -- typing that
 * live in front of them is the payoff. If live-typing runs long or goes
 * sideways, stop this OpMode and run Lesson07_HeldVsToggleDemo_Fixed instead
 * -- same file, already working.
 *
 * Robot config needed: one Servo named exactly "testServo" (Lesson 06's
 * servo works fine, still wired up).
 */
@TeleOp(name = "L07 - Held vs Toggle", group = "Lesson 07 - Gamepad")
public class Lesson07_HeldVsToggleDemo extends LinearOpMode {

    private static final double POSITION_UP   = 0.2;
    private static final double POSITION_DOWN = 0.8;

    @Override
    public void runOpMode() {
        Servo testServo = hardwareMap.get(Servo.class, "testServo");

        boolean isUp = true;
        testServo.setPosition(POSITION_UP);

        telemetry.addLine("Ready. Right bumper = slow mode (held). X = toggle servo (broken on purpose).");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // ---------- HELD pattern: correct as-is ----------
            boolean slowMode = gamepad1.right_bumper; // read fresh every loop -- that's the point
            double driveScale = slowMode ? 0.5 : 1.0;

            // ---------- TOGGLE pattern: BROKEN ON PURPOSE ----------
            if (gamepad1.x) {
                isUp = !isUp;
                testServo.setPosition(isUp ? POSITION_UP : POSITION_DOWN);
            }

            /* THE FIX -- delete the block above and uncomment this one live:

            boolean xCurr = gamepad1.x;
            if (xCurr && !xPrev) {              // rising edge only
                isUp = !isUp;
                testServo.setPosition(isUp ? POSITION_UP : POSITION_DOWN);
            }
            xPrev = xCurr;

            // and add this field above runOpMode(): private boolean xPrev = false;
            */

            telemetry.addData("right_bumper (held)", slowMode);
            telemetry.addData("Drive scale", "%.1fx", driveScale);
            telemetry.addLine();
            telemetry.addData("x (raw, every loop)", gamepad1.x);
            telemetry.addData("Servo state", isUp ? "UP" : "DOWN");
            telemetry.addLine();
            telemetry.addLine(gamepad1.x
                    ? "Holding X? Watch the servo -- it's flickering, not toggling."
                    : "Release, then tap X once. Hold it next time and watch closely.");
            telemetry.update();
        }
    }
}
