@echo off
chcp 65001 >nul
cls

net session >nul 2>&1
if %errorLevel% neq 0 (
    powershell -Command "Start-Process '%~dpnx0' -Verb RunAs"
    exit
)

if "%1"=="samostatne_okno" goto menu
start "PŘEPÍNAČ MONIITORŮ" cmd.exe /c "%~dpnx0" samostatne_okno
exit

:menu
cls
echo ================================================
echo       Takbar For Touchscreen Switcher
echo ================================================
echo [1] Main monitor (Classic taskbar)
echo [2] Spacedesk (Touchscreen taskbar)
echo ================================================
echo.
set /p volba="Select an option (1 or 2) and press enter: "

if "%volba%"=="1" goto stolni_pc
if "%volba%"=="2" goto postel
echo.
echo Invalid option, try again.
timeout /t 2 >nul
goto menu

:stolni_pc
echo.
echo Switching to Classic taskbar...
reg add "HKLM\System\CurrentControlSet\Control\PriorityControl" /v ConvertibleSlateMode /t REG_DWORD /d 1 /f >nul
reg add "HKCU\Software\Microsoft\Windows\CurrentVersion\Explorer\Advanced" /v ExpandableTaskbar /t REG_DWORD /d 0 /f >nul
reg delete "HKCU\Software\Microsoft\Windows\CurrentVersion\Explorer\TouchTaskbar" /f >nul 2>&1
displayswitch.exe /internal
goto restart_explorer

:postel
echo.
echo Swithcing to Touchscreen taskbar (Tablet)...
reg add "HKLM\System\CurrentControlSet\Control\PriorityControl" /v ConvertibleSlateMode /t REG_DWORD /d 0 /f >nul
reg add "HKCU\Software\Microsoft\Windows\CurrentVersion\Explorer\Advanced" /v ExpandableTaskbar /t REG_DWORD /d 1 /f >nul
displayswitch.exe /external
goto restart_explorer

:restart_explorer
echo.
echo Restating explorer.exe...
taskkill /f /im explorer.exe >nul 2>&1
timeout /t 1 >nul
start explorer.exe
echo Applied...
timeout /t 2 >nul
goto menu
