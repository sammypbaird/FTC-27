package org.firstinspires.ftc.teamcode.opsmodes.auto;

import static com.pedropathing.ivy.groups.Groups.sequential;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import static com.pedropathing.api.Paths.line;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Driver;
import org.firstinspires.ftc.teamcode.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Transfer;

@Autonomous(name = "Auto")
public class AutoOp extends LinearOpMode {

    private final Pose start = new Pose(0,0,0);
    private final Pose end = new Pose(24, 0, 0);

    @Override
    public void runOpMode() throws InterruptedException {
        Follower follower = Constants.createFollower(hardwareMap);
        AutoMoves moves = new AutoMoves(follower);

        // THE PLAN: one move after another.
        // ".constant(heading)" keeps the robot facing one way for the whole path.
        Command auto = sequential(
                moves.drive(line(start, end).constant(0)),
                moves.hold(end)
        );

        telemetry.addLine("Ready. Press START");
        telemetry.update();
        waitForStart();

        follower.setPose(start);
        Scheduler.reset();
        Scheduler.schedule(auto);

        while (opModeIsActive()) {
            follower.update(); // Pedro updates the odometry readings
            Scheduler.execute(); // Ivy: run the current command, move on when it's done

            Pose pose = follower.pose();
            telemetry.addData("X (in)", "%.1f", pose.x());
            telemetry.addData("Y (in)", "%.1f", pose.y());
            telemetry.addData("Heading (deg)", "%.1f", Math.toDegrees(pose.heading()));
            telemetry.update();
        }

        follower.stop();
    }
}
