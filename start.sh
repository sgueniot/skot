#! /bin/sh
touch build.gradle.kts
pkg=$1
name=$2

echo $name

printf 'buildscript {
    repositories {
        google()
    }
}
plugins {
    id("tech.skot.starter") version "1.+"
}
skot {
    appPackage = "%s"
    appName = "%s"
}' "$pkg" "$name" > build.gradle.kts

gradle wrapper
./gradlew start
./gradlew skGenerate


