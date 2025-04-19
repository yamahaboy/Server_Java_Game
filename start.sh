#!/bin/bash
mkdir -p bin
javac -d bin src/*.java
java -cp bin Server
chmod +x start.sh
