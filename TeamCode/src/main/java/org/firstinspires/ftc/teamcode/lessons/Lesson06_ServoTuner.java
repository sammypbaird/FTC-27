package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

/*
 * LESSON 06 · SERVOS — tuning race + "plant the flag"
 * -------------------------------------------------------------------------
 * Supports:
 *   "Tuning Race"               -> D-pad up/down nudges position by 0.01
 *   "Plant the Flag"            -> set a position at the start of class,
 *                                  walk away, come back to it later -- it
 *                                  will still be exactly there
 *   "The Mirror-Mount Puzzle"   -> physically flip the servo body while this
 *                                  runs and watch the SAME number produce
 *                                  the opposite physical motion
 *
 * Robot config needed: one Servo named exactly "testServo" on any port.
 *
 * CONTROLS
 *   D-pad up      position += 0.01
 *   D-pad down    position -= 0.01
 */
@Disabled
@TeleOp(name = "L06 - Servo Tuner", group = "Lesson 06 - Servos")
public class Lesson06_ServoTuner extends LinearOpMode {

    @Override
    public void runOpMode() {
        Servo testServo = hardwareMap.get(Servo.class, "testServo");

        double pos = 0.5;
        testServo.setPosition(pos);

        telemetry.addLine("Ready. D-pad up/down nudges position by 0.01. Mark your target with tape!");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.dpad_up)   pos += 0.01;
            if (gamepad1.dpad_down) pos -= 0.01;
            pos = Math.max(0.0, Math.min(1.0, pos)); // clamp to the servo's valid range

            testServo.setPosition(pos);

            telemetry.addData("Position", "%.3f", pos);
            telemetry.addLine("D-pad up/down to nudge. Write the number down once it looks right.");
            telemetry.update();

            sleep(100); // slow the repeat rate so each press moves a clean 0.01
        }
    }
}
