import os
import psutil
import platform

# Get GPU name
def get_gpu_name():
    system = platform.system()
    
    if system == "Windows":
        try:
            gpu_name = os.popen('wmic path win32_VideoController get Name').read().strip().split("\n")[1]
            return gpu_name if gpu_name else "GPU info not available"
        except Exception:
            return "GPU info not available"
    
    elif system == "Linux":
        try:
            gpu_name = os.popen("lspci | grep -i 'VGA'").read().strip()
            return gpu_name if gpu_name else "GPU info not available"
        except Exception:
            return "GPU info not available"
    
    elif system == "Darwin":
        try:
            gpu_name = os.popen("system_profiler SPDisplaysDataType | grep 'Chipset Model'").read().strip()
            return gpu_name if gpu_name else "GPU info not available"
        except Exception:
            return "GPU info not available"

    return "GPU info not available"

# Selections
def selection():
    print("Welcome to Tools")
    print("build: 2")

    print("1. Specs")
    selection = input("Select input: ")

    if selection == "1":
        print("OS:", platform.system())
        print("CPU cores:", os.cpu_count())

        def get_cpu_freq():
            freq = psutil.cpu_freq()
            if freq:
                return f"{freq.max:.2f} MHz"
            return "CPU Frequency not available"
        
        print("Architecture:", platform.processor())
        print("RAM:", round(psutil.virtual_memory().total / (1024 ** 3), 2), "GB")
        print("RAM speed:", )
        if platform.system() == "Windows":
            ram_speed = os.popen("wmic memorychip get Speed").read().strip().split("\n")[1]
            print("RAM Speed:", ram_speed, "MHz")
        else:
            print("RAM Speed: Not available on this OS")

        print("GPU:", get_gpu_name())
selection()