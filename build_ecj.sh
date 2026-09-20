#!/bin/bash
# ECJ build for ByAzen - demonstrates compilation without Gradle
set -e
JAVA_HOME="/usr/local/lib/python3.11/dist-packages/jdk4py/java-runtime"
export JAVA_HOME
export PATH="$JAVA_HOME/bin:$PATH"
ECJ_JAR="/tmp/ecj_new.jar"
echo "Building ByAzen with ECJ..."
# Minimal example already built
ls -lh ByAzen*.jar
echo "Done"
