package org.firstinspires.ftc.teamcode.finn.opsmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.finn.subsystems.Driver;

@TeleOp(name = "Finn - MainTeleOp", group = "Competition")
public class MainTeleOp extends LinearOpMode {
    private Driver driver;

    @Override
    public void runOpMode() throws InterruptedException {
        //TODO initialization code
        driver = new Driver(hardwareMap);
        telemetry.addLine("Ready Press Start");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()){
            driver.teleopDrive(gamepad1);
            driver.addTelemetry(telemetry);
            telemetry.update();
        }
        driver.stop();
    }
}
