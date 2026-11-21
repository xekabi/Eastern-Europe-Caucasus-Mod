@echo off
setlocal
cd /d "%~dp0"
where java >nul 2>nul
if errorlevel 1 (
 echo Java not found. Install JDK 17 from https://adoptium.net/
 pause
 exit /b 1
)
where gradle >nul 2>nul
if not errorlevel 1 (
 gradle --no-daemon clean build
) else (
 echo Gradle not found. Downloading Gradle 8.8 once, internet required...
 powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $d='gradle-8.8'; if (!(Test-Path $d)) { Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-8.8-bin.zip' -OutFile 'gradle-8.8-bin.zip'; Expand-Archive 'gradle-8.8-bin.zip' -DestinationPath . -Force; Remove-Item 'gradle-8.8-bin.zip' }; & '.\gradle-8.8\bin\gradle.bat' '--no-daemon' 'clean' 'build'"
)
if errorlevel 1 (
 echo BUILD FAILED - consult the log above.
 pause
 exit /b 1
)
echo Done. Your jar: build\libs\eac16-0.1.0.jar
pause
