# Path Recorder & Autonomous Implementation Plan

I will create a tool for you to record a path manually using the robot's odometry wheels, and then integrate that path into an autonomous OpMode that handles your specific shooting sequence.

## User Review Required

- **Path Recording:** You will need to manually drive the robot to record the path.
- **Odometry Integration:** This plan assumes `com.pedropathing` is correctly set up with your odometry hardware.

## Proposed Changes

### [Component Name: Path Recording]

#### [NEW] [PathRecorderOp.java](file:///C:/Users/Finn/Documents/GitHub/FTC-27/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/fp_code/opsmodes/auto/PathRecorderOp.java)
- A TeleOp mode that allows you to drive the robot normally and press a button (e.g., A) to start recording the odometry path.
- It will output the recorded points to the driver station telemetry so you can copy/paste or save them.

### [Component Name: Shooting Autonomous]

#### [NEW] [ShootingAutoOp.java](file:///C:/Users/Finn/Documents/GitHub/FTC-27/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/fp_code/opsmodes/auto/ShootingAutoOp.java)
- An autonomous OpMode that uses `PedroPathing` to follow the recorded path.
- Sequence:
    1. Follow path to shooting position.
    2. Execute shooting sequence (shoot without moving forward/backward).
    3. Follow the path in reverse back to the start.

## Verification Plan

### Automated Tests
- Build `TeamCode` to ensure all imports for `PedroPathing` are resolved.

### Manual Verification
1. Run `PathRecorderOp` and drive the desired path.
2. Verify the points.
3. Update `ShootingAutoOp` with the recorded points.
4. Run `ShootingAutoOp` and observe the sequence.
