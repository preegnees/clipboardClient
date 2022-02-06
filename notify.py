from plyer import notification
import argparse

parser = argparse.ArgumentParser()
parser.add_argument("--text")
parser.add_argument("--title")
args = parser.parse_args()
text = args.text
title = args.title

notification.notify(
        title=title,
        message=text,
        timeout=3
    )

