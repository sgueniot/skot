#! /bin/sh
touch build.gradle.kts
pkg=${pkg:-my.pkg.app}
name=${name:-My App}

while [ $# -gt 0 ]; do

   if [[ $1 == *"--"* ]]; then
        param="${1/--/}"
        declare $param="$2"
        # echo $1 $2 // Optional to see the parameter:value result
   fi

  shift
done

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


