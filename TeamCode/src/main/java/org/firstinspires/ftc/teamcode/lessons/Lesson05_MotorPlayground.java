package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

/*
 * LESSON 05 · MOTORS — bench sandbox
 * -------------------------------------------------------------------------
 * One OpMode, four Bench Rundown blocks:
 *   "DcMotor vs DcMotorEx + setPower()"  -> hold Y, the live clamp test
 *   "Two People, One Wheel"              -> D-pad left/right, direction
 *   "Feel the Brake"                     -> A / B, zero power behavior
 *   "Count the Ticks"                    -> always-on encoder telemetry
 *
 * Robot config needed: one DC Motor named exactly "testMotor" on any port.
 *
 * CONTROLS
 *   Right trigger   spin forward, proportional to how far it's pulled
 *   Left trigger    spin backward, proportional to how far it's pulled
 *   Y (hold)        commands setPower(2.5) -- watch it clamp to 1.0
 *   D-pad right     direction = FORWARD
 *   D-pad left      direction = REVERSE
 *   A               zero power behavior = BRAKE
 *   B               zero power behavior = FLOAT
 *
 * Direction and zero-power-behavior are set directly by a button, not
 * toggled, on purpose -- edge detection isn't taught until Lesson 07, so
 * this file doesn't need it to stay simple and reliable live.
 */
@Disabled
@TeleOp(name = "L05 - Motor Playground", group = "Lesson 05 - Motors")
public class Lesson05_MotorPlayground extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotorEx testMotor = hardwareMap.get(DcMotorEx.class, "pinkfloyd");
        testMotor.setDirection(DcMotor.Direction.FORWARD);
        testMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addLine("Ready. Triggers drive it, Y proves the clamp, D-pad sets direction, A/B set brake/float.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // --- direction: explicit per button, never ambiguous mid-demo ---
            if (gamepad1.dpad_right) testMotor.setDirection(DcMotor.Direction.FORWARD);
            if (gamepad1.dpad_left)  testMotor.setDirection(DcMotor.Direction.REVERSE);

            // --- zero power behavior: same reasoning ---
            if (gamepad1.a) testMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            if (gamepad1.b) testMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

            // --- power ---
            double commandedPower;
            if (gamepad1.y) {
                commandedPower = 2.5;   // out of range on purpose -- the clamp test
            } else {
                commandedPower = gamepad1.right_trigger - gamepad1.left_trigger;
            }
            testMotor.setPower(commandedPower);

            telemetry.addData("Commanded power", "%.2f", commandedPower);
            telemetry.addData("Actual power (SDK clamps to +/-1.0)", "%.2f", testMotor.getPower());
            telemetry.addData("Direction", testMotor.getDirection());
            telemetry.addData("Zero power behavior", testMotor.getZeroPowerBehavior());
            telemetry.addLine();
            telemetry.addData("Encoder ticks", testMotor.getCurrentPosition());
            telemetry.addData("Velocity (ticks/s)", "%.1f", testMotor.getVelocity());
            telemetry.addLine();
            telemetry.addLine("Triggers = power | Y = try 2.5 | D-pad = direction | A/B = brake/float");
            telemetry.update();
        }
    }
}
