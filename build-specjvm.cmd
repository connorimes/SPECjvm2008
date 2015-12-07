@if "%__ECHO%" NEQ "" (@echo %__ECHO%) else (@echo off)

setlocal

if not defined JAVA_HOME (
  echo Please point the system variable JAVA_HOME to where your jdk is installed.
  goto :the_end
)

if not exist %JAVA_HOME% (
  echo Can not find folder %JAVA_HOME%
  echo Please check your JAVA_HOME variable.
  goto :the_end
)

if exist %JAVA_HOME%\jre\bin\java.exe (
  set JAVA_EXE=%JAVA_HOME%\jre\bin\java.exe
)

if exist %JAVA_HOME%\bin\java.exe (
  set JAVA_EXE=%JAVA_HOME%\bin\java.exe
)

if not defined JAVA_EXE (
  echo Expected to find file %JAVA_HOME%\jre\bin\java.exe or %JAVA_HOME%\bin\java.exe
  echo Please check your JAVA_HOME variable or edit the script to match your JDK.
  goto :the_end
)

if exist %JAVA_HOME%\..\lib\tools.jar (
  set JAVA_TOOLS_JAR=%JAVA_HOME%\..\lib\tools.jar
)

if exist %JAVA_HOME%\lib\tools.jar (
  set JAVA_TOOLS_JAR=%JAVA_HOME%\lib\tools.jar
)

if not defined JAVA_TOOLS_JAR (
  echo Expected to find file %JAVA_HOME%\lib\tools.jar (
  echo Please update your JAVA_HOME variable or point to the tools jar in system variable JAVA_TOOLS_JAR directly.
  goto :the_end
)


%JAVA_EXE% -cp lib\ant.jar;lib\ant-launcher.jar;%JAVA_TOOLS_JAR% org.apache.tools.ant.Main -f build.xml %*

:the_end
endlocal
:end

