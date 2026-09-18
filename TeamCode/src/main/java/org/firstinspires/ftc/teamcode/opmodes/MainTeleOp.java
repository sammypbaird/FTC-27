package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Driver;

/*
 * MAIN TELEOP -- the OpMode the drivers use during the match
 * -------------------------------------------------------------------------
 * This is the "boss" file. It doesn't know HOW to drive; it just asks the
 * Driver class to do it. Each part of the robot gets its own class (Driver
 * now, later maybe a Shooter and an Intake), and this file uses them all.
 *
 * Right now it only drives. When a new part of the robot is built, add it
 * here the same way Driver is added: create it, use it in the loop, stop it
 * at the end.
 */
@TeleOp(name = "TeleOp", group = "Competition")
public class MainTeleOp extends LinearOpMode {

    // The parts of the robot. We make them empty here and build them inside
    // runOpMode(), because the hardwareMap (the list of robot parts) isn't
    // ready until the OpMode starts running.
    private Driver driver;
    // TODO: add more parts here as they get built
    // private Shooter shooter;
    // private Intake intake;

    @Override
    public void runOpMode() {

        // --- Setup: runs when the driver presses INIT ---
        driver = new Driver(hardwareMap);
        // TODO: shooter = new Shooter(hardwareMap);
        // TODO: intake = new Intake(hardwareMap);

        telemetry.addLine("Ready. Press START.");
        telemetry.update();

        waitForStart();   // wait here until the driver presses START

        // TODO: If something needs to know how much time has passed, make a
        // timer here: ElapsedTime matchTimer = new ElapsedTime();

        // --- Main loop: repeats over and over until the driver presses STOP ---
        while (opModeIsActive()) {

            // Read the joysticks and move the wheels.
            driver.teleopDrive(gamepad1);

            // TODO: controls for the other parts of the robot, maybe on gamepad2

            // Send info to the Driver Station screen.
            driver.addTelemetry(telemetry);
            telemetry.update();
        }

        // --- Cleanup: runs after STOP is pressed. Make sure nothing is still moving. ---
        driver.stop();
        // TODO: shooter.stop();
        // TODO: intake.stop();
    }
}
