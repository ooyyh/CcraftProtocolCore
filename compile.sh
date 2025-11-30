#!/bin/bash
echo "Compiling Minecraft 1.8.9 Protocol Core..."

mkdir -p out

javac -d out -encoding UTF-8 \
    src/Main.java \
    src/client/*.java \
    src/network/*.java \
    src/protocol/*.java \
    src/protocol/packets/handshake/*.java \
    src/protocol/packets/status/*.java \
    src/protocol/packets/login/*.java \
    src/protocol/packets/play/*.java \
    src/crypto/*.java \
    src/examples/*.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo ""
    echo "Run examples:"
    echo "  java -cp out Main dedicated"
    echo "  java -cp out Main lan"
else
    echo "Compilation failed!"
fi
