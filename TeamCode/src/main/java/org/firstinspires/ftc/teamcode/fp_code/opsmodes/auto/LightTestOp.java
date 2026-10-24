package org.firstinspires.ftc.teamcode.fp_code.opsmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "LightTestOp", group = "Test")
public class LightTestOp extends LinearOpMode {

    private Servo indicator;

    @Override
    public void runOpMode() throws InterruptedException {
        // Initializes the servo named "indicator" plugged into Servo Port 0
        indicator = hardwareMap.get(Servo.class, "indicator");

        telemetry.addLine("LightTestOp Ready. Press Start to cycle colors.");
        telemetry.update();

        waitForStart();

        // Different test positions to cycle through the light's color spectrum
        double[] testPositions = {0.0, 0.2, 0.4, 0.6, 0.8, 1.0};
        int index = 0;

        while (opModeIsActive()) {
            double currentPosition = testPositions[index];

            // Change the color by updating the servo position
            indicator.setPosition(currentPosition);

            telemetry.addData("Status", "Cycling Colors...");
            telemetry.addData("Servo Position", currentPosition);
            telemetry.update();

            // Wait 1.5 seconds before moving to the next color
            sleep(1500);

            // Move to the next index, looping back to 0 at the end
            index = (index + 1) % testPositions.length;
        }
    }
}