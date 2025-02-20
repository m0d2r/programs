import os
import psutil
import platform

def selection():
    print("Welcome to Tools")
    print("build: 1")

    print("1. Specs")
    selection = input("Select input: ")

    if selection == "1":
        print("CPU cores:", os.cpu_count())
        print("Alchitexture:", platform.processor())
        print("RAM:", round(psutil.virtual_memory().total / (1024 ** 3), 2), "GB")
selection()