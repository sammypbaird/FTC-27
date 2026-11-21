# Walkthrough: MainAutoMode Integration

I have successfully updated `MainAutoMode.java` to include vision-based pollen acquisition using the Limelight.

## Changes Made
- Integrated `Limelight3A` and `Servo indicator` for vision tracking.
- Implemented a search-and-approach loop that uses the detection logic from `GoToPollenOp`.
- Integrated existing intake, shoot, and return-to-start logic into the sequence.
- Added telemetry to monitor the acquisition status.

## Verification
- Code successfully updated to include the vision logic.
- The sequence now flows from acquisition -> intake -> shoot -> return.

> [!IMPORTANT]
> Please ensure the Limelight pipeline (pipeline 1) is correctly configured to detect "pollen" and that the Servo `indicator` is mapped correctly in your hardware configuration.
