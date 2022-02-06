import argparse
import subprocess

parser = argparse.ArgumentParser()
parser.add_argument("--text")
parser.add_argument("--title")
args = parser.parse_args()
text = args.text
title = args.title

info = title + ": " + text

out = subprocess.Popen(["termux-toast", "-g top", info], stdout=subprocess.PIPE)
output = out.communicate()[0]

