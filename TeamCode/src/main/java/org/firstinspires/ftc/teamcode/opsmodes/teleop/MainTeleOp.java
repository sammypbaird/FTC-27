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
 *   Right bumper (hold) intake in + transfer toward the flywheel
 *   Left bumper (hold)  intake out (the transfer stays off)
 *   A (hold)            spin the flywheel
 *
 * The buttons are "hold-to-run": let go and that part stops.
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

            // Intake and transfer. Hold right bumper to pull pieces in and
            // carry them to the flywheel. Hold left bumper to push pieces out.
            if (gamepad1.right_bumper) {
                intake.in();
                transfer.run();
            } else if (gamepad1.left_bumper) {
                intake.out();
                transfer.stop();   // the transfer only goes toward the flywheel
            } else {
                intake.stop();
                transfer.stop();
            }

            // Flywheel. Hold A to spin it.
            if (gamepad1.a) {
                flywheel.spin();
            } else {
                flywheel.stop();
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
