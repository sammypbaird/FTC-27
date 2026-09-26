# Limelight model: 2026-09-25, BlueDarkUP dataset (no augmented copies), 4000 steps

First test model. Detects all three BIOBUZZ game pieces.

## Put it on the Limelight 3A
1. Plug the Limelight into a laptop with USB and open http://limelight.local:5801
2. Set a pipeline's type to **Neural Detector**.
3. Upload `limelight_neural_detector_8bit.tflite` (model) and `limelight_neural_detector_labels.txt` (labels).
4. Set the runtime to **CPU** (the 3A has no Coral chip).

`limelight_neural_detector_coral.tflite` is only for a Google Coral accelerator. We don't use it.

## Classes
| id | name |
|---|---|
| 0 | blue_nectar |
| 1 | red_nectar |
| 2 | pollen |

(The label file lists them in this order. Class ids on the Limelight usually start at 0.)

## How it was made
- **Dataset:** "FTC-BioBuzz" v1 by BlueDarkUP on Roboflow Universe (MIT license),
  https://universe.roboflow.com/bluedarkup/ftc-biobuzz-6vjbl
  - Exported as COCO JSON, converted with `limelight/tools/coco_to_tfrecord.py`:
    `--rename Yellow=pollen --rename Red=red_nectar --rename Blue=blue_nectar --exclude aug_`
  - The author's ~16,000 pre-augmented `aug_` copies were dropped.
  - 2,196 train / 763 valid / 381 test images, all 320x320. About 5x more pollen boxes than nectar.
- **Trainer:** Limelight Neural Network Trainer, https://tools.limelightvision.io/neural-network-trainer
  - Job ID `f0030a40`, 4000 steps, batch size 16, platform Coral / CPU, variant Default. About 25 minutes.

## Results
- Validation loss went from 0.91 to **0.073**. Training loss ended around 0.03 to 0.09.
- No overfitting: validation loss was still slowly decreasing at the end, so more steps may help a little.
- The trainer doesn't report accuracy (mAP). See `training_loss_graph.png` and `training_log.txt`.
- **Not yet tested on the robot.** Record how it does here: misses, false detections, red/blue confusion.
