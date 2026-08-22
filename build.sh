#!/bin/bash
# Simple build script for FrameGenMod

if ! command -v gradle &> /dev/null; then
    echo "Gradle not found. Installing..."
    curl -s https://get.gradle.org | bash
    export PATH=$PATH:$HOME/.gradle/gradle-8.5/bin
fi

gradle build
