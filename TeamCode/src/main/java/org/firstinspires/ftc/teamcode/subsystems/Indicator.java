package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

/*
 * This class is in charge of the RGB indicator light: a light the drivers
 * can see from across the field, to show what the robot is doing.
 *
 * The goBILDA RGB Indicator Light plugs into a SERVO port. We pick its color
 * by setting a servo position. Each position makes a different signal
 * (pulse width), and the light turns each pulse width into a color:
 *
 *   Off 0.0   Red 0.277   Orange 0.333   Yellow 0.388   Sage 0.444
 *   Green 0.5   Azure 0.555   Blue 0.611   Indigo 0.666   Violet 0.722   White 1.0
 */
public class Indicator {

    private static final double OFF = 0.0;
    private static final double GREEN = 0.5;

    private final Servo light;

    public Indicator(HardwareMap hardwareMap) {
        // On servo port 0. The name must match the robot configuration.
        light = hardwareMap.get(Servo.class, "indicator");
        off();
    }

    /** Turn the light green. */
    public void green() {
        light.setPosition(GREEN);
    }

    /** Turn the light off. */
    public void off() {
        light.setPosition(OFF);
    }
}
