package org.firstinspires.ftc.teamcode.opsmodes.auto;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.groups.Groups.sequential;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Transfer;

@Autonomous(name = "Auto Shoot", preselectTeleOp = "MainTeleOp")
public class AutoShoot extends LinearOpMode {

    //how long each shooting step lasts, in milliseconds.
    private static final double INTAKE_MS = 2000;

    //The parking path
    // !!! PLACEHOLDERS: measure these on the field with Pinpoint Check!! :)
    private final Pose start = new Pose(0,0,0);
    private final Pose shootTurn = new Pose(3.9, 2, Math.toRadians(18.2));
    private final Pose preCollect = new Pose(6, 50, 0);
    private final Pose collecting = new Pose(0, 50, 0);
    private final Pose turnSpot = new Pose(114,0,0);
    private final Pose park = new Pose(114, 47, 0);

    @Override
    public void runOpMode() throws InterruptedException {
        Follower follower = Constants.createFollower(hardwareMap);
        Flywheel flywheel = new Flywheel(hardwareMap);
        Transfer transfer = new Transfer(hardwareMap);
        Intake intake = new Intake(hardwareMap);
        IvyCommands commands = new IvyCommands(follower, flywheel,transfer, intake);

        //THE PLANNNNN: one step after another
        Command auto = sequential(
                commands.hold(start),
                commands.startFlywheel(0),
                commands.turn(shootTurn),
                commands.shootSequence(),

                commands.drive(line(shootTurn, preCollect)),
                commands.intakeOn(),
                commands.drive(line(preCollect, collecting)),
                waitMs(INTAKE_MS),

                commands.drive(line(collecting, shootTurn)),
                commands.shootSequence(),
                commands.drive(line(collecting, turnSpot)),
                commands.drive(line(turnSpot, park).constant(Math.toRadians(0))),
                commands.hold(park)
        );

        telemetry.addLine("Ready. Press START.");
        telemetry.update();
        waitForStart();

        ElapsedTime timer = new ElapsedTime();
        follower.setPose(start);
        Scheduler.reset();
        Scheduler.schedule(auto);

        while (opModeIsActive()){
            follower.update(); // Pedro: read the odometry and set the motor powers
            Scheduler.execute(); // Ivy: run the current step, move on when it's done

            Pose pose = follower.pose();
            telemetry.addData("Time (s)", "%.1f", timer.seconds());
            flywheel.addTelemetry(telemetry);
            transfer.addTelemetry(telemetry);
            telemetry.addData("X (in)", "%.1f", pose.x());
            telemetry.addData("Y (in)", "%.1f", pose.y());
            telemetry.addData("Heading (deg)", "%.1f", Math.toDegrees(pose.heading()));
            telemetry.update();
        }

        //AUTO is overrrrr, make sure everything is stopped
        follower.stop();
        flywheel.stop();
        transfer.stop();

    }
}
