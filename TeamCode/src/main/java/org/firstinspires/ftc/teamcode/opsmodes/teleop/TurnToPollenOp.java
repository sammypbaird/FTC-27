package org.firstinspires.ftc.teamcode.opsmodes.teleop;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.subsystems.Driver;

@TeleOp(name = "Turn to Pollen", group = "Test")
public class TurnToPollenOp extends LinearOpMode {

    // Ignore detections the model isn't sure about
    private static final double MIN_CONFIDENCE = 0.5;

    private static final double TURN_GAIN = 0.02;

    private static final double MAX_TURN = 0.3;

    @Override
    public void runOpMode() throws InterruptedException {
        Driver driver = new Driver(hardwareMap);
        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");

        limelight.pipelineSwitch(1);
        limelight.start();

        telemetry.addLine("Ready. Hold A to turn toward pollen");
        telemetry.update();

        waitForStart();
        while (opModeIsActive()) {
            LLResult result = limelight.getLatestResult();
            LLResultTypes.DetectorResult pollen = findClosestPollen(result);

            if (gamepad1.a) {
                if (pollen != null) {
                    double tx = pollen.getTargetXDegrees();
                    double yaw = Range.clip(TURN_GAIN * tx, -MAX_TURN, MAX_TURN);
                    driver.drive(0, 0, yaw);
                    telemetry.addData("tx", "%.1f°", tx);
                    telemetry.addData("turn power", "%.2f", yaw);
                }
                else {
                    driver.stop();
                    telemetry.addLine("No pollen seen");
                }
            }
            else {
                driver.teleopDrive(gamepad1);
            }

            telemetry.addData("pollen seen", pollen != null);
        }
    }

    private LLResultTypes.DetectorResult findClosestPollen(LLResult result) {
        if (result == null || !result.isValid())
            return null;
        LLResultTypes.DetectorResult best = null;
        for (LLResultTypes.DetectorResult d : result.getDetectorResults()) {
            if (!d.getClassName().equals("pollen"))
                continue;
            if (d.getConfidence() < MIN_CONFIDENCE)
                continue;
            if (best == null || d.getTargetArea() > best.getTargetArea())
                best = d;
        }
        return best;
    }
}
