#!/bin/bash
set -e
GRADLE_USER_HOME="${GRADLE_USER_HOME:-.gradle}"
export GRADLE_USER_HOME
mkdir -p "$GRADLE_USER_HOME/wrapper"
JAR_FILE="$GRADLE_USER_HOME/wrapper/gradle-8.5-all.zip"
if [ ! -f "$JAR_FILE" ]; then
  echo "Downloading Gradle..."
  mkdir -p "$(dirname "$JAR_FILE")"
  curl -L https://services.gradle.org/distributions/gradle-8.5-all.zip -o "$JAR_FILE"
fi
exec java -jar gradle/wrapper/gradle-wrapper.jar "$@"
