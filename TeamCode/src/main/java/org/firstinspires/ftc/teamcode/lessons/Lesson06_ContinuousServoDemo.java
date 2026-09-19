package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

/*
 * LESSON 06 · SERVOS — "When Position Means Speed"
 * -------------------------------------------------------------------------
 * A continuous rotation servo looks identical to a standard servo, but
 * setPosition() means SPEED + DIRECTION here, not angle. Same method,
 * opposite meaning -- that contrast is the whole point of this file.
 *
 * No continuous servo on the bench? This still works as a screen-only
 * walkthrough: run it, point at the position value, and read the telemetry
 * description out loud instead of watching anything spin.
 *
 * Robot config needed: one Servo named exactly "testContinuousServo" (a
 * separate config entry/port from Lesson 06's "testServo" -- it can be a
 * regular Servo entry, the SDK doesn't distinguish continuous from standard).
 * Starts at 0.5 (stopped) immediately so it never surprise-spins on init.
 *
 * CONTROLS
 *   A (hold)   full speed, one direction
 *   B (hold)   full speed, the other direction
 *   (neither)  stopped
 */
@TeleOp(name = "L06 - Continuous Servo", group = "Lesson 06 - Servos")
@Disabled
public class Lesson06_ContinuousServoDemo extends LinearOpMode {

    @Override
    public void runOpMode() {
        Servo continuousServo = hardwareMap.get(Servo.class, "testContinuousServo");
        continuousServo.setPosition(0.5); // 0.5 = stopped

        telemetry.addLine("Ready. Hold A = full speed one way, B = full speed the other way, neither = stop.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            double position;
            String description;

            if (gamepad1.a) {
                position = 1.0;
                description = "1.0 -> full speed, direction A";
            } else if (gamepad1.b) {
                position = 0.0;
                description = "0.0 -> full speed, direction B";
            } else {
                position = 0.5;
                description = "0.5 -> stopped (the neutral point)";
            }

            continuousServo.setPosition(position);

            telemetry.addData("setPosition() value", "%.2f", position);
            telemetry.addData("What it means here", description);
            telemetry.addLine();
            telemetry.addLine("Same method as a standard servo. Completely different meaning.");
            telemetry.update();
        }
    }
}
