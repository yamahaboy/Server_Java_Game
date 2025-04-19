#!/bin/bash
mkdir -p bin
javac -d bin src/*.java
java -cp bin HttpServerGame
chmod +x start.sh
