package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/*
 * DRIVER -- the part of the robot that makes it move around the field
 * -------------------------------------------------------------------------
 * This class is in charge of the 4 wheel motors and the IMU (the sensor
 * that knows which way the robot is facing). Every other file should ask
 * the Driver to move the robot instead of touching the wheel motors itself.
 * That way, all the driving code lives in ONE place.
 *
 * This is a MECANUM drive. Mecanum wheels let the robot slide sideways
 * (strafe) as well as go forward, backward, and turn.
 *
 * The class has TWO groups of methods:
 *
 *   1. Basic drive commands   -> drive(), driveFieldCentric(), stop()
 *      You give these three numbers (forward, sideways, turn) and they
 *      spin the wheels. Both TeleOp AND Autonomous can use these.
 *
 *   2. TeleOp helper          -> teleopDrive(gamepad)
 *      Reads the driver's joysticks and buttons, cleans up the numbers,
 *      then calls the basic drive commands. Only TeleOp uses this one.
 *
 * YOUR JOB: fill in every TODO. Start from the top of the file and work
 * down. Test each piece on the robot (up on blocks!) before moving on.
 *
 * HOW TO USE IT (inside an OpMode's runOpMode method)
 *   Driver driver = new Driver(hardwareMap);
 *   waitForStart();
 *   while (opModeIsActive()) {
 *       driver.teleopDrive(gamepad1);
 *   }
 *   driver.stop();
 */
public class Driver {

    // ---------------------------------------------------------------------
    // TODO: Check these names against the robot's configuration on the
    // Driver Station. They have to match EXACTLY, including capital letters.
    // If one is wrong, the robot will crash when you press INIT with a
    // "device not found" error.
    // ---------------------------------------------------------------------
    private static final String FRONT_LEFT_NAME  = "frontLeft";
    private static final String FRONT_RIGHT_NAME = "frontRight";
    private static final String BACK_LEFT_NAME   = "backLeft";
    private static final String BACK_RIGHT_NAME  = "backRight";
    private static final String IMU_NAME         = "imu";

    // ---------------------------------------------------------------------
    // Numbers we can adjust ("tuning"). Try changing them on the real robot
    // and see what feels best to drive.
    // ---------------------------------------------------------------------
    private static final double STICK_DEADBAND = 0.05;   // ignore tiny joystick wiggles
    private static final double SLOW_MODE_SCALE = 0.4;   // slow mode = 40% of full speed
    private static final double STRAFE_CORRECTION = 1.1; // sideways is weaker than forward, so boost it a bit

    // The four wheel motors and the IMU. "final" means we set them once (in
    // the constructor below) and they never change after that.
    private final DcMotorEx frontLeft;
    private final DcMotorEx frontRight;
    private final DcMotorEx backLeft;
    private final DcMotorEx backRight;
    private final IMU imu;

    // Two driving "modes" the driver can turn on and off.
    //   slowMode:     drive slower so it's easier to line things up
    //   fieldCentric: "forward" always means the same direction on the
    //                 field, no matter which way the robot is pointing
    private boolean slowMode = false;
    private boolean fieldCentric = false;

    // TODO: If a button will TOGGLE a mode (press once = on, press again =
    // off), you need a variable here that remembers whether the button was
    // already pressed last loop. Otherwise the mode flips on and off many
    // times a second while the button is held down. You practiced this
    // in Lesson 07 (held vs. toggle).

    /**
     * The constructor. It runs once when we write "new Driver(hardwareMap)".
     * It finds the motors and IMU and gets them ready.
     *
     * Build the Driver INSIDE runOpMode(). The hardwareMap doesn't exist
     * yet when an OpMode's fields are first created, so it can't be done
     * up there.
     *
     * The robot must NOT move here. Only set things up.
     */
    public Driver(HardwareMap hardwareMap) {
        // Find each device by its configuration name.
        frontLeft = hardwareMap.get(DcMotorEx.class, FRONT_LEFT_NAME);
        frontRight = hardwareMap.get(DcMotorEx.class, FRONT_RIGHT_NAME);
        backLeft = hardwareMap.get(DcMotorEx.class, BACK_LEFT_NAME);
        backRight = hardwareMap.get(DcMotorEx.class, BACK_RIGHT_NAME);
        imu = hardwareMap.get(IMU.class, IMU_NAME);

        // TODO: Set each motor's direction so that POSITIVE power drives the
        //       robot FORWARD. The motors on the left side are mounted
        //       facing the opposite way from the right side, so one side
        //       usually needs REVERSE. Test one wheel at a time.
        // TODO: Set the zero power behavior to BRAKE, so the robot stops
        //       quickly when the driver lets go of the stick (instead of
        //       coasting).
        // TODO: Set the run mode. RUN_WITHOUT_ENCODER is a good start: it
        //       means "the power number I give you is the power you use."
        // TODO: Set up the IMU. It needs to know how the Control Hub is
        //       mounted on the robot (which way the logo faces and which way
        //       the USB ports face). Look up IMU.Parameters and
        //       RevHubOrientationOnRobot.
        // TODO: Reset the heading so the way the robot is facing right now
        //       counts as "0" (straight ahead).
    }

    // ---------------------------------------------------------------------
    // Group 1: basic drive commands (TeleOp and Autonomous both use these)
    // ---------------------------------------------------------------------

    /**
     * Drive the robot relative to ITSELF ("robot-centric"): forward means
     * the direction the front of the robot is facing.
     *
     * All three numbers go from -1 to +1.
     *
     * @param axial   forward / backward.  +1 = full speed forward
     * @param lateral left / right.        +1 = slide to the right
     * @param yaw     turning.             +1 = spin clockwise
     */
    public void drive(double axial, double lateral, double yaw) {
        // TODO: Multiply lateral by STRAFE_CORRECTION.
        // TODO: Work out the power for each wheel. This is the "mecanum
        //       mix" -- each wheel gets a different blend of the three inputs:
        //         frontLeft  = axial + lateral + yaw
        //         frontRight = axial - lateral - yaw
        //         backLeft   = axial - lateral + yaw
        //         backRight  = axial + lateral - yaw
        // TODO: A motor can't go above 1.0 power. Adding three numbers can
        //       give you 2.0 or more! Find the biggest wheel power (ignore
        //       the plus/minus sign). If it's bigger than 1, divide ALL four
        //       wheel powers by it. That keeps the wheels in the right
        //       proportion to each other so the robot still goes where you
        //       aimed.
        // TODO: If slowMode is on, multiply all four powers by SLOW_MODE_SCALE.
        // TODO: Call setPower() on each of the four motors.
    }

    /**
     * Drive relative to the FIELD ("field-centric"). Push the stick "up"
     * and the robot goes toward the far end of the field, even if the robot
     * is turned sideways. Many drivers find this easier to think about.
     *
     * It works by rotating the joystick direction by the robot's heading,
     * then using the regular drive().
     *
     * @param heading which way the robot is facing, in radians (from getHeading()).
     *                It goes UP when the robot turns left (counterclockwise).
     *                That's the opposite of the yaw input, where + is clockwise,
     *                so if the robot drives the wrong way, check this first.
     */
    public void driveFieldCentric(double axial, double lateral, double yaw, double heading) {
        // TODO: Rotate the (lateral, axial) direction backwards by the heading:
        //         rotLateral = lateral * cos(-heading) - axial * sin(-heading)
        //         rotAxial   = lateral * sin(-heading) + axial * cos(-heading)
        //       (Use Math.cos and Math.sin.)
        // TODO: Call drive(rotAxial, rotLateral, yaw)
    }

    /** Stop all four wheels. Call this when the OpMode ends. */
    public void stop() {
        // TODO: Set the power of all four motors to 0.
    }

    // ---------------------------------------------------------------------
    // Group 2: TeleOp helper
    // ---------------------------------------------------------------------

    /**
     * Call this once every time through the main loop. It reads the
     * joysticks and buttons, then drives the robot.
     */
    public void teleopDrive(Gamepad gamepad) {
        // TODO: Read the joysticks:
        //         axial   = -gamepad.left_stick_y   (the minus sign is there
        //                   because pushing a stick UP gives a NEGATIVE number)
        //         lateral =  gamepad.left_stick_x
        //         yaw     =  gamepad.right_stick_x
        // TODO: Clean each number with deadband() so a joystick that isn't
        //       perfectly centered doesn't make the robot creep.
        // TODO: Choose which buttons do what. Ideas: hold a bumper for slow
        //       mode, press a button to switch field-centric on/off, press
        //       another to reset the heading. Ask the drivers what they like!
        // TODO: If fieldCentric is on, call driveFieldCentric(..., getHeading()).
        //       Otherwise call drive(...).
    }

    /** Turn slow mode on or off. */
    public void setSlowMode(boolean enabled) {
        // TODO: Save the value in slowMode.
    }

    /** Turn field-centric driving on or off. */
    public void setFieldCentric(boolean enabled) {
        // TODO: Save the value in fieldCentric.
    }

    // ---------------------------------------------------------------------
    // Which way are we facing? (the IMU)
    // ---------------------------------------------------------------------

    /**
     * Which way the robot is facing, in radians. 0 means "the direction it
     * was facing the last time resetHeading() was called."
     */
    public double getHeading() {
        // TODO: Ask the IMU for the yaw angle in radians:
        //         imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS)
        //       and return it. (You'll need to import AngleUnit.)
        return 0.0;
    }

    /**
     * Say "the way I'm facing right now is forward." Handy as a button for
     * the driver if the field-centric drive ever feels off.
     */
    public void resetHeading() {
        // TODO: Tell the IMU to reset its yaw: imu.resetYaw()
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    /**
     * Joysticks never sit at exactly 0. If the number is really small, treat
     * it as 0 so the robot doesn't slowly drift.
     */
    private double deadband(double value) {
        // TODO: If the value is between -STICK_DEADBAND and +STICK_DEADBAND
        //       (use Math.abs), return 0. Otherwise return the value.
        return value;
    }

    /** Show the driving info on the Driver Station screen. Great for debugging! */
    public void addTelemetry(Telemetry telemetry) {
        // TODO: Add lines like telemetry.addData("Slow mode", slowMode);
        //       Also try the heading and the four wheel powers.
    }
}
