package org.firstinspires.ftc.teamcode.opsmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Driver;
import org.firstinspires.ftc.teamcode.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Transfer;

/*
 * MAIN TELEOP -- what the drivers use in a match
 * -------------------------------------------------------------------------
 * Each subsystem only knows how to run its own motors. THIS file decides
 * which button does what, so all the controls are in one place:
 *
 *   Left stick          drive and strafe
 *   Right stick (x)     turn
 *   X (press)           slow mode on/off
 *   Right bumper (hold) intake in (the transfer stays off)
 *   Left bumper (hold)  intake out + transfer out, to push pieces back out
 *   A (hold)            spin the flywheel + transfer in, to launch
 *
 * The buttons are "hold-to-run": let go and that part stops.
 * If the left bumper and A are both held, pushing out wins for the transfer.
 */
@TeleOp(name = "MainTeleOp")
public class MainTeleOp extends LinearOpMode {

    private Driver driver;
    private Intake intake;
    private Transfer transfer;
    private Flywheel flywheel;

    @Override
    public void runOpMode() throws InterruptedException {

        //Initialization
        driver = new Driver(hardwareMap);
        intake = new Intake(hardwareMap);
        transfer = new Transfer(hardwareMap);
        flywheel = new Flywheel(hardwareMap);
        telemetry.addLine("Ready. Press START.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // Read the joysticks and move the wheels.
            driver.teleopDrive(gamepad1);

            // Intake. Hold right bumper to pull pieces in, left bumper to push them out.
            if (gamepad1.right_bumper) {
                intake.in();
            } else if (gamepad1.left_bumper) {
                intake.out();
            } else {
                intake.stop();
            }

            // Flywheel. Hold A to spin it.
            if (gamepad1.a) {
                flywheel.spin();
            } else {
                flywheel.stop();
            }

            // Transfer. Two buttons use it, so ONE if/else decides what it does.
            // (If each button set the transfer on its own, the second one would
            // undo the first in the same loop.)
            if (gamepad1.left_bumper) {
                transfer.out();   // pushing out: carry pieces back to the intake
            } else if (gamepad1.a) {
                transfer.run();   // launching: feed pieces into the flywheel
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
    }
}
