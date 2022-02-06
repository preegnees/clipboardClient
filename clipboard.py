import argparse
import pyperclip

parser = argparse.ArgumentParser()
parser.add_argument("--option")
parser.add_argument("--text")
args = parser.parse_args()
option = args.option
text = args.text

if option == "get":
    print(pyperclip.paste())

if option == "set":
    pyperclip.copy(text)
