@echo off
set ADB=C:\Users\prem7\AppData\Local\Android\Sdk\platform-tools\adb.exe

echo ========================================
echo   Spin Bottle: Truth Dare Games
echo   Building and launching on device...
echo ========================================
echo.

:: Check if device is connected
%ADB% devices | findstr /r "device$" >nul
if errorlevel 1 (
    echo [ERROR] No Android device detected!
    echo Make sure:
    echo   1. USB debugging is enabled on your phone
    echo   2. Phone is connected via USB
    echo   3. You've authorized this computer
    echo.
    pause
    exit /b 1
)

echo [OK] Device connected
echo.

:: Build and install debug APK
echo [BUILD] Assembling debug APK...
call gradlew.bat assembleDebug
if errorlevel 1 (
    echo [ERROR] Build failed!
    pause
    exit /b 1
)

echo.
echo [INSTALL] Installing on device...
call gradlew.bat installDebug
if errorlevel 1 (
    echo [ERROR] Install failed!
    pause
    exit /b 1
)

echo.
echo [LAUNCH] Starting app...
%ADB% shell am start -n com.spinbottle.truthdare.games.debug/com.spinbottle.truthdare.games.MainActivity

echo.
echo ========================================
echo   App launched successfully! 
echo ========================================

