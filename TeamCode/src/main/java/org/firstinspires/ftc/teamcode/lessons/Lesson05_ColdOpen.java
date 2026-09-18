package org.firstinspires.ftc.teamcode.lessons;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;

/*
 * LESSON 05 · MOTORS — "The Silent Spin" cold open
 * -------------------------------------------------
 * Classroom use: run this BEFORE students see any code. Init, start, let the
 * motor spin for a few seconds, say nothing. Ask the class what line of code
 * just ran -- THEN open this file and show them. The whole reveal is one line.
 *
 * Robot config needed: one DC Motor named exactly "testMotor" on any port.
 */
@TeleOp(name = "L05 - Cold Open")
public class Lesson05_ColdOpen extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor testMotor = hardwareMap.get(DcMotor.class, "pinkfloyd");

        waitForStart();

        testMotor.setDirection(Direction.REVERSE);
        testMotor.setPower(0.1);

        sleep(3000);
        testMotor.setPower(0.0);
    }
}
