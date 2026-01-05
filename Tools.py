import os
import psutil

try:
    import psutil
    psutil_installed = True
except ImportError:
    psutil_installed = False

import webbrowser
import colorsys

def clear_console():
    if os.name == 'nt':
        os.system('cls')

    else:
        os.system('clear')

clear_console()

def Selections():
    print("Welcome in tools V1.0")
    print("build: 1")

    print("1. Specs")
    print("2. speedtest")
    print("3. automatic clicker")
    print("4. color converter")
    user = input("Select a option\n")

    if user == "1":
        print("specs")
        print("CPU cores:", os.cpu_count)
        
        if psutil_installed:
            total_mem = psutil.virtual_memory().total / (1024**3)
            print(f"Memory: {total_mem:.2f} GB")
        else:
            print("Memory: psutil library not installed on your computer please run 'pip install psutil'")

    elif user == "2":
        webbrowser.open_new_tab("https://www.speedtest.net/")

    elif user == "3":
        print("comming soon")

    elif user == "4":
        print("1. RGB to Hlv")

        user_2 = input("select option")

    clear_console()
