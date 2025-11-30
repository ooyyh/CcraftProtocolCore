@echo off
echo ========================================
echo Minecraft 1.8.9 Protocol Core - Build
echo ========================================
echo.

if not exist out mkdir out

echo Compiling Java sources...
javac -d out -encoding UTF-8 -sourcepath src src/Main.java src/examples/*.java

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo Build successful!
    echo ========================================
    echo.
    echo Usage:
    echo   Dedicated Server: java -cp out Main dedicated [host] [port] [username]
    echo   LAN Server:       java -cp out Main lan [username]
    echo.
    echo Examples:
    echo   java -cp out Main dedicated
    echo   java -cp out Main dedicated localhost 25565 TestBot
    echo   java -cp out Main lan
    echo   java -cp out Main lan MyBot
    echo.
) else (
    echo.
    echo ========================================
    echo Build failed!
    echo ========================================
    exit /b 1
)
