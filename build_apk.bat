@echo off
set "JAVA_HOME=C:\Users\sanja\android-env\jdk-17"
set "ANDROID_HOME=C:\Users\sanja\android-env\sdk"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Checking Java:
"%JAVA_HOME%\bin\java.exe" -version

echo Running Gradle assembleDebug...
call gradlew.bat assembleDebug --stacktrace
