"""
Convert a Roboflow "COCO JSON" export into the zipped TFRecord format that the
Limelight Neural Network Trainer expects.

Use this when Roboflow won't let you export a dataset as TFRecord directly.
It needs only plain Python 3 (no TensorFlow).

USAGE
    python coco_to_tfrecord.py <coco_export.zip or folder> <output.zip> [--rename OLD=NEW ...]

EXAMPLE
    python coco_to_tfrecord.py "FTC-BioBuzz.v1i.coco.zip" ftc-biobuzz-tfrecord.zip ^
        --rename Yellow=pollen --rename Red=red_nectar --rename Blue=blue_nectar

INPUT (what Roboflow's COCO export looks like)
    train/  _annotations.coco.json + images
    valid/  _annotations.coco.json + images
    test/   _annotations.coco.json + images     (any split may be missing)

OUTPUT (same layout as Roboflow's own TFRecord export)
    train/<name>.tfrecord  + train/<name>_label_map.pbtxt
    valid/...              test/...
"""

import argparse
import json
import os
import struct
import sys
import tempfile
import zipfile

SPLITS = ("train", "valid", "test")
RECORD_NAME = "dataset"


# ---------------------------------------------------------------------------
# CRC32C checksum. Every TFRecord entry is protected by a "masked" CRC32C.
# Uses the fast google-crc32c package if installed, else a pure-Python table.
# ---------------------------------------------------------------------------
try:
    import google_crc32c

    def crc32c(data):
        return google_crc32c.value(data)
except ImportError:
    _TABLE = []
    for n in range(256):
        c = n
        for _ in range(8):
            c = (c >> 1) ^ 0x82F63B78 if c & 1 else c >> 1
        _TABLE.append(c)

    def crc32c(data):
        crc = 0xFFFFFFFF
        table = _TABLE
        for byte in data:
            crc = table[(crc ^ byte) & 0xFF] ^ (crc >> 8)
        return crc ^ 0xFFFFFFFF


def masked_crc(data):
    crc = crc32c(data)
    return (((crc >> 15) | (crc << 17)) + 0xA282EAD8) & 0xFFFFFFFF


# ---------------------------------------------------------------------------
# Minimal protobuf encoding of a tf.train.Example. An Example is just a map of
# feature name -> list of bytes, floats, or ints.
# ---------------------------------------------------------------------------
def _varint(value):
    out = bytearray()
    while True:
        byte = value & 0x7F
        value >>= 7
        if value:
            out.append(byte | 0x80)
        else:
            out.append(byte)
            return bytes(out)


def _field(number, payload):
    """A length-delimited protobuf field."""
    return _varint((number << 3) | 2) + _varint(len(payload)) + payload


def bytes_feature(values):
    return _field(1, b"".join(_field(1, v) for v in values))


def float_feature(values):
    return _field(2, _field(1, struct.pack("<%df" % len(values), *values)))


def int64_feature(values):
    return _field(3, _field(1, b"".join(_varint(v) for v in values)))


def encode_example(features):
    entries = b"".join(
        _field(1, _field(1, name.encode()) + _field(2, feature))
        for name, feature in features.items()
    )
    return _field(1, entries)


def write_record(f, data):
    length = struct.pack("<Q", len(data))
    f.write(length)
    f.write(struct.pack("<I", masked_crc(length)))
    f.write(data)
    f.write(struct.pack("<I", masked_crc(data)))


# ---------------------------------------------------------------------------
# Conversion
# ---------------------------------------------------------------------------
def load_split(root, split):
    path = os.path.join(root, split, "_annotations.coco.json")
    if not os.path.exists(path):
        return None
    with open(path, encoding="utf-8") as f:
        return json.load(f)


def build_classes(coco_by_split, renames):
    """
    Collect the class names that are actually used by annotations, across all
    splits, so every split shares one label map. Roboflow adds an unused
    "parent" category to COCO exports; this skips it.
    """
    names = []
    for coco in coco_by_split.values():
        used = {a["category_id"] for a in coco["annotations"]}
        for cat in sorted(coco["categories"], key=lambda c: c["id"]):
            name = renames.get(cat["name"], cat["name"])
            if cat["id"] in used and name not in names:
                names.append(name)
    return names


def convert_split(root, split, coco, class_ids, renames, out_dir):
    cat_name = {c["id"]: renames.get(c["name"], c["name"]) for c in coco["categories"]}
    boxes_by_image = {}
    for ann in coco["annotations"]:
        boxes_by_image.setdefault(ann["image_id"], []).append(ann)

    os.makedirs(os.path.join(out_dir, split), exist_ok=True)
    record_path = os.path.join(out_dir, split, RECORD_NAME + ".tfrecord")
    skipped_boxes = 0
    with open(record_path, "wb") as out:
        for image in coco["images"]:
            width, height = image["width"], image["height"]
            with open(os.path.join(root, split, image["file_name"]), "rb") as f:
                encoded = f.read()
            ext = os.path.splitext(image["file_name"])[1].lower()
            image_format = b"png" if ext == ".png" else b"jpeg"

            xmins, xmaxs, ymins, ymaxs, texts, labels = [], [], [], [], [], []
            for ann in boxes_by_image.get(image["id"], []):
                x, y, w, h = ann["bbox"]  # COCO: pixels, top-left corner + size
                if w <= 0 or h <= 0:
                    skipped_boxes += 1
                    continue
                name = cat_name[ann["category_id"]]
                xmins.append(max(0.0, x / width))
                xmaxs.append(min(1.0, (x + w) / width))
                ymins.append(max(0.0, y / height))
                ymaxs.append(min(1.0, (y + h) / height))
                texts.append(name.encode())
                labels.append(class_ids[name])

            example = encode_example({
                "image/encoded": bytes_feature([encoded]),
                "image/filename": bytes_feature([image["file_name"].encode()]),
                "image/format": bytes_feature([image_format]),
                "image/height": int64_feature([height]),
                "image/width": int64_feature([width]),
                "image/object/bbox/xmin": float_feature(xmins),
                "image/object/bbox/xmax": float_feature(xmaxs),
                "image/object/bbox/ymin": float_feature(ymins),
                "image/object/bbox/ymax": float_feature(ymaxs),
                "image/object/class/text": bytes_feature(texts),
                "image/object/class/label": int64_feature(labels),
            })
            write_record(out, example)

    label_map = "".join(
        'item {\n    name: "%s",\n    id: %d,\n    display_name: "%s"\n}\n' % (n, i, n)
        for n, i in class_ids.items()
    )
    with open(os.path.join(out_dir, split, RECORD_NAME + "_label_map.pbtxt"), "w") as f:
        f.write(label_map)
    return len(coco["images"]), len(coco["annotations"]) - skipped_boxes, skipped_boxes


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("input", help="Roboflow COCO JSON export (.zip or unzipped folder)")
    parser.add_argument("output", help="output .zip for the Limelight trainer")
    parser.add_argument("--rename", action="append", default=[], metavar="OLD=NEW",
                        help="rename a class, e.g. --rename Yellow=pollen (repeatable)")
    args = parser.parse_args()
    renames = dict(r.split("=", 1) for r in args.rename)

    with tempfile.TemporaryDirectory() as tmp:
        root = args.input
        if zipfile.is_zipfile(root):
            print("Unzipping %s ..." % root)
            with zipfile.ZipFile(root) as z:
                z.extractall(os.path.join(tmp, "in"))
            root = os.path.join(tmp, "in")

        coco_by_split = {s: c for s in SPLITS if (c := load_split(root, s)) is not None}
        if not coco_by_split:
            sys.exit("No train/valid/test folders with _annotations.coco.json found in " + args.input)

        classes = build_classes(coco_by_split, renames)
        class_ids = {name: i + 1 for i, name in enumerate(classes)}  # ids start at 1
        print("Classes:", ", ".join("%d=%s" % (i, n) for n, i in class_ids.items()))

        out_dir = os.path.join(tmp, "out")
        for split, coco in coco_by_split.items():
            images, boxes, skipped = convert_split(root, split, coco, class_ids, renames, out_dir)
            note = " (skipped %d empty boxes)" % skipped if skipped else ""
            print("  %-5s %6d images, %6d boxes%s" % (split, images, boxes, note))

        print("Writing %s ..." % args.output)
        with zipfile.ZipFile(args.output, "w", zipfile.ZIP_STORED) as z:
            for folder, _, files in os.walk(out_dir):
                for name in files:
                    full = os.path.join(folder, name)
                    z.write(full, os.path.relpath(full, out_dir))
    print("Done. Upload %s to https://tools.limelightvision.io/neural-network-trainer" % args.output)


if __name__ == "__main__":
    main()
