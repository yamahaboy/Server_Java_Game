#!/bin/bash
rm -rf bin
mkdir -p bin
javac -d bin src/*.java
java -cp bin HttpServerGame 
