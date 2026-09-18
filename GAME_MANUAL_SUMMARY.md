# BIOBUZZ (2026-2027) Game Manual Summary

Summary of `doc/BIOBUZZ_Competition_Manual_V1.pdf` (Competition Manual **V1**, 173 pages, *FIRST* Tech Challenge,
"BIOBUZZ presented by RTX", season theme *FIRST* CANOPY). Written for this team's coding needs.

> **The PDF is the source of truth.** This is a summary; if the two disagree, the manual wins. The manual is V1 and
> will change: Team Updates post every Thursday, and the Q&A opens Sept 28, 2026. Re-check before competing.
> Section and rule numbers (e.g. `G410`, `R105`, "§10.5") point back into the PDF.

Terms in ALL CAPS are the manual's defined terms (see [Terminology](#terminology)).

---

## 1. The game in one minute

Two ALLIANCES (2 teams each, Red vs Blue) collect **POLLEN** (small yellow balls) and **NECTAR** (larger red/blue
balls). They do three things with them:

1. **LAUNCH** them into their **HIVE**'s upward-facing **CELL**. Enough elements make the HIVE **TIP**, which scores
   points and unlocks more NECTAR for the human player.
2. Drop them into **FLOWERS** (only in the last 60 s). The ALLIANCE whose NECTAR is top-most **owns** the FLOWER and
   scores for everything in it.
3. Push or carry them into their **GARDEN** (corner strip) for a small per-element score.

Robots also score for **LEAVE** (AUTO) and **PARK** (in their LOADING ZONE).

Match flow: **AUTO 30 s → 8 s transition → TELEOP 2:00** (2:38 total).

---

## 2. The field (§9)

- **FIELD:** about 144 in x 144 in (12 ft square), 36 interlocking foam TILES (24 in each, 6x6 grid). Columns run
  **A-F**. Columns **A, B, C = red side; D, E, F = blue side.** Rows and columns are used for setup and for the AUTO
  boundary (G402).
- **Orientation:** the **red ALLIANCE AREA is on the left** as seen from the audience. Blue is on the right.
- **HIVE Structure:** in the center. A frame holds one red HIVE and one blue HIVE.
  - Each HIVE is **bi-stable** on a pivot (axis 43.95 in above the TILES) and has **2 CELLS**, about 18.8 in apart.
    Exactly one CELL faces up at a time.
  - **CELL opening: about 20 in wide x 14 in tall x 12 in deep.** This is the LAUNCH target.
  - The HIVE holds its position until enough POLLEN/NECTAR is LAUNCHED into the upward CELL, then it flips to its
    other stable state (a **TIP**).
  - **Tipping ratios** (from the team, **not stated in the manual**): the CELL tips at any of these combinations of
    contents:

    | NECTAR in CELL | POLLEN in CELL |
    |---|---|
    | 0 | 8 |
    | 1 | 6 |
    | 2 | 5 |
    | 3 | 3 |
    | 4 | 2 |
    | 5 | 0 |

    More NECTAR means fewer POLLEN needed, since NECTAR is heavier. Each HIVE starts with 3 NECTAR already in the
    upward CELL, so the first TIP needs roughly 3 more POLLEN, if the counts include the pre-staged NECTAR. Verify
    that on a real HIVE.
- **FLOWERS:** 4, attached to the perimeter walls.
  - Top opening is about **4 in diameter, about 21.5 in above the TILES**, with a 1.25 in backstop.
  - Elements go in through the top.
  - **POLLEN (not NECTAR) can be removed from the bottom** through a retrieval opening about 3.55 in tall x 3.57 in
    deep. The lower ring has a POLLEN-sized hole (about 2.79 in).
  - The scoring volume is between the top ring and the middle ring.
- **GARDEN:** about 23 in x 2 in strip of tape in opposite corners: the red GARDEN in one corner, the blue GARDEN in
  the opposite corner. It is **not protected**: either ALLIANCE can remove elements from either GARDEN.
- **LOADING ZONE:** about 23 in wide x 11 in deep, against the perimeter wall next to each ALLIANCE AREA. It is used
  for PARK and is where humans introduce NECTAR.
- **ALLIANCE AREA:** about 97 in x 54 in, outside the FIELD. The DRIVE TEAM stands here.
- **Tolerances:** field dimensions are +/- 1 in. Design the robot to tolerate field variation (§9.1). Elements are not
  perfect spheres and vary in size.

### Scoring elements (§9.8)

| Element | Size | Color | Total in a match |
|---|---|---|---|
| **POLLEN** | about 2.8 in (7.1 cm) | yellow | 40 |
| **NECTAR** | about 3.6 in (9.1 cm) | red (8) and blue (8) | 16 |

All are Gopher ResisDent polyethylene balls (AndyMark am-5851 and am-5852).

### Starting layout (§10.3.1)

- **POLLEN (40):** 4 in each of the 4 FLOWERS (16), 4 in the red GARDEN, 4 in the blue GARDEN, and **4 pre-loaded in
  each robot (16)**.
- **NECTAR (16):** **3 in each upward-facing CELL** (6 total), and **5 per ALLIANCE waiting in the ALLIANCE AREA**
  (10 total).
- Each HIVE starts tilted with one CELL up and one down. The CELL that "points at" a FLOWER is the one tilted down.

---

## 3. Match timeline (§10.4, Table 9-1)

| Clock (event timer) | What happens |
|---|---|
| 2:30 | Match starts (countdown, optional "Cavalry Charge") |
| 2:30 to 2:00 | **AUTO**, 30 s, no driver input |
| 2:00 | AUTO ends (buzzer) |
| 0:08 to 0:01 | **Transition**, 8 s. Drivers pick up controllers. **No powered robot movement** (G403) |
| 2:00 (TELEOP) | **TELEOP begins**, 2 minutes |
| **1:00 left** | **FLOWER scoring and human NECTAR dump unlock** (audio cue TBD) |
| 0:20 left | Train whistle |
| 0:00 | End of match. Robot must be motionless (G404) |

The visual field timer is authoritative, not the audio cues.

---

## 4. Scoring (§10.5)

### Match points (Table 10-2)

| Achievement | AUTO | TELEOP | Notes |
|---|---|---|---|
| **LEAVE** | 3 | - | Robot no longer touching the perimeter wall at end of AUTO |
| **PARK** | 5 | 5 | At least partially in the LOADING ZONE. AUTO PARK is assessed at end of AUTO, TELEOP PARK at end of match |
| **HIVE TIP** | 20 | 20 | Per TIP. TIPS finished before TELEOP count as AUTO |
| POLLEN/NECTAR remaining in the upward CELL | - | 2 | At end of match (see open questions on per-element) |
| **Bottom NECTAR Bonus** (FLOWER) | - | 5 | ALLIANCE with the bottom-most qualifying NECTAR of its color in a FLOWER |
| POLLEN/NECTAR in an **owned** FLOWER | - | 2 | Per element, whoever placed it |
| POLLEN/NECTAR in your **GARDEN** | - | 1 | Per element, whoever placed it. Either ALLIANCE's NECTAR counts for the GARDEN of that color |

### Ranking points (qualification)

| RP | Condition | Threshold ("all other events") |
|---|---|---|
| **WIN** | more match points than opponent | 3 RP |
| **TIE** | equal match points | 1 RP |
| **SWARM RP** | combined LEAVE + PARK points | **>= 16 points** |
| **POLLINATOR 1 RP** | number of HIVE TIPS | **>= 4 TIPS** |
| **POLLINATOR 2 RP** | number of HIVE TIPS | **>= 7 TIPS** |

Thresholds for Regional Championships and the *FIRST* Championship are **TBA** (Team Updates). Premier Events set
their own.

### How each thing is judged

- **HIVE TIP** requires (a) the HIVE moving from one stable state to the other so the downward CELL becomes upward,
  and (b) the damper that wasn't touching the frame then touching it.
  - **LAUNCHING into the upward CELL is the only legal way to cause a TIP** (G417).
  - Launching at the downward CELL while the HIVE is tipping can disrupt the TIP. Pause launching briefly so it's
    unambiguous.
- **FLOWER scoring:** an element scores when it is at least partially in the scoring volume (between the top and
  middle rings).
  - Elements must go in the top (G418).
  - **Owner** = ALLIANCE whose NECTAR is top-most. The owner scores 2 for every POLLEN and NECTAR in the FLOWER.
  - **Bottom NECTAR Bonus** = 5 for the ALLIANCE with the bottom-most NECTAR of its color.
  - **Scoring cannot begin until 60 s remain** (G410). Elements placed earlier still count but draw penalties.
- **GARDEN:** an element needs to be at least partially inside the GARDEN zone at the end of TELEOP.
- Everything is assessed after all robots and elements have come to rest at the end of the match.

### Penalties (Table 10-4)

| Penalty | Effect |
|---|---|
| VERBAL WARNING | warning only |
| **MINOR FOUL** | +5 to the opponent |
| **MAJOR FOUL** | +20 to the opponent |
| YELLOW CARD | warning. A second yellow becomes a RED CARD. Playoff cards apply to the whole ALLIANCE |
| RED CARD | DISQUALIFIED for the match (0 points) |
| DISABLED | robot is stopped for the rest of the match |

Time words used in rules: **MOMENTARY** is under about 3 s, **CONTINUOUS** is over about 10 s, **REPEATED** is more
than once per match. **STRATEGIC** means done to gain an advantage. Many rules are a VERBAL WARNING for accidents and
a MAJOR FOUL plus YELLOW CARD if referees judge the act STRATEGIC.

---

## 5. The human DRIVE TEAM (§10.2, §11.4.6)

- Up to **4 people**: 1 DRIVE COACH, up to 3 DRIVERS, and a HUMAN PLAYER role. Only 1 may be a non-STUDENT.
- The **HUMAN PLAYER only handles NECTAR.**
  - After each **TIP of your own HIVE**, you may enter **1** of your 5 staged NECTAR (G426).
  - With **60 s or less left**, you may enter **all** remaining NECTAR.
  - NECTAR must be introduced by hand (no tools) so it **touches the TILE inside your LOADING ZONE first** (G427).
    Humans may never place elements in a HIVE or FLOWER.
  - Humans may retrieve their own ALLIANCE's NECTAR that left the FIELD, if reachable from the ALLIANCE AREA.
- Stay in your ALLIANCE AREA. Do not touch robots, elements on the floor or on a robot, or FIELD elements (G422,
  G425).
- DRIVE COACHES may not hold the gamepads or touch elements (G423, G424). They may hold the Driver Station device,
  select OpModes, and press INIT, start, and stop.
- **No interaction with the robot during AUTO** (G401), except pressing start and stop.

---

## 6. Rules that shape the code

### Start of match (must be true when the match starts)

- **G304:** the robot is fully on its own side, **touching the perimeter wall**, in contact with **exactly 4 POLLEN**
  pre-loads, not in the LOADING ZONE, not touching a FLOWER, in its STARTING CONFIGURATION, and **fully motionless
  after OpMode INIT completes.**
  - Your INIT must hold or pre-position servos and motors without twitching or drifting (R103 allows OpMode-held
    positions).
- **G305 / §10.3.3:** an OpMode must be selected and INIT'd before the match, even if you don't run AUTO.
  - For AUTO, select the AUTO OpMode with the **30 s timer enabled**.
  - Use **`preselectTeleOp`** on the `@Autonomous` annotation so TELEOP auto-queues.
  - Without an AUTO OpMode, create a default AUTO OpMode (from the BasicOpMode sample) that queues TELEOP.
- **R102:** starting configuration fits in an **18 x 18 x 18 in cube**. Pre-loaded POLLEN may hang outside it.
- **R105 / G416:** after the start the robot may expand, but **must stay inside 18 x 24 x 29 in** (29 in vertical).
  - This must be physically enforced, not just limited by software. A mechanism that can mechanically exceed the
    limit is illegal even if code stops it.

### During AUTO (30 s)

- **G402:** don't interfere with the opponent's AUTO. Keep to your own side: **columns A-C for red, D-F for blue**.
  Crossing to the other side is risky and may be judged STRATEGIC.
- HIVE TIPS finished before TELEOP score as AUTO (20 each). LEAVE (3) and PARK (5) are assessed at AUTO end.
- AutoPark means ending AUTO at least partly inside your LOADING ZONE. LEAVE means no longer touching the wall.

### Transition (8 s)

- **G403:** no powered movement of the robot or any mechanism between AUTO and TELEOP.
  - Gravity, inertia, and de-energizing are okay.
  - If your TELEOP `init` moves actuators, **wait for TELEOP to begin before pressing INIT**.

### During TELEOP

- **G407:** never CONTROL **more than 4 elements** at once. Add intake guards or counters (5 or more for more than a
  moment will be scrutinized).
- **G408:** don't CONTROL the opponent's NECTAR.
- **G409:** don't catch or deflect elements released by a TIPPED HIVE until they hit something else.
- **G410:** **NECTAR into FLOWERS only in the last 60 s.** Gate this on the match timer (60 s into TELEOP).
- **G405:** don't deliberately eject elements from the FIELD.
- **G411:** don't hoard or corral elements to keep the opponent from them.
- **G417:** don't touch or disturb the HIVE in any way other than LAUNCHING into the upward CELL. Don't LAUNCH at the
  bottom, sides, or top faces of a CELL on purpose.
- **G418:** only enter POLLEN or NECTAR at the top of a FLOWER. Only remove POLLEN from the bottom.
- **G415:** don't grab, attach to, or hang from ARENA elements. Wrapping around a FLOWER for alignment is okay.
- **G419 / G420 / G421:** don't damage, tip, or entangle opponents. **PINNING is limited to a 3-count.**
- **G412 / G413:** the robot must be under control and must stop when told (stop button plus set down controllers).

### End of match

- **G404:** the robot must have **no powered movement after TELEOP ends**. Stop drive and mechanisms cleanly at the
  end.

### Rules for the robot and control system (§12)

| Topic | Rule | Notes |
|---|---|---|
| **Controller** | R701 | One REV **Control Hub**, optionally plus **one** Expansion Hub (or an Android phone plus Expansion Hub) |
| **Motors** | R501, R503 | Only listed motors (NeveRest, goBILDA Yellow Jacket and 5000, REV HD Hex and Core Hex, etc.). **Max 8 motors** |
| **Servos** | R502, R503 | Under 8 W and stall current within limit at 6 V. **Max 8 servos** |
| **Power regulators** | R505, R608 | Approved hubs, servo modules, and injectors only, at 2 motors or 2 servos per port |
| **Sensors / vision** | R707, R708, R702 | USB is for **UVC webcams** (single sensor, no stereo) and vision coprocessors. **Limelight 3A** is the only programmable coprocessor allowed. OpenMV, Luxonis OAK, and Limelight 3G are prohibited |
| **Battery / switch** | R601, R603 | One approved 12 V NiMH battery, one main power switch |
| **Lasers** | R710 | Only non-visible Class I sensors |
| **Air / pneumatics** | R801 | No compressors or blowers. **Flywheels and rollers for launching are fine** |
| **Naming** | R705 | Control Hub `<team#>-RC`, Driver Station `<team#>-DS` |
| **Wi-Fi** | R711 | Change the Control Hub default Wi-Fi password. Bluetooth off on RC and DS |

**Telemetry, logging, and networking (R704.D):** teams may **not** use **FTC Dashboard, FTControl Panels, or other
third-party streaming or logging tools** during matches, and there must be **no continuous video stream**. Only the
FTC Driver Station app telemetry is allowed. Configure your code so those tools are disabled or removed for
competition.

**COTS limits (R301, R303):** custom software and designs from before kickoff may be reused (R304). Purpose-built COTS
game-task mechanisms are not allowed. COTS parts must be single degree of freedom (with exceptions for mecanum and
omni wheels, dead-wheel odometry kits, and similar). Scoring elements can't be used in robot construction (R305).

**Other build rules:** no weight limit (R104). Robots need at least two ROBOT SIGNS (team number plus red or blue
alliance color) (R401 to R403). No floor-gripping or suction downforce (R204). Robots must be removable from the
FIELD without power (R203). Don't mimic 36h11 AprilTags on the robot (R202.C).

---

## 7. AprilTags and vision (§9.9)

- **Family: 36h11.** Each tag is **3.25 in (8.25 cm)** square.
- Tags come **4 per cluster (one sticker)**, one cluster per **CELL**. Each CELL cluster has a distinct ID group.
- Tag IDs by location (checked against Figure 9-17):

| Location | Tag IDs |
|---|---|
| Red, side **opposite the audience** ("Red Scoring Tags") | **30, 31, 32, 33** |
| Red, **audience side** | **34, 35, 36, 37** |
| Blue, **audience side** | **38, 39, 40, 41** |
| Blue, side **opposite the audience** ("Blue Scoring Tags") | **42, 43, 44, 45** |

- Clusters are mounted on the **bottom face of the CELL**, facing down toward the TILES, with the cluster's bottom
  edge toward the center of the FIELD. Reference holes let you measure the cluster position relative to the FIELD.
- The tags are for **navigation and targeting** (localizing to the HIVE / launch zone). **Confirm your SDK's
  AprilTag library supports the 36h11 family and these IDs.**
- Don't shine lights or lasers at the field, and don't mimic AprilTags. That counts as jamming a robot's sensors
  (G302.E).

---

## 8. Strategy notes for the code

These are my readings of the rules, not statements from the manual.

- **Everyone starts with 4 pre-loaded POLLEN.** A short AUTO that LAUNCHES those into your upward CELL is worth going
  for. TIPS in AUTO score 20 each, and each TIP unlocks one NECTAR for the human player.
- **Use the tipping ratios.** Each NECTAR launched saves 1 to 2 POLLEN, and a TIP is worth 20 points. Prefer NECTAR
  for TIPS once the human player has fed it in. A launcher that counts elements per CELL could tell when a TIP is
  due.
- **LEAVE + PARK is 8 points in AUTO** and PARK is 5 more in TELEOP. Two robots each doing both give 26, over the
  SWARM RP threshold of 16.
- **POLLINATOR RPs come from TIP count** (4 and 7 TIPS), so TIP rate is a ranking lever, not just a scoring one.
- **Time-gate FLOWER behavior.** Nothing goes into a FLOWER before 60 s remain. End-of-match sequences should be
  planned in the last minute: place NECTAR (bottom bonus, then top for ownership), then return to the LOADING ZONE
  for PARK.
- **Never hold more than 4 elements.** Add a count-based limit on intake.
- **Keep it deterministic.** Referees judge "unambiguous." Make scoring actions obvious (e.g. pause launching while
  the HIVE tips).
- **Field variation is expected.** Use AprilTags and sensors rather than fixed dead-reckoning where you can.

---

## 9. Terminology

| Term | Meaning |
|---|---|
| **ALLIANCE** | 2 teams playing together (Red or Blue) |
| **ARENA** | the FIELD plus all game infrastructure (SCORING ELEMENTS, queue area, media area, event equipment) |
| **AUTO** | first 30 s, robot runs only pre-programmed code |
| **TELEOP** | final 2:00, drivers control the robot |
| **CELL** | one of 2 scoring pockets on a HIVE. LAUNCH target |
| **HIVE** | the bi-stable, pivoting structure with 2 CELLS. One red, one blue |
| **TIP / TIPPED** | HIVE flipped to its other stable state by launched elements |
| **FLOWER** | perimeter tube structure elements are dropped into |
| **GARDEN** | corner strip where elements score 1 each |
| **LOADING ZONE** | alliance-side zone by the ALLIANCE AREA. Used for PARK and NECTAR entry |
| **ALLIANCE AREA** | where the DRIVE TEAM stands, outside the FIELD |
| **POLLEN / NECTAR** | the small yellow / larger red-or-blue scoring balls |
| **LAUNCH** | shoot, propel, or forcefully throw an element toward a target |
| **CONTROL** | element is fully supported by the robot, or the robot herds it. Capped at 4 (G407) |
| **LEAVE** | robot is no longer touching the perimeter wall (AUTO) |
| **PARK** | robot is at least partially in its LOADING ZONE |
| **PIN** | blocking an opponent's movement by contact (3 s limit) |
| **STARTING CONFIGURATION** | pre-match shape of the robot (18 in cube) |
| **SWARM / POLLINATOR RP** | ranking points for LEAVE+PARK points and for HIVE TIPS |
| **DRIVE TEAM** | up to 4 people: coach, drivers, human player |
| **HUMAN PLAYER** | team member who manages NECTAR |
| **FTA / LRI / Head REFEREE** | *FIRST* Technical Advisor / Lead Robot Inspector / final rules authority at an event |
| **COTS / FABRICATED ITEM** | off-the-shelf part / anything modified or built by the team |
| **RP** | RANKING POINTS |

---

## 10. Open questions and gaps in V1

Items the manual leaves open, or where the wording is unclear.

- **How many elements tip a HIVE** is not stated in the manual (only "enough"). The ratios in section 2 came from
  the team. Confirm them against a real HIVE or the Field Setup Guide, and whether the pre-staged NECTAR count.
- **CELL end-of-match points:** the manual doesn't say whether the 2 points are per element or per CELL.
- **FLOWER capacity and stacking depth** are not given. The bottom/top order depends on how many elements fit.
- **RP thresholds** for Regionals and the *FIRST* Championship are TBA. The 16, 4, and 7 values apply only at "all
  other events."
- **Audio cue for FLOWER unlock** at 1:00 is `[TBD]`.
- **AprilTag geometry:** which CELL's tags face where changes as the HIVE tips. Verify with CAD and the Field Setup
  Guide before writing pose code.
- **Still "coming soon":** Field Acceptance Checklist, Field Mitigation Guide, Escalation Guidelines.
- **Q&A opens Sept 28, 2026.** Weekly Team Updates follow on Thursdays.

---

## 11. Where to look in the PDF

| Topic | Section (page) |
|---|---|
| Game overview | §8 (p. 62) |
| Field and elements | §9 (pp. 63-79) |
| Match setup and periods | §10.1-10.4 (pp. 81-85) |
| Scoring and point table | §10.5 (pp. 86-91) |
| Penalties and cards | §10.6 (pp. 92-96) |
| Game rules (G) | §11 (pp. 99-117) |
| Robot construction rules (R) | §12 (pp. 118-144) |
| Tournament structure (T) | §13 (pp. 145-164) |
| Glossary | §16 (pp. 169-173) |
