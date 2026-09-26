# Limelight model: 2026-09-26, our own 7 photos, 500 steps

**Practice run only.** Trained to test the full pipeline (Limelight snapshots → Roboflow → trainer → Limelight)
with our own photos. With 7 images it has memorized them and will do poorly elsewhere. Keep using
`2026-09-25_bluedarkup-noaug_4000steps` on the robot.

## Put it on the Limelight 3A
Load it into a **separate pipeline** (e.g. pipeline 2) so the working model stays in pipeline 1.
Configuration tab → upload `limelight_neural_detector_8bit.tflite` and `limelight_neural_detector_labels.txt`,
Detector Runtime **CPU**.

## Classes
| id | name |
|---|---|
| 0 | pollen |

**Only pollen.** No NECTAR was labeled in these photos, so this model can't detect red or blue NECTAR.

## How it was made
- **Dataset:** our Roboflow project "FTC27 - BioBuzz - HVCB", version v2 (2026-09-26 10:31am), CC BY 4.0,
  https://universe.roboflow.com/sam-baird/ftc27-biobuzz-hvcb
  - 7 Limelight 3A snapshots at 640x480: **5 train / 1 valid / 1 test**.
  - Exported directly as TFRecord from Roboflow.
  - The first export (v1) had no valid split and the trainer failed with `val=None`.
- **Trainer:** Limelight Neural Network Trainer, job ID `ffd99947`, 500 steps, batch size 16, Coral / CPU.

## Results
- Validation loss 0.83 → 0.018, training loss ended around 0.03 to 0.05.
- With only 1 validation image this number is meaningless. It only shows the process worked.
