package org.firstinspires.ftc.teamcode.opsmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Driver;

@TeleOp(name = "MainTeleOp", group = "Competition")
public class MainTeleOp extends LinearOpMode {

    private Driver driver;

    @Override
    public void runOpMode() throws InterruptedException {

        //Initialization
        driver = new Driver(hardwareMap);
        telemetry.addLine("Ready. Press START.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // Read the joysticks and move the wheels.
            driver.teleopDrive(gamepad1);

            // Send info to the telemetry screen
            driver.addTelemetry(telemetry);
            telemetry.update();
        }

        // Make sure to stop the motors at the end of the mode
        driver.stop();
    }
}
