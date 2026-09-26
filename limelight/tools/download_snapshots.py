"""
Download every snapshot saved on the Limelight into a folder on this computer,
ready to upload to Roboflow for labeling.

Connect the Limelight to this laptop with USB first (or join the robot's Wi-Fi
and use --host with the address that reaches the Limelight).

USAGE
    python download_snapshots.py [output_folder] [--host limelight.local]

    With no output folder, photos go to limelight/snapshots/<today>/
    (that folder is ignored by git).

Only the full-size snap*.png files are downloaded. The min_* thumbnails are
skipped, and files that are already in the output folder are not downloaded
again, so it's safe to run this more than once.
"""

import argparse
import datetime
import os
import re
import sys
import urllib.error
import urllib.request

REPO_LIMELIGHT_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    today = datetime.date.today().isoformat()
    parser.add_argument("output", nargs="?",
                        default=os.path.join(REPO_LIMELIGHT_DIR, "snapshots", today),
                        help="folder to save the photos in")
    parser.add_argument("--host", default="limelight.local", help="Limelight address (default: limelight.local)")
    args = parser.parse_args()

    base = "http://%s:5801/snapshots/" % args.host
    try:
        with urllib.request.urlopen(base, timeout=10) as response:
            listing = response.read().decode("utf-8", errors="replace")
    except (urllib.error.URLError, OSError) as e:
        sys.exit("Couldn't reach the Limelight at %s (%s).\n"
                 "Is it plugged in, with the green light on?" % (base, e))

    # The folder page is a plain list of links. Keep only full-size snapshots.
    names = sorted(set(re.findall(r'href="(snap[^"/]*\.png)"', listing)))
    if not names:
        print("No snapshots on the Limelight yet.")
        return

    os.makedirs(args.output, exist_ok=True)
    downloaded = skipped = 0
    for name in names:
        path = os.path.join(args.output, name)
        if os.path.exists(path):
            skipped += 1
            continue
        urllib.request.urlretrieve(base + name, path)
        downloaded += 1
        print("  " + name)

    print("Downloaded %d new photo(s), skipped %d already saved, into:\n  %s"
          % (downloaded, skipped, os.path.abspath(args.output)))


if __name__ == "__main__":
    main()
