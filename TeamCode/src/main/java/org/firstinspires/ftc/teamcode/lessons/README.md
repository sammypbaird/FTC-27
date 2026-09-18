# Lesson OpModes

Bench-demo OpModes for teaching Weeks 5–7 of the programming curriculum
(Motors, Servos, Gamepad Input) — companion code for the Bench Rundown.
These aren't competition code; they're here so a mentor can drive real
hardware behavior during a lesson instead of just talking through slides.

Each one shows up on the Driver Station grouped by lesson (`Lesson 05 -
Motors`, `Lesson 06 - Servos`, `Lesson 07 - Gamepad`).

## Robot configuration needed

Set these up once on whichever Control/Expansion Hub the loose parts are
wired to, under the Driver Station's Configure Robot menu:

| Config name           | Type  | Used by                                                  |
|------------------------|-------|-----------------------------------------------------------|
| `testMotor`            | Motor | Lesson05_ColdOpen, Lesson05_MotorPlayground                |
| `testServo`            | Servo | Lesson06_ServoTuner, Lesson07_HeldVsToggleDemo (+ _Fixed)  |
| `testContinuousServo`  | Servo | Lesson06_ContinuousServoDemo (optional — only if you own one) |

`testServo` is shared between Weeks 6 and 7 on purpose — leave it wired up
between sessions.

## Files, mapped to Bench Rundown blocks

**Week 5 — Motors**
- `Lesson05_ColdOpen` — the "Silent Spin" hook (0:00). One line, run before showing any code.
- `Lesson05_MotorPlayground` — clamp test, direction, brake/float, and encoder ticks (3:00–40:00), all in one sandbox.

**Week 6 — Servos**
- `Lesson06_ServoTuner` — tuning race, "plant the flag", mirror-mount puzzle (0:00–42:00).
- `Lesson06_ContinuousServoDemo` — "When Position Means Speed" (45:00). Works as a talk-through even without the hardware.

**Week 7 — Gamepad Input**
- `Lesson07_TelemetryMirror` — hook, inverted-stick gotcha, deadband staring, raw buttons (0:00–26:00). No hardware needed.
- `Lesson07_HeldVsToggleDemo` — "Make It Buzz" (26:00) and "Held or Toggled?" (48:00). Ships broken on purpose — the fix is a commented-out block inside, meant to be live-typed.
- `Lesson07_HeldVsToggleDemo_Fixed` — safety net. If the live edit goes wrong or runs long, stop and run this one instead.

Full timing and classroom-facing script: see the Bench Rundown artifact.
