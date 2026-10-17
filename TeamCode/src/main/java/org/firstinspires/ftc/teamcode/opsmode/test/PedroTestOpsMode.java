package org.firstinspires.ftc.teamcode.opsmode.test;

import com.pedropathing.math.Pose;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedro.Constants;

// Shows where the odometry thinks the robot is. Push the robot by hand.
// Press A to reset back to (0, 0, 0).
@TeleOp(name = "Pedro Test", group = "Test")
public class PedroTestOpsMode extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        //We need only the localizer, no driving, so this works before tuning
        PinpointLocalizer odometry = new PinpointLocalizer(hardwareMap, Constants.localizerConfig);

        telemetry.addLine("Keep the robot still, then press STARTTTTT");
        telemetry.update();

        waitForStart();

        //Start counting from here: (0,0), facing 0 degrees (in radians)
        odometry.setPose(new Pose(0,0,0));

        while (opModeIsActive()) {
            if (gamepad1.aWasPressed()) {
                odometry.setPose(new Pose(0,0,0));
            }

            //Ask the Pinpoint for its latest reading. Do this every loop!! :)
            odometry.update();
            Pose pose = odometry.pose();

            telemetry.addData("X (in)", "%.1f", pose.x());
            telemetry.addData("Y (in)", "%.1f", pose.y());
            //Pedro uses radians, so convert to degrees because degrees are way better than radians, boo tomato unit cirle
            telemetry.addData("Heading (deg)", "%.1f", Math.toDegrees(pose.heading()));
            telemetry.addLine();
            telemetry.addLine("Press A to reset to (0,0,0) :)");
            telemetry.update();
        }

    }
}
