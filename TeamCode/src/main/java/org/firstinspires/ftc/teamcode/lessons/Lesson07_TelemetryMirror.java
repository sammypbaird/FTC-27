package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/*
 * LESSON 07 · GAMEPAD INPUT — "Telemetry Mirror"
 * -------------------------------------------------------------------------
 * No hardware needed -- just the gamepad and the screen. Hand the controller
 * to a student and project this telemetry. Everyone else calls out a guess
 * for the next number before it updates.
 *
 * Good moments to pause on:
 *   - Push the left stick forward. Y goes NEGATIVE. Get a prediction before
 *     it happens -- almost everyone guesses positive the first time.
 *   - Let go of a stick and stare at it. It never settles at exactly 0.000.
 *     That drift is what deadband filtering exists to clean up.
 *
 * No robot config needed -- this OpMode only reads the gamepad.
 */
@TeleOp(name = "L07 - Telemetry Mirror", group = "Lesson 07 - Gamepad")
public class Lesson07_TelemetryMirror extends LinearOpMode {

    @Override
    public void runOpMode() {
        waitForStart();

        while (opModeIsActive()) {
            telemetry.addLine("--- STICKS (float, -1.0 to 1.0) ---");
            telemetry.addData("left_stick_x",  "%.3f", gamepad1.left_stick_x);
            telemetry.addData("left_stick_y",  "%.3f", gamepad1.left_stick_y);
            telemetry.addData("right_stick_x", "%.3f", gamepad1.right_stick_x);
            telemetry.addData("right_stick_y", "%.3f", gamepad1.right_stick_y);

            telemetry.addLine();
            telemetry.addLine("--- TRIGGERS (float, 0.0 to 1.0) ---");
            telemetry.addData("left_trigger",  "%.3f", gamepad1.left_trigger);
            telemetry.addData("right_trigger", "%.3f", gamepad1.right_trigger);

            telemetry.addLine();
            telemetry.addLine("--- BUTTONS (boolean) ---");
            telemetry.addData("a / b / x / y", "%b / %b / %b / %b",
                    gamepad1.a, gamepad1.b, gamepad1.x, gamepad1.y);
            telemetry.addData("left_bumper / right_bumper", "%b / %b",
                    gamepad1.left_bumper, gamepad1.right_bumper);
            telemetry.addData("dpad up/down/left/right", "%b / %b / %b / %b",
                    gamepad1.dpad_up, gamepad1.dpad_down, gamepad1.dpad_left, gamepad1.dpad_right);
            telemetry.addData("back / start", "%b / %b", gamepad1.back, gamepad1.start);

            telemetry.update();
        }
    }
}
