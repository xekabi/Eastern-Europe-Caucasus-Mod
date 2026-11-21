#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if ! command -v java >/dev/null 2>&1; then
  echo 'Install Java JDK 17 first (for example Eclipse Temurin 17).' >&2
  exit 1
fi
if command -v gradle >/dev/null 2>&1; then
  gradle --no-daemon clean build
else
  echo 'Fetching Gradle 8.8 (once). Internet is required.'
  if [ ! -f .gradle-local/gradle-8.8/bin/gradle ]; then
    mkdir -p .gradle-local
    curl -fL https://services.gradle.org/distributions/gradle-8.8-bin.zip -o .gradle-local/gradle-8.8-bin.zip
    unzip -q .gradle-local/gradle-8.8-bin.zip -d .gradle-local
    rm .gradle-local/gradle-8.8-bin.zip
  fi
  .gradle-local/gradle-8.8/bin/gradle --no-daemon clean build
fi
echo 'build/libs/eac16-0.1.0.jar'
