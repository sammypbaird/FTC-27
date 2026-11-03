package org.firstinspires.ftc.teamcode.opsmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Driver;
import org.firstinspires.ftc.teamcode.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.subsystems.Indicator;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Transfer;

/*
 * MAIN TELEOP -- what the drivers use in a match
 * -------------------------------------------------------------------------
 * Each subsystem only knows how to run its own motors. THIS file decides
 * which button does what, so all the controls are in one place:
 *
 * GAMEPAD 1 (driver) -- driving is handled in Driver.teleopDrive()
 *   Left stick          drive and strafe
 *   Right stick (x)     turn
 *   D-pad (hold)        slow drive (up/down) and slow strafe (left/right)
 *   Triggers            slow turn left / right (press harder = faster, max 50%)
 *   X (press)           slow mode on/off
 *
 * GAMEPAD 2 (operator)
 *   Right bumper (hold) intake in (the transfer stays off)
 *   Left bumper (hold)  intake out + transfer out, to push pieces back out
 *   A (hold)            spin the flywheel + transfer in, to launch
 *   D-pad up (press)    flywheel power +10% (max 100%)
 *   D-pad down (press)  flywheel power -10% (min 10%)
 *
 * LIGHT: the RGB indicator is green while shooting (A held) and red while
 * pushing pieces out (left bumper held).
 *
 * The bumpers and A are "hold-to-run": let go and that part stops.
 * If the left bumper and A are both held, pushing out wins for the transfer.
 */
@TeleOp(name = "MainTeleOp")
public class MainTeleOp extends LinearOpMode {

    private Driver driver;
    private Intake intake;
    private Transfer transfer;
    private Flywheel flywheel;
    private Indicator indicator;

    @Override
    public void runOpMode() throws InterruptedException {

        //Initialization
        driver = new Driver(hardwareMap);
        intake = new Intake(hardwareMap);
        transfer = new Transfer(hardwareMap);
        flywheel = new Flywheel(hardwareMap);
        indicator = new Indicator(hardwareMap);
        telemetry.addLine("Ready. Press START.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // Read the joysticks and move the wheels.
            driver.teleopDrive(gamepad1);

            // Intake. Hold right bumper to pull pieces in, left bumper to push them out.
            if (gamepad2.right_bumper) {
                intake.in();
            } else if (gamepad2.left_bumper) {
                intake.out();
            } else {
                intake.stop();
            }

            // Flywheel power. Each PRESS of the d-pad changes it by 10%.
            // (WasPressed is true for only one loop per press, so holding the
            // button down doesn't keep changing it.)
            if (gamepad2.dpadUpWasPressed()) {
                flywheel.increasePower();
            }
            if (gamepad2.dpadDownWasPressed()) {
                flywheel.decreasePower();
            }

            // Flywheel. Hold A to spin it.
            if (gamepad2.a) {
                flywheel.spin();
            } else {
                flywheel.stop();
            }

            // Indicator light. Same order as the transfer below, so the light
            // always matches what the transfer is doing.
            if (gamepad2.left_bumper) {
                indicator.red();      // pushing pieces out
            } else if (gamepad2.a) {
                indicator.green();    // shooting
            } else {
                indicator.off();
            }

            // Transfer. Two buttons use it, so ONE if/else decides what it does.
            // (If each button set the transfer on its own, the second one would
            // undo the first in the same loop.)
            if (gamepad2.left_bumper) {
                transfer.backward();   // pushing out: carry pieces back to the intake
            } else if (gamepad2.a) {
                transfer.forward();   // launching: feed pieces into the flywheel
            } else {
                transfer.stop();
            }

            // Send info to the telemetry screen
            driver.addTelemetry(telemetry);
            intake.addTelemetry(telemetry);
            transfer.addTelemetry(telemetry);
            flywheel.addTelemetry(telemetry);
            telemetry.update();
        }

        // Make sure to stop the motors at the end of the mode
        driver.stop();
        intake.stop();
        transfer.stop();
        flywheel.stop();
        indicator.off();
    }
}
